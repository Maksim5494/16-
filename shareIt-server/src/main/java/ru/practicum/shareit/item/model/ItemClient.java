package ru.practicum.shareit.item.model;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Component
public class ItemClient {

    private final RestTemplate restTemplate;

    public ItemClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ItemDto create(Long userId, ItemCreateDto dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<ItemCreateDto> entity = new HttpEntity<>(dto, headers);

        ResponseEntity<ItemDto> response = restTemplate.exchange(
                "http://server/items",
                HttpMethod.POST,
                entity,
                ItemDto.class
        );
        return response.getBody();
    }

    public List<ItemDto> getByOwner(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Sharer-User-Id", String.valueOf(userId));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ItemDto>> response = restTemplate.exchange(
                "http://server/items",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<ItemDto>>() {}
        );
        return response.getBody();
    }
}