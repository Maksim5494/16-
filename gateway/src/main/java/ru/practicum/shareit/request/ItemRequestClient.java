package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import java.util.Map;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> findAll(long userId) {
        log.info("Отправка GET запроса на /requests. userId={}", userId);
        return get("", userId);
    }

    public ResponseEntity<Object> create(long userId, ItemRequestRequestDto itemRequestRequestDto) {
        log.info("Отправка POST запроса на /requests. userId={}, requestDto={}", userId, itemRequestRequestDto);
        return post("", userId, itemRequestRequestDto);
    }

    public ResponseEntity<Object> findById(long userId, Long requestId) {
        log.info("Отправка GET запроса на /requests/{}. userId={}", requestId, userId);
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAllByOwner(long userId, Integer from, Integer size) {
        log.info("Отправка GET запроса на /requests/all. userId={}, from={}, size={}", userId, from, size);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}