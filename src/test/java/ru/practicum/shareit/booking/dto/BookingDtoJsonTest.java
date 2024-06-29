package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreatedDto> jsonBookingCreatedDto;

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    @Test
    void testSerializeBookingCreatedDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingCreatedDto bookingCreatedDto = new BookingCreatedDto();
        bookingCreatedDto.setItemId(1L);
        bookingCreatedDto.setStart(start);
        bookingCreatedDto.setEnd(end);

        JsonContent<BookingCreatedDto> result = jsonBookingCreatedDto.write(bookingCreatedDto);

        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Test
    void testDeserializeBookingCreatedDto() throws Exception {
        String jsonContent = "{\"itemId\":1,\"start\":\"2024-06-26T10:15:30\",\"end\":\"2024-06-27T10:15:30\"}";

        ObjectContent<BookingCreatedDto> result = jsonBookingCreatedDto.parse(jsonContent);

        assertThat(result).isInstanceOf(BookingCreatedDto.class);
        assertThat(result.getObject().getItemId()).isEqualTo(1);
        assertThat(result.getObject().getStart())
                .isEqualTo(LocalDateTime.of(2024, 6, 26, 10, 15, 30));
        assertThat(result.getObject().getEnd())
                .isEqualTo(LocalDateTime.of(2024, 6, 27, 10, 15, 30));
    }

    @Test
    void testSerializeBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .item(new ItemDto())
                .booker(new UserDto())
                .build();

        JsonContent<BookingDto> result = jsonBookingDto.write(bookingDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).hasJsonPathStringValue("$.status");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.name());
    }

    @Test
    void testDeserializeBookingDto() throws Exception {
        String jsonContent = "{\"id\":1,\"start\":\"2024-06-26T10:15:30\",\"end\":\"2024-06-27T10:15:30\",\"status\":\"WAITING\"}";

        ObjectContent<BookingDto> result = jsonBookingDto.parse(jsonContent);

        assertThat(result).isInstanceOf(BookingDto.class);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getStart())
                .isEqualTo(LocalDateTime.of(2024, 6, 26, 10, 15, 30));
        assertThat(result.getObject().getEnd())
                .isEqualTo(LocalDateTime.of(2024, 6, 27, 10, 15, 30));
        assertThat(result.getObject().getStatus()).isEqualTo(Status.WAITING);
    }
}
