package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public RequestDto create(Long userId, RequestCreateDto dto) {
        Request request = new Request();
        request.setRequesterId(userId);
        request.setDescription(dto.getDescription());
        request.setCreated(LocalDateTime.now());
        return RequestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getAllByUser(Long userId) {
        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }
}