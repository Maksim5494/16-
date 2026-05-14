package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Component
public class BookingClient extends BaseClient {

    private static final String BOOKINGS_PATH = "/bookings";
    private static final String OWNER_PATH = "/owner";
    private static final String STATE_PARAM = "state";
    private static final String FROM_PARAM = "from";
    private static final String SIZE_PARAM = "size";
    private static final String APPROVED_PARAM = "approved";
    private static final String SLASH = "/";

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return post(BOOKINGS_PATH, userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get(BOOKINGS_PATH + SLASH + bookingId, userId);
    }

    public ResponseEntity<Object> findAllByBookerAndStatus(long userId, BookingState state, Integer from, Integer size) {
        return get(BOOKINGS_PATH + "?" + STATE_PARAM + "=" + state.name()
                + "&" + FROM_PARAM + "=" + from
                + "&" + SIZE_PARAM + "=" + size, userId);
    }

    public ResponseEntity<Object> findAllByOwnerAndStatus(long userId, BookingState state) {
        return get(BOOKINGS_PATH + OWNER_PATH + "?" + STATE_PARAM + "=" + state.name(), userId);
    }

    public ResponseEntity<Object> setApproved(long userId, Long bookingId, boolean approved) {
        return patch(BOOKINGS_PATH + SLASH + bookingId + "?" + APPROVED_PARAM + "=" + approved, userId);
    }
}