package com.example.some.models.analytics;

import lombok.Data;
import java.util.Map;

@Data
public class PostAnalyticsModel {
    private Long postId;
    private Map<String, Integer> hourlyViews;
    private Map<String, Integer> reactionDistribution;
    private double viralityScore;
    private Map<String, Double> demographicBreakdown;
    private Map<String, Integer> referralSources;
}