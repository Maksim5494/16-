package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> findAllByBookerAndStatus(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "all") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.findAllByBookerAndStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwnerAndStatus(
            @RequestHeader(REQUEST_HEADER) Long userId,
            @RequestParam(name = "state", defaultValue = "all") String state) {
        return bookingService.findAllByOwnerAndStatus(userId, state);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApproved(@RequestHeader(REQUEST_HEADER) Long userId,
                                              @PathVariable Long bookingId,
                                              @RequestParam Boolean approved) {
        return bookingClient.setApproved(userId, bookingId, approved);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(REQUEST_HEADER) Long userId,
                                           @Valid @RequestBody BookItemRequestDto requestDto) {
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(REQUEST_HEADER) Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }
}