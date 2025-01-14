package com.example.some.models.analytics;

import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
public class EngagementMetricsModel {
    private LocalDate date;
    private int activeUsers;
    private double averageSessionDuration;
    private int newUsers;
    private Map<String, Integer> userActions;
    private double retentionRate;
    private Map<String, Double> contentPerformance;
}