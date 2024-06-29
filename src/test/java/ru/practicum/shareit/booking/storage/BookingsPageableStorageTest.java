package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class BookingsPageableStorageTest {

    @Autowired
    private BookingsPageableStorage bookingsPageableStorage;

    @Autowired
    private UserStorage userRepository;
    @Autowired
    private ItemStorage itemRepository;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description of test item");
        item.setAvailable(true);
        item.setUser(user);
        item = itemRepository.save(item);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        bookingsPageableStorage.save(booking);
    }

    @Test
    void findAllByBookerIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByBookerIdOrderByStartDesc(user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByEndBeforeAndBookerIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByEndBeforeAndBookerIdOrderByStartDesc(
                LocalDateTime.now().plusDays(2), user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByStartAfterAndBookerIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByStartAfterAndBookerIdOrderByStartDesc(
                LocalDateTime.now().minusDays(2), user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByEndAfterAndStartBeforeAndBookerIdOrderByStartDesc(
                LocalDateTime.now().plusHours(2), LocalDateTime.now().minusHours(2), user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByBookerIdAndStatusOrderByStartDesc(
                user.getId(), Status.WAITING, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemUserIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByItemUserIdOrderByStartDesc(user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findByEndBeforeAndItemUserIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findByEndBeforeAndItemUserIdOrderByStartDesc(
                LocalDateTime.now().plusDays(2), user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemUserIdAndStartAfterOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByItemUserIdAndStartAfterOrderByStartDesc(
                user.getId(), LocalDateTime.now().minusDays(2), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByEndAfterAndStartBeforeAndItemUserIdOrderByStartDesc(
                LocalDateTime.now().plusHours(10), LocalDateTime.now().minusHours(10), user.getId(), pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }

    @Test
    void findAllByItemUserIdAndStatusOrderByStartDesc_shouldReturnBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> bookings = bookingsPageableStorage.findAllByItemUserIdAndStatusOrderByStartDesc(
                user.getId(), Status.WAITING, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
    }
}
