package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemRequestService(ItemRequestRepository requestRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequester(user);

        ItemRequest saved = requestRepository.save(request);
        return toDto(saved);
    }

    public List<ItemRequestDto> getOwnRequests(Long userId) {
        return requestRepository.findByRequesterIdOrderByCreatedDesc(userId)
                .stream()
                .map(this::toDtoWithItems)
                .toList();
    }

    public List<ItemRequestDto> getAllRequests(Long userId) {
        return requestRepository.findByRequesterIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(this::toDtoWithItems)
                .toList();
    }

    public ItemRequestDto getById(Long requestId) {
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        return toDtoWithItems(request);
    }

    private ItemRequestDto toDtoWithItems(ItemRequest request) {
        ItemRequestDto dto = toDto(request);

        List<ItemDto> items = itemRepository.findByRequestId(request.getId()).stream()
                .map(this::toItemDto)
                .toList();

        dto.setItems(items);
        return dto;
    }

    private ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    private ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        }
        return dto;
    }
}