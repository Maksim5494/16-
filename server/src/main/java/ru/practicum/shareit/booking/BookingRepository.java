package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndItem_IdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime end
    );

    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(
            Long itemId, BookingStatus status, LocalDateTime now
    );

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId, BookingStatus status, LocalDateTime now
    );
}