package com.example.ExpenseTrackerAPI;

import com.example.ExpenseTrackerAPI.exceptions.InvalidRequestException;
import com.example.ExpenseTrackerAPI.exceptions.ResourceNotFoundException;
import com.example.ExpenseTrackerAPI.model.Expense;
import com.example.ExpenseTrackerAPI.model.User;
import com.example.ExpenseTrackerAPI.repository.ExpenseRepository;
import com.example.ExpenseTrackerAPI.repository.UserRepository;
import com.example.ExpenseTrackerAPI.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;
    private Expense expense;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setActive(true);

        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(100.0);
        expense.setCategory("Food");
        expense.setDate(LocalDate.now());
        expense.setUser(user);
    }

    @Test
    void testCreateExpense_Success() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(expenseRepository.save(expense)).thenReturn(expense);

        Expense savedExpense = expenseService.createExpense(expense);
        assertNotNull(savedExpense);
        assertEquals(100.0, savedExpense.getAmount());
    }

    @Test
    void testCreateExpense_InvalidAmount() {
        expense.setAmount(0.0);
        Exception exception = assertThrows(InvalidRequestException.class, () -> expenseService.createExpense(expense));
        assertEquals("Amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void testCreateExpense_MissingCategory() {
        expense.setCategory("");
        Exception exception = assertThrows(InvalidRequestException.class, () -> expenseService.createExpense(expense));
        assertEquals("Category is required.", exception.getMessage());
    }

    @Test
    void testCreateExpense_UserNotFound() {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> expenseService.createExpense(expense));
        assertTrue(exception.getMessage().contains("User not found with ID"));
    }

    @Test
    void testGetExpensesByUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(expenseRepository.findByUserId(1L)).thenReturn(Collections.singletonList(expense));

        List<Expense> expenses = expenseService.getExpensesByUser(1L);
        assertEquals(1, expenses.size());
    }

    @Test
    void testGetExpensesByDateRange_Success() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(expenseRepository.findByUserIdAndDateBetween(1L, start, end))
                .thenReturn(Collections.singletonList(expense));

        List<Expense> result = expenseService.getExpensesByDateRange(1L, start, end);
        assertEquals(1, result.size());
    }

    @Test
    void testGetExpensesByDateRange_InvalidDateRange() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().minusDays(5);
        when(userRepository.existsById(1L)).thenReturn(true);

        Exception exception = assertThrows(InvalidRequestException.class,
                () -> expenseService.getExpensesByDateRange(1L, start, end));
        assertEquals("Start date must be before end date.", exception.getMessage());
    }

    @Test
    void testCalculateTotalExpense() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        when(userRepository.existsById(1L)).thenReturn(true);
        when(expenseRepository.findByUserIdAndDateBetween(1L, start, end))
                .thenReturn(Collections.singletonList(expense));

        double total = expenseService.calculateTotalExpense(1L, start, end);
        assertEquals(100.0, total);
    }

    @Test
    void testGetCategoryWiseExpense_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(expenseRepository.findByUserId(1L)).thenReturn(Collections.singletonList(expense));

        Map<String, Double> categoryWise = expenseService.getCategoryWiseExpense(1L);
        assertEquals(1, categoryWise.size());
        assertEquals(100.0, categoryWise.get("Food"));
    }

    @Test
    void testGetCategoryWiseExpense_NoExpenses() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(expenseRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> expenseService.getCategoryWiseExpense(1L));
        assertTrue(exception.getMessage().contains("No expenses found for user with ID"));
    }

}
