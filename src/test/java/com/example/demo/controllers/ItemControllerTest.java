package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

class ItemControllerTest {

    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test items
        item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setPrice(BigDecimal.valueOf(10.99));
        item1.setDescription("Description for Test Item 1");

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setPrice(BigDecimal.valueOf(20.99));
        item2.setDescription("Description for Test Item 2");
    }

    @Test
    void testGetItems() {
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("Test Item 1", response.getBody().get(0).getName());
        assertEquals("Test Item 2", response.getBody().get(1).getName());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testGetItemById_Found() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Item 1", response.getBody().getName());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItemById_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testGetItemsByName_Found() {
        when(itemRepository.findByName("Test Item 1")).thenReturn(Collections.singletonList(item1));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Test Item 1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Item 1", response.getBody().get(0).getName());
        verify(itemRepository, times(1)).findByName("Test Item 1");
    }

    @Test
    void testGetItemsByName_NotFound() {
        when(itemRepository.findByName("Nonexistent Item")).thenReturn(Collections.emptyList());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Nonexistent Item");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(itemRepository, times(1)).findByName("Nonexistent Item");
    }
}
