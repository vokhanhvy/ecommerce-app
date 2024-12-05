package com.example.demo.controllers;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.IntStream;

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
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    private User user;
    private Cart cart;
    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize user, cart, and item
        cart = new Cart();
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setCart(cart);

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setPrice(BigDecimal.valueOf(10.0));
        cart.setUser(user);
    }

    @Test
    void testAddToCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("invalidUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("invalidUser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("invalidUser");
    }

    @Test
    void testAddToCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(99L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(itemRepository, times(1)).findById(99L);
    }

    @Test
    void testAddToCart_Success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(item, response.getBody().getItems().get(0));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void testRemoveFromCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("invalidUser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("invalidUser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("invalidUser");
    }

    @Test
    void testRemoveFromCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(99L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(itemRepository, times(1)).findById(99L);
    }

    @Test
    void testRemoveFromCart_Success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testUser");
        request.setItemId(1L);
        request.setQuantity(1);

        cart.addItem(item);

        when(userRepository.findByUsername("testUser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getItems().size());
        verify(cartRepository, times(1)).save(cart);
    }
}
