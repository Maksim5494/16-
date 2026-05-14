package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        log.info("Получен HTTP-запрос по адресу /requests (метод POST). userId={}", userId);
        return itemRequestClient.create(userId, itemRequestRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Получен HTTP-запрос по адресу /requests (метод GET). userId={}", userId);
        return itemRequestClient.findAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @PathVariable Long requestId) {
        log.info("Получен HTTP-запрос по адресу /requests/{} (метод GET). userId={}", requestId, userId);
        return itemRequestClient.findById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllUsersItemRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                                          @RequestParam(defaultValue = "0", required = false) @PositiveOrZero int from,
                                                          @RequestParam(defaultValue = "10", required = false) @Positive int size) {
        log.info("Получен HTTP-запрос по адресу /requests/all (метод GET). userId={}, from={}, size={}",
                userId, from, size);
        return itemRequestClient.findAllByOwner(userId, from, size);
    }
}