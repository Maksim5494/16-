package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        return itemRequestClient.create(userId, itemRequestRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(REQUEST_HEADER) Long userId) {
        return itemRequestClient.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader(REQUEST_HEADER) Long userId,
                                                      @PathVariable Long requestId) {
        return itemRequestClient.findItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllUsersItemRequest(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.findAllUsersItemRequest(userId, from, size);
    }
}