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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя бронировать свою вещь");
        }

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new NotFoundException("Вещь недоступна для бронирования");
        }

        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        if (start == null || end == null) {
            throw new ValidationException("Дата начала и окончания обязательны");
        }

        if (!start.isBefore(end)) {
            throw new ValidationException("Дата окончания должна быть после даты начала");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала должна быть в будущем");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, item, booker);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto setApproved(Long userId, Long bookingId, Boolean approved) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id = " + userId + " not found!"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking id = " + bookingId + " not found!"));

        if (booking.getItem() == null || booking.getItem().getOwner() == null
                || !booking.getItem().getOwner().getId().equals(owner.getId())) {
            throw new NotFoundException("Only item owner can approve booking!");
        }

        if (booking.getStatus() == BookingStatus.APPROVED && Boolean.TRUE.equals(approved)) {
            throw new ValidationException("Booking is already approved!");
        }

        if (booking.getStatus() == BookingStatus.REJECTED && !Boolean.TRUE.equals(approved)) {
            throw new ValidationException("Booking is already rejected!");
        }

        booking.setStatus(Boolean.TRUE.equals(approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking id = " + bookingId + " not found!"));

        boolean isBooker = booking.getBooker() != null && booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem() != null && booking.getItem().getOwner() != null
                && booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotFoundException("Access denied!");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findAllByBookerAndStatus(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id = " + userId + " not found!"));

        List<Booking> bookings;

        if ("ALL".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
        } else if ("CURRENT".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                    .filter(b -> !b.getStart().isAfter(LocalDateTime.now()) && !b.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if ("PAST".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if ("FUTURE".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if ("WAITING".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if ("REJECTED".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        } else {
            throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> findAllByOwnerAndStatus(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id = " + userId + " not found!"));

        List<Booking> bookings;

        if ("ALL".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
        } else if ("CURRENT".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                    .filter(b -> !b.getStart().isAfter(LocalDateTime.now()) && !b.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if ("PAST".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                    .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if ("FUTURE".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId).stream()
                    .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());
        } else if ("WAITING".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
        } else if ("REJECTED".equalsIgnoreCase(state)) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        } else {
            throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}