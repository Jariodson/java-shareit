package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByItemId(long itemId);

    List<Booking> findByItemIdAndBookerId(long itemId, long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND (b.start < :start AND b.end > :start) " +
            "OR (b.start < :end AND b.end > :end) " +
            "OR (b.start >= :start AND b.end <= :end)")
    List<Booking> findOverlappingBookings(@Param("itemId") Long itemId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(@Param("bookerId") long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByEndTimeBeforeNow(@Param("bookerId") long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByStartTimeAfterNow(@Param("bookerId") long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findActiveBookings(@Param("bookerId") long bookerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByStatusAndBookerId(@Param("bookerId") long bookerId, @Param("status") Status status);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.user.id = :userId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByUserOwnerId(@Param("userId") long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.user.id = :userId " +
            "AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByEndTimeBeforeNowUserOwnerId(@Param("userId") long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.user.id = :userId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByStartTimeAfterNowByOwnerId(@Param("userId") long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.user.id = :userId " +
            "AND b.start <= CURRENT_TIMESTAMP " +
            "AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findActiveBookingsByOwnerId(@Param("userId") long userId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.user.id = :userId " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByStatusAndOwnerId(@Param("userId") long userId, @Param("status") Status status);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.end <= CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC")
    List<Booking> findByEndTimeOrderByEndTimeDesc(@Param("itemId") long itemId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.start >= CURRENT_TIMESTAMP")
    List<Booking> findByStartTimeOrderByStartTimeDesc(@Param("itemId") long itemId);
}
