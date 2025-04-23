package com.example.ExpenseTrackerAPI.controller;

import com.example.ExpenseTrackerAPI.model.Expense;
import com.example.ExpenseTrackerAPI.model.MonthlyExpenseReport;
import com.example.ExpenseTrackerAPI.model.User;
import com.example.ExpenseTrackerAPI.service.ExpenseService;
import com.example.ExpenseTrackerAPI.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense) {
        return ResponseEntity.ok(expenseService.createExpense(expense));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Expense>> getUserExpenses(@Valid @PathVariable Long userId, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        if (!user.getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed to access this data.");
        }
        return ResponseEntity.ok(expenseService.getExpensesByUser(userId));
    }

    @GetMapping("/total/{userId}")
    public ResponseEntity<Double> getTotalExpense(@Valid @PathVariable Long userId,
                                                  @RequestParam LocalDate startDate,
                                                  @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(expenseService.calculateTotalExpense(userId, startDate, endDate));
    }

    @GetMapping("/category/{userId}")
    public ResponseEntity<Map<String, Double>> getCategoryWiseExpense(@Valid @PathVariable Long userId) {
        return ResponseEntity.ok(expenseService.getCategoryWiseExpense(userId));
    }

    @GetMapping("/monthly/{userId}")
    public ResponseEntity<MonthlyExpenseReport> getMonthlyExpense(@Valid @PathVariable Long userId) {
        MonthlyExpenseReport report =  expenseService.getMonthlyExpenseReport(userId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Expense>> getExpensesByDateRange(
            @Valid @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(userId, startDate, endDate));
    }

}
