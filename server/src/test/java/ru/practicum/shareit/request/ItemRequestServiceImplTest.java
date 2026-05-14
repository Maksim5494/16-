package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    private ItemRequestService itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestRequestDto itemRequestRequestDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        itemRequestService = new ItemRequestServiceImpl(
                itemRequestRepository,
                userRepository
        );

        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.now())
                .description("TestItemRequestDescription")
                .build();

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestRequestDto = new ItemRequestRequestDto(user.getId(), "TestItemRequestText");

        pageable = PageRequest.of(0, 10, Sort.by("created").descending());

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestRepository.findByIdOrderByCreatedAsc(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(itemRequest));

        final int start = (int) pageable.getOffset();
        final int end = Math.min(start + pageable.getPageSize(), List.of(itemRequest).size());
        Page<ItemRequest> page = new PageImpl<>(
                List.of(itemRequest).subList(start, end),
                pageable,
                List.of(itemRequest).size()
        );
        when(itemRequestRepository.findAll((Pageable) any())).thenReturn(page);
    }

    @Test
    void create() {
        ItemRequestDto result = itemRequestService.create(itemRequestRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getCreated(), result.getCreated());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.getRequestor());
        assertEquals(itemRequestDto.getId(), result.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void findAllByUserId() {
        List<ItemRequestDto> result = itemRequestService.findAllByUserId(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemRequestDto.getCreated(), result.getFirst().getCreated());
        assertEquals(itemRequestDto.getDescription(), result.getFirst().getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.getFirst().getRequestor());
        assertEquals(itemRequestDto.getId(), result.getFirst().getId());

        verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong());
    }

    @Test
    void findItemRequestById() {
        ItemRequestDto result = itemRequestService.findItemRequestById(itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getCreated(), result.getCreated());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.getRequestor());
        assertEquals(itemRequestDto.getId(), result.getId());

        verify(itemRequestRepository, times(1)).findByIdOrderByCreatedAsc(anyLong());
    }

    @Test
    void findAllUsersItemRequest() {
        List<ItemRequestDto> result = itemRequestService.findAllUsersItemRequest(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(itemRequestDto.getCreated(), result.getFirst().getCreated());
        assertEquals(itemRequestDto.getDescription(), result.getFirst().getDescription());
        assertEquals(itemRequestDto.getRequestor(), result.getFirst().getRequestor());
        assertEquals(itemRequestDto.getId(), result.getFirst().getId());

        verify(itemRequestRepository, times(1)).findAll((Pageable) any());
    }
}