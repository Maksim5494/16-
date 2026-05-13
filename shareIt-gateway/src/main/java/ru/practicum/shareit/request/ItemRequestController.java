package ru.practicum.shareit.request.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")

public class ItemRequestController {
    private final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<RequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody RequestCreateDto dto) {
        return ResponseEntity.ok(requestClient.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestClient.getAllByUser(userId));
    }
}