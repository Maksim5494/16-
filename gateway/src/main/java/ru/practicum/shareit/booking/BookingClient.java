package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";
    private static final String GET_BOOKINGS_TEMPLATE = "?state={state}&from={from}&size={size}";
    private static final String GET_OWNER_BOOKINGS_TEMPLATE = "/owner?state={state}";
    private static final String APPROVE_BOOKING_PATH = "/%d?approved=%s";

    private static final String STATE_PARAM = "state";
    private static final String FROM_PARAM = "from";
    private static final String SIZE_PARAM = "size";

    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .requestFactory(settings -> new HttpComponentsClientHttpRequestFactory())
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> create(long userId, BookItemRequestDto requestDto) {
        return post(API_PREFIX, userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get(API_PREFIX + "/" + bookingId, userId);
    }
    public ResponseEntity<Object> getBookings(long userId, String state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                STATE_PARAM, state,
                FROM_PARAM, from,
                SIZE_PARAM, size
        );
        return get(API_PREFIX + GET_BOOKINGS_TEMPLATE, userId, parameters);
    }

    public ResponseEntity<Object> findAllByOwnerAndStatus(long userId, String state) {
        Map<String, Object> parameters = Map.of(
                STATE_PARAM, state
        );
        return get(API_PREFIX + GET_OWNER_BOOKINGS_TEMPLATE, userId, parameters);
    }

    public ResponseEntity<Object> setApproved(long userId, Long bookingId, boolean approved) {
        return patch(API_PREFIX + String.format(APPROVE_BOOKING_PATH, bookingId, approved), userId);
    }
}