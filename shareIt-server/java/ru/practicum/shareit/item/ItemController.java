package ru.practicum.shareit.item.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody ItemCreateDto dto) {
        return ResponseEntity.ok(itemService.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getByOwner(userId));
    }
}