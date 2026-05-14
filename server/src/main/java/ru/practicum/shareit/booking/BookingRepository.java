package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime end
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
            Long itemId, BookingStatus status, LocalDateTime now
    );

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime now
    );
}