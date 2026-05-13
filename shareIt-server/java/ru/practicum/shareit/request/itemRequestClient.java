package ru.practicum.shareit.request.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@Component
public class itemRequestClient {

    private final RestTemplate restTemplate;

    public RequestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RequestDto create(Long userId, RequestCreateDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<RequestCreateDto> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<RequestDto> response = restTemplate.exchange(
                "http://server/requests",
                HttpMethod.POST,
                entity,
                RequestDto.class
        );
        return response.getBody();
    }

    public List<RequestDto> getAllByUser(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<RequestDto>> response = restTemplate.exchange(
                "http://server/requests",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<RequestDto>>() {}
        );
        return response.getBody();
    }
}