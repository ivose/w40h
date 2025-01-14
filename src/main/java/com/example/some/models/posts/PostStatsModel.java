package com.example.some.models.posts;

import lombok.Data;
import java.util.Map;

@Data
public class PostStatsModel {
    private Long postId;
    private int viewCount;
    private int uniqueViewers;
    private int commentCount;
    private Map<String, Integer> reactionCounts;
    private double engagementRate;
}