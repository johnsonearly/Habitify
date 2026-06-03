package com.johnson.habit.service.serviceImpl;

import com.johnson.habit.dto.GroupMembersDTO;
import com.johnson.habit.entity.GroupMember;
import com.johnson.habit.entity.GroupTable;
import com.johnson.habit.entity.Habit;
import com.johnson.habit.entity.UserEntity;
import com.johnson.habit.entity.enums.HabitCategory;
import com.johnson.habit.repository.GroupTableRepository;
import com.johnson.habit.response.exception.DefaultErrorException;
import com.johnson.habit.service.GroupTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupTableServiceImpl implements GroupTableService {
    private final GroupTableRepository groupTableRepository;


    @Override
    public void createGroup(GroupTable group) {
        try{
            groupTableRepository.save(group);
        }catch (Exception e){
            throw new DefaultErrorException(e.getMessage());
        }

    }

    @Override
    public void updateGroup(GroupTable group) {
        Optional<GroupTable> groupTable = groupTableRepository.findById(group.getId());
        if (groupTable.isPresent()) {
            groupTableRepository.save(group);
        }

    }

    @Override
    public void deleteGroup(UUID id) {
        Optional<GroupTable> groupTable = groupTableRepository.findById(id);
        groupTable.ifPresent(groupTableRepository::delete);

    }

    public void findGroupByHabitCategory(Habit habit, UserEntity userEntity) {
        log.info("Inside  findGroupByHabitCategory ServiceImpl");
        try {
            HabitCategory habitCategory = habit.getCategory();
            Optional<GroupTable> groupTable = groupTableRepository.findByCategory(habitCategory);
            GroupMember groupMember = new GroupMember();
            groupMember.setUser(userEntity);
            groupMember.setHabit(habit.getDescription());
            if (groupTable.isPresent()) {
                groupMember.setGroup(groupTable.get());
                groupTable.get().getMembers().add(groupMember);
                groupTable.get().setNoMembers(groupTable.get().getNoMembers() + 1);
                createGroup(groupTable.get());
                log.info("Saved present groupTable");

            } else {
                GroupTable table = new GroupTable();
                groupMember.setGroup(table);
                table.getMembers().add(groupMember);
                table.setCategory(habitCategory);
                table.setNoMembers(1);
                table.setName(habitCategory.name() + "squad");
               createGroup(groupTable.get());
                log.info("Saved new groupTable");


            }
        }catch (RuntimeException e){
            throw new DefaultErrorException(e.getMessage());
        }
    }

    public List<GroupMembersDTO> fetchGroupTableByUsernameAndHabitCategory(
            String username,
            HabitCategory category
    ) {
        List<String> usernames = List.of("John", "Jason", "Debbie", "Sharon");

        GroupMembersDTO membersDTO = new GroupMembersDTO();
        Random rand = new Random();
        List<GroupMembersDTO> groupMembersDto = new ArrayList<>();
        try {
            GroupTable table = groupTableRepository
                    .findByCategoryAndMembers_User_Username(category, username)
                    .orElseThrow(() -> new RuntimeException("Group not found"));
            List<GroupMember> groupMembers = table.getMembers();
            for (int i = 0; i < 4; i++) {
                if (groupMembers.get(i) != null) {
                    membersDTO.setName(groupMembers.get(i).getUser().getUsername());
                    membersDTO.setHabit(groupMembers.get(i).getHabit());
                    groupMembersDto.add(membersDTO);
                }


                membersDTO.setName(usernames.get(rand.nextInt(usernames.size())));
                membersDTO.setHabit(groupMembers.getFirst().getHabit());
                groupMembersDto.add(membersDTO);
            }
        }catch (Exception e) {
            log.error("Error in fetch groupTableByUsernameAndHabitCategory");
            throw new DefaultErrorException(e.getMessage());
        }
        return groupMembersDto;
    }
}
