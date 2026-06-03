package com.johnson.habit.entity;

import com.johnson.habit.entity.enums.GroupCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "group_member")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupTable group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String habit;



}
