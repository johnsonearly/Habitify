package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.UserGoalProfile;
import com.johnson.habit.entity.enums.GoalType;
import com.johnson.habit.entity.enums.PersonalitySignal;
import com.johnson.habit.entity.enums.UrgencyLevel;
import com.johnson.habit.repository.UserGoalProfileRepository;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.response.exception.DefaultErrorException;
import com.johnson.habit.service.AIGroupingService;
import com.johnson.habit.service.GroupMatchingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIGroupingServiceImpl implements AIGroupingService {
    private  final WebClient webClient;
    private final UserGoalProfileRepository profileRepository;
    private final GroupMatchingService groupMatchingService;
    private final ObjectMapper objectMapper;
    private final UserServiceImpl userService;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Transactional
    @Override
    public SuccessResponse processNewUser(String username, String goalDescription) {
        SuccessResponse successResponse = new SuccessResponse();
        try {
            UserEntity user = userService.getUserProfileByUsername(username).getData();
            UserGoalProfile profile = extractGoalProfile(user, goalDescription);
            profileRepository.save(profile);
            groupMatchingService.attemptSquadMatch(user, profile);
            successResponse.setStatusCode(200);
            successResponse.setMessage("Successfully updated group profile");
        }catch (Exception e){
            successResponse.setStatusCode(500);
            successResponse.setMessage("Error updating group profile");
            throw new DefaultErrorException("Error updating group profile " + e.getMessage());
        }

        return successResponse;
    }
    private UserGoalProfile extractGoalProfile(UserEntity user, String goalDescription) {
        String prompt = """
            You are analyzing a person's personal goal or habit they want to build or break.
            Extract the following and respond ONLY in raw JSON, no markdown, no backticks:
            {
                "goalArea": "one or two word label summarising their goal e.g. reading, fitness, consistency, photography",
                "urgencyLevel": "HIGH, MEDIUM or LOW based on how urgent they seem",
                "hoursPerDayAvailable": an integer representing how many hours they can commit daily,
                "goalType": "BUILD_HABIT, BREAK_HABIT, IMPROVE_CONSISTENCY or ACHIEVE_MILESTONE",
                "personalitySignal": "COMPETITIVE, COLLABORATIVE, INDEPENDENT or STRUGGLING"
            }
            Person's goal: "%s"
            """.formatted(goalDescription);

        String response = callGeminiAPI(prompt);
        return parseGoalProfile(user, goalDescription, response);
    }

    private String callGeminiAPI(String prompt) {
        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        return webClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> {
                    List<Map> candidates = (List<Map>) res.get("candidates");
                    Map firstCandidate = candidates.getFirst();
                    Map contentMap = (Map) firstCandidate.get("content");
                    List<Map> parts = (List<Map>) contentMap.get("parts");
                    return (String) parts.get(0).get("text");
                })
                .block();
    }

    private UserGoalProfile parseGoalProfile(UserEntity user,
                                             String rawGoal,
                                             String jsonResponse) {
        try {
            String clean = jsonResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode node = objectMapper.readTree(clean);

            UserGoalProfile profile = new UserGoalProfile();
            profile.setUser(user);
            profile.setRawGoalDescription(rawGoal);
            profile.setGoalArea(node.get("goalArea").asText());
            profile.setUrgencyLevel(
                    UrgencyLevel.valueOf(node.get("urgencyLevel").asText()));
            profile.setHoursPerDayAvailable(
                    node.get("hoursPerDayAvailable").asInt());
            profile.setGoalType(
                    GoalType.valueOf(node.get("goalType").asText()));
            profile.setPersonalitySignal(
                    PersonalitySignal.valueOf(node.get("personalitySignal").asText()));
            profile.setMatched(false);
            return profile;

        } catch (Exception e) {
            throw new DefaultErrorException("Failed to parse Gemini response: " + e.getMessage());
        }
    }
}
