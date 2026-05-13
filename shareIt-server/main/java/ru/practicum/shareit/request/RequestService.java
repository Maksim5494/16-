package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface itemRequestService {
    RequestDto create(Long userId, RequestCreateDto dto);
    List<RequestDto> getAllByUser(Long userId);
}