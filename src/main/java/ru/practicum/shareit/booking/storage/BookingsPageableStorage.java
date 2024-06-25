package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingsPageableStorage extends PagingAndSortingRepository<Booking, Integer> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findAllByEndBeforeAndBookerIdOrderByStartDesc(LocalDateTime end, Long bookerId, Pageable pageable);

    List<Booking> findAllByStartAfterAndBookerIdOrderByStartDesc(LocalDateTime start, Long bookerId, Pageable pageable);

    List<Booking> findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(
            LocalDateTime end, LocalDateTime start, Long userId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);

    //Item Owner
    List<Booking> findAllByItemUserIdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findByEndBeforeAndItemUserIdOrderByStartDesc(LocalDateTime end, Long userId, Pageable pageable);

    List<Booking> findAllByItemUserIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
            LocalDateTime end, LocalDateTime start, Long userId, Pageable pageable);

    List<Booking> findAllByItemUserIdAndStatusOrderByStartDesc(Long userId, Status status, Pageable pageable);
}
