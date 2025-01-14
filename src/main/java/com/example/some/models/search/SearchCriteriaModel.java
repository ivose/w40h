package com.example.some.models.search;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchCriteriaModel {
    private String searchTerm;
    private List<String> contentTypes;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private List<String> tags;
    private List<Long> userIds;
    private boolean includeInactive;
    private String sortBy;
    private String sortDirection;
}