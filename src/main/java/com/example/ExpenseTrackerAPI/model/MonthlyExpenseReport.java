package com.example.ExpenseTrackerAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyExpenseReport {

    private double total;
    private Map<String, Double> categoryBreakdown;

}
