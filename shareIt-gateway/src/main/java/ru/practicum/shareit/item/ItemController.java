package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemCreateDto dto) {
        return ResponseEntity.ok(itemClient.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemClient.getByOwner(userId));
    }
}
