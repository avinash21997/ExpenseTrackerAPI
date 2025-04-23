package com.example.ExpenseTrackerAPI;

import com.example.ExpenseTrackerAPI.exceptions.UserAlreadyExistsException;
import com.example.ExpenseTrackerAPI.model.User;
import com.example.ExpenseTrackerAPI.repository.UserRepository;
import com.example.ExpenseTrackerAPI.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setActive(true);
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User registeredUser = userService.registerUser(user);
        assertNotNull(registeredUser);
        assertEquals("John Doe", registeredUser.getName());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void registerUser_UserAlreadyExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(user);
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User loggedInUser = userService.login(user.getEmail());
        assertNotNull(loggedInUser);
        assertEquals("john@example.com", loggedInUser.getEmail());
    }

    @Test
    void login_UserNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.login("notfound@example.com"));
    }

}
