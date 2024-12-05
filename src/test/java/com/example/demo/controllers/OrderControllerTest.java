package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Cart cart;
    private Item item;
    private UserOrder userOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize user, cart, item, and order
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.99));
        cart.setItems(Arrays.asList(item));
        cart.setTotal(BigDecimal.valueOf(10.99));
        user.setCart(cart);

        userOrder = UserOrder.createFromCart(cart);
    }

    @Test
    void testSubmitOrder_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userOrder.getItems().size(), response.getBody().getItems().size());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    void testSubmitOrder_UserNotFound() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("nonexistentUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    void testGetOrdersForUser_Success() {
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(userOrder));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    void testGetOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonexistentUser");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("nonexistentUser");
        verify(orderRepository, never()).findByUser(any(User.class));
    }
}
