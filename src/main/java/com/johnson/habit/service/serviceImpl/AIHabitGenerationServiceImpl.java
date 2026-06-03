package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.auth.util.JwtUtil;
import com.johnson.habit.dto.GoalProfileDTO;
import com.johnson.habit.entity.Habit;
import com.johnson.habit.entity.Milestone;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.enums.HabitCategory;
import com.johnson.habit.repository.HabitRepository;
import com.johnson.habit.repository.MileStoneRepository;
import com.johnson.habit.response.SuccessResponse;
import com.johnson.habit.response.exception.DefaultErrorException;
import com.johnson.habit.service.AIHabitGenerationService;
import com.johnson.habit.service.GroupTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class AIHabitGenerationServiceImpl implements AIHabitGenerationService {

    private final WebClient webClient;
    private final HabitRepository habitRepository;
    private final MileStoneRepository milestoneRepository;
    private final ObjectMapper objectMapper;
    private final UserServiceImpl userService;
    private final GroupTableService groupTableService;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Override
    public SuccessResponse<Habit> generateHabitForUser(String authHeader, GoalProfileDTO profile) {
        SuccessResponse<Habit> successResponse = new SuccessResponse<>();
        String token = "";
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                 token = authHeader.substring(7);
            }
            String username = JwtUtil.extractUsername(token);
            UserEntity user = userService.getUserProfileByUsername(username).getData();
            String prompt = buildHabitPrompt(profile);
            String response = callGeminiAPI(prompt);
            successResponse.setStatusCode(200);
            successResponse.setMessage(response);
            successResponse.setData(parseAndSaveHabit(user, response));
        }catch (RuntimeException e){
            successResponse.setStatusCode(500);
            successResponse.setMessage(e.getMessage());
            throw new DefaultErrorException(e.getMessage());
        }
        return successResponse;
    }

    private String buildHabitPrompt(GoalProfileDTO profile) {
        return """
            You are a habit coach creating a personalized progressive habit plan.
            Based on this person's profile, generate a main habit broken into
            progressive sub habits (milestones) that gradually increase in intensity.
            
            Profile:
            - Goal: %s
            
            Rules:
            - Create 3 to 4 milestones that progressively build on each other
            - Start easy to build confidence, increase gradually
            - Each milestone should have a clear daily target with a unit
            - Total target days should be 90 days
            
            Respond ONLY in raw JSON, no markdown, no backticks:
            {
                "habitTitle": "short title for the main habit",
                "habitDescription": "one sentence describing the overall goal",
                "category": "one of: HEALTH_FITNESS, MENTAL_WELLNESS, LEARNING_EDUCATION, CREATIVITY, PRODUCTIVITY, FINANCE, RELATIONSHIPS, SPIRITUALITY, NUTRITION, LIFESTYLE",
                "milestones": [
                    {
                        "title": "milestone title",
                        "description": "what they do daily in this phase",
                        "dailyTarget": a number,
                        "targetUnit": "unit of measurement e.g pages, minutes, words",
                        "startDay": 1,
                        "endDay": 14
                    }
                ]
            }
            """.formatted(
                profile.getRawGoalDescription()
        );
    }

    private String callGeminiAPI(String prompt) {
        try {
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));

            return webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    // Safely catch HTTP error status codes (4xx, 5xx) from the server
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                System.err.println("Gemini API Server Error Body: " + errorBody);
                                return Mono.error(new RuntimeException("Gemini API Error: " + clientResponse.statusCode()));
                            })
                    )
                    .bodyToMono(Map.class)
                    .map(res -> {
                        try {
                            List<Map> candidates = (List<Map>) res.get("candidates");
                            if (candidates == null || candidates.isEmpty()) {
                                throw new RuntimeException("No candidates found in Gemini response. Full response: " + res);
                            }

                            Map firstCandidate = candidates.getFirst();
                            Map contentMap = (Map) firstCandidate.get("content");
                            if (contentMap == null) {
                                throw new RuntimeException("Content map missing from candidate structure.");
                            }

                            List<Map> parts = (List<Map>) contentMap.get("parts");
                            if (parts == null || parts.isEmpty()) {
                                throw new RuntimeException("Parts missing from response content.");
                            }

                            return (String) parts.get(0).get("text");
                        } catch (Exception parseException) {
                            System.err.println("Error parsing JSON map response structure: " + res);
                            throw parseException;
                        }
                    })
                    .block();

        } catch (org.springframework.web.reactive.function.client.WebClientResponseException e) {
            System.err.println("WebClient HTTP Error status: " + e.getStatusCode());
            System.err.println("WebClient Response Body: " + e.getResponseBodyAsString());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("General Exception occurred in callGeminiAPI: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private Habit parseAndSaveHabit(UserEntity user,
                                    String jsonResponse) {
        try {
            String clean = jsonResponse
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode node = objectMapper.readTree(clean);
            Habit habit = new Habit();
            habit.setUser(user);
            habit.setTitle(node.get("habitTitle").asText());
            habit.setDescription(node.get("habitDescription").asText());
            habit.setCategory(
                    HabitCategory.valueOf(node.get("category").asText()));
            habit.setTargetDays(90);
            habit.setCurrentStreak(0);
            habit.setLongestStreak(0);
            habit.setActive(true);


            // create and save each milestone
            JsonNode milestonesNode = node.get("milestones");
            boolean isFirst = true;
            List<Milestone> milestones = new ArrayList<>();
            for (JsonNode m : milestonesNode) {
                Milestone milestone = new Milestone();
                milestone.setHabit(habit);
                milestone.setTitle(m.get("title").asText());
                milestone.setDescription(m.get("description").asText());
                milestone.setDailyTarget(m.get("dailyTarget").asInt());
                milestone.setTargetUnit(m.get("targetUnit").asText());
                milestone.setStartDay(m.get("startDay").asInt());
                milestone.setEndDay(m.get("endDay").asInt());
                milestone.setCompleted(false);
                milestone.setActive(isFirst);
                milestoneRepository.save(milestone);
                milestones.add(milestone);

                isFirst = false;
            }
            habit.setMilestones(milestones);
            habitRepository.save(habit);
            groupTableService.findGroupByHabitCategory(habit, user);
            return habit;

        } catch (Exception e) {
            throw new DefaultErrorException("Failed to parse habit from Gemini: " + e.getMessage());
        }
    }

}

