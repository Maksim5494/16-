package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingState;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingClient bookingClient;

    public ResponseEntity<Object> findAllByOwnerAndStatus(long userId, String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.findAllByOwnerAndStatus(userId, state);
    }

    public ResponseEntity<Object> findAllByBookerAndStatus(long userId, String stateParam, Integer from, Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.findAllByBookerAndStatus(userId, state, from, size);
    }
}