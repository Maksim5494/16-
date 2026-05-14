package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus() != null ? booking.getStatus().name() : null)
                .build();
    }

    public static BookingDateInfoDto toBookingDateInfoDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingDateInfoDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .build();
    }

    public static Booking toBooking(BookingRequestDto requestDto, Item item, User booker) {
        if (requestDto == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setStart(requestDto.getStart());
        booking.setEnd(requestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }
}