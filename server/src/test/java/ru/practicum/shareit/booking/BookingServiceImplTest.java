package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingRequestDto bookingRequestDto;
    private User user;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("TestOwnerName")
                .email("OwnerEmail@test.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("TestItemName")
                .description("TestItemDescription")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();

        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(booking.getStart());
        bookingRequestDto.setEnd(booking.getEnd());
    }

    @Test
    void create() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        final BookingDto result = bookingService.create(user.getId(), bookingRequestDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getId(), result.getId());
        Assertions.assertEquals(BookingStatus.WAITING.toString(), result.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void setApproved() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.setApproved(owner.getId(), booking.getId(), true);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(BookingStatus.APPROVED.toString(), result.getStatus());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void findById() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.findById(booking.getId(), user.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getId(), result.getId());
        verify(bookingRepository, times(1)).findById(booking.getId());
    }

    @Test
    void findAllByBookerAndStatusTest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId()))
                .thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.findAllByBookerAndStatus(user.getId(), "ALL");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(bookingRepository).findAllByBookerIdOrderByStartDesc(user.getId());
    }

    @Test
    void findAllByOwnerAndStatus() {
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(owner.getId()))
                .thenReturn(List.of(booking));

        Collection<BookingDto> result = bookingService.findAllByOwnerAndStatus(owner.getId(), "ALL");

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(bookingRepository).findAllByItemOwnerIdOrderByStartDesc(owner.getId());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.findById(999L, user.getId());
        });
    }
}