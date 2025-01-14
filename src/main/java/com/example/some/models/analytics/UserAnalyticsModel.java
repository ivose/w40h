package com.example.some.models.analytics;

import lombok.Data;
import java.util.Map;

@Data
public class UserAnalyticsModel {
    private Long userId;
    private Map<String, Integer> dailyActivity;
    private Map<String, Double> engagementRates;
    private Map<String, Integer> contentDistribution;
    private double averageResponseTime;
}