package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize User and Cart
        cart = new Cart();
        cart.setId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setCart(cart);
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testUser", response.getBody().getUsername());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.findById(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void testFindByUserName_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testFindByUserName_NotFound() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("nonexistentUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
    }

    @Test
    void testCreateUser_Success() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("newUser");

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newUser", response.getBody().getUsername());
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(userRepository, times(1)).save(any(User.class));
    }
}
