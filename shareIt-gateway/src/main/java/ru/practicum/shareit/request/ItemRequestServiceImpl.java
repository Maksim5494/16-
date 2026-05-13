package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(user);

        return toDto(requestRepository.save(request));
    }

    public List<ItemRequestDto> getOwnRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        // Тут также нужно мапить список Items, если они есть
        return dto;
    }
}