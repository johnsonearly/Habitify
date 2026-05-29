package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.entity.GroupMember;
import com.johnson.habit.entity.GroupTable;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.UserGoalProfile;
import com.johnson.habit.repository.GroupMemberRepository;
import com.johnson.habit.repository.GroupTableRepository;
import com.johnson.habit.repository.UserGoalProfileRepository;
import com.johnson.habit.repository.UserRepository;
import com.johnson.habit.service.GroupMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupMatchingServiceImpl  implements GroupMatchingService {

    private final UserGoalProfileRepository profileRepository;
    private final GroupTableRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;


    @Override
    public void attemptSquadMatch(UserEntity user, UserGoalProfile newProfile) {
        List<UserGoalProfile> pool = profileRepository
                .findByIsMatchedFalseAndUserNot(user);

        if (pool.size() < 3) {
            // not enough users yet, stay in waiting pool
            return;
        }

        List<UserGoalProfile> bestMatches = pool.stream()
                .sorted(Comparator.comparingDouble(
                        p -> -calculateCompatibilityScore(newProfile, p)))
                .limit(3)
                .toList();

        createSquad(user, newProfile, bestMatches);
    }

    private double calculateCompatibilityScore(UserGoalProfile a, UserGoalProfile b) {
        double score = 0;

        // same goal area is most important
        if (a.getGoalArea().equalsIgnoreCase(b.getGoalArea())) score += 40;

        // same urgency level
        if (a.getUrgencyLevel() == b.getUrgencyLevel()) score += 20;

        // similar hours available
        int hourDiff = Math.abs(a.getHoursPerDayAvailable() - b.getHoursPerDayAvailable());
        if (hourDiff == 0) score += 20;
        else if (hourDiff == 1) score += 10;

        // different personality signals complement each other
        if (a.getPersonalitySignal() != b.getPersonalitySignal()) score += 20;

        return score;
    }

    private void createSquad(UserEntity newUser,
                             UserGoalProfile newProfile,
                             List<UserGoalProfile> matches) {
        GroupTable squad = new GroupTable();
        squad.setName(generateSquadName(newProfile.getGoalArea()));
        squad.setDescription("Auto matched group for " + newProfile.getGoalArea());
        squad.setCreatedBy(newUser);
        squad.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        groupRepository.save(squad);

        addToSquad(newUser, squad);
        newProfile.setMatched(true);
        profileRepository.save(newProfile);

        for (UserGoalProfile match : matches) {
            addToSquad(match.getUser(), squad);
            match.setMatched(true);
            profileRepository.save(match);
        }
    }

    private void addToSquad(UserEntity user, GroupTable squad) {
        GroupMember member = new GroupMember();
        member.setUser(user);
        member.setGroup(squad);
        groupMemberRepository.save(member);
    }

    private String generateSquadName(String goalArea) {
        String[] adjectives = {"focused", "driven", "sharp", "relentless", "elite"};
        String adj = adjectives[new Random().nextInt(adjectives.length)];
        return goalArea + " " + adj + " squad";
    }


}
