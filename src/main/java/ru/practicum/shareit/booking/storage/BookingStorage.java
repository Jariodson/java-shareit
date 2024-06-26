package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    //Booker
    List<Booking> findByItemId(long itemId);

    List<Booking> findByItemIdAndBookerId(long itemId, long bookerId);

    List<Booking> findAllByBookerIdOrderByStartDesc(long bookerId);

    List<Booking> findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime localDateTime, long bookerId);

    List<Booking> findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime localDateTime, long bookerId);

    List<Booking> findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(LocalDateTime before, LocalDateTime after, long bookerId);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long bookerId, Status status);

    //ItemOwner
    List<Booking> findAllByItemUserIdOrderByStartDesc(long userId);

    List<Booking> findByEndBeforeAndItemUserIdOrderByStartDesc(LocalDateTime now, long userId);

    List<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(LocalDateTime before, LocalDateTime after, long userId);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(long userId, Status status);

    //For ItemService
    List<Booking> findAllByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByItemIdAndStartAfter(long itemId, LocalDateTime localDateTime);
}
