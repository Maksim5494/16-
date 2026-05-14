package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id = " + userId + " not found!"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item id = " + bookingRequestDto.getItemId() + " not found!"));

        validate(userId, bookingRequestDto, item);

        Booking booking = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto setApproved(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking id = " + bookingId + " not found!"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Only owner can approve booking!");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking status must be WAITING");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking id = " + bookingId + " not found!"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Access denied for user " + userId);
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findAllByBookerAndStatus(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        if (state == null || state.isBlank()) {
            state = "ALL";
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST" -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> findAllByOwnerAndStatus(Long userId, String state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        if (state == null || state.isBlank()) {
            state = "ALL";
        }

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "ALL" -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case "PAST" -> bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case "FUTURE" -> bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case "WAITING" -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new ValidationException("Unknown state: " + state);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validate(Long userId, BookingRequestDto dto, Item item) {
        LocalDateTime now = LocalDateTime.now();

        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Owner cannot book his own item");
        }
        if (dto.getStart() == null || dto.getEnd() == null ||
                dto.getStart().isBefore(now) ||
                !dto.getEnd().isAfter(dto.getStart())) {
            throw new ValidationException("Invalid booking dates");
        }
    }
}