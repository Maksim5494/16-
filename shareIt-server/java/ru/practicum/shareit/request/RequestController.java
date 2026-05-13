package ru.practicum.shareit.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<RequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody RequestCreateDto dto) {
        return ResponseEntity.ok(requestService.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getAllByUser(userId));
    }
}