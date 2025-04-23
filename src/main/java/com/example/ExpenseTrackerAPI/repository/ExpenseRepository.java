package com.example.ExpenseTrackerAPI.repository;

import com.example.ExpenseTrackerAPI.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {

    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

}
