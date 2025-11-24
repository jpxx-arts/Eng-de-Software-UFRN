package com.umbrella.tomaladaka.controller;
import com.umbrella.tomaladaka.model.Item;
import com.umbrella.tomaladaka.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;

@RestController
@RequestMapping("/items")
@Tag(name = "Item Controller", description = "Manage food items")
public class ItemController {
    private final ItemService itemService;
    public ItemController(ItemService itemService) { this.itemService = itemService; }
    @PostMapping
    @Operation(summary = "Create a new item", description = "Create a new food item")
    @ApiResponse(responseCode = "200", description = "Item created successfully")
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        return ResponseEntity.ok(itemService.createItem(item));
    }
    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieve a list of all food items")
    @ApiResponse(responseCode = "200", description = "List of items retrieved")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }
}