package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

public class itemRequestMapper {

    public static RequestDto toDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static Request toEntity(RequestDto dto) {
        Request request = new Request();
        request.setId(dto.getId());
        request.setDescription(dto.getDescription());
        request.setCreated(dto.getCreated());
        return request;
    }
}