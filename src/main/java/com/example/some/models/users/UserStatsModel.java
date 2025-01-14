package com.example.some.models.users;

import lombok.Data;

@Data
public class UserStatsModel {
    private Long userId;
    private int totalPosts;
    private int totalFollowers;
    private int totalFollowing;
    private int totalReactionsGiven;
    private int totalReactionsReceived;
    private int totalComments;
}