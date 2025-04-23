package com.example.ExpenseTrackerAPI.service;

import com.example.ExpenseTrackerAPI.exceptions.InvalidRequestException;
import com.example.ExpenseTrackerAPI.exceptions.ResourceNotFoundException;
import com.example.ExpenseTrackerAPI.model.Expense;
import com.example.ExpenseTrackerAPI.model.MonthlyExpenseReport;
import com.example.ExpenseTrackerAPI.repository.ExpenseRepository;
import com.example.ExpenseTrackerAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public Expense createExpense(Expense expense) {
        if (expense.getAmount() <= 0) {
            throw new InvalidRequestException("Amount must be greater than zero.");
        }
        if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
            throw new InvalidRequestException("Category is required.");
        }
        if (!userRepository.existsById(expense.getUser().getId())) {
            throw new ResourceNotFoundException("User not found with ID: " + expense.getUser().getId());
        }
        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByUser(Long userId) {
        validateUserExists(userId);
        return expenseRepository.findByUserId(userId);
    }

    public List<Expense> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        validateUserExists(userId);
        if (startDate.isAfter(endDate)) {
            throw new InvalidRequestException("Start date must be before end date.");
        }
        return expenseRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    public double calculateTotalExpense(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Expense> expenses = getExpensesByDateRange(userId, startDate, endDate);
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public Map<String, Double> getCategoryWiseExpense(Long userId) {
        List<Expense> expenses = getExpensesByUser(userId);
        if (expenses.isEmpty()) {
            throw new ResourceNotFoundException("No expenses found for user with ID: " + userId);
        }
        return expenses.stream()
                .filter(expense -> expense.getCategory() != null)
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));
    }

    public MonthlyExpenseReport getMonthlyExpenseReport(Long userId) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Expense> expenses = getExpensesByDateRange(userId, startOfMonth, endOfMonth);

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        Map<String, Double> categoryBreakdown = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        return new MonthlyExpenseReport(total, categoryBreakdown);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }

}
