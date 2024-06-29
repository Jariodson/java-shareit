package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
public class BookingCreatedDtoJsonTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Autowired
    private JacksonTester<BookingCreatedDto> json;

    @Test
    public void serializeDto() throws IOException {
        BookingCreatedDto dto = new BookingCreatedDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2024, 6, 30, 10, 0));
        dto.setEnd(LocalDateTime.of(2024, 7, 1, 10, 0));

        JsonContent<BookingCreatedDto> jsonContent = json.write(dto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2024-06-30T10:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2024-07-01T10:00:00");
    }

    @Test
    public void deserializeDto() throws Exception {
        String content = "{\"itemId\": 2, \"start\": \"2024-07-02T10:00:00\", \"end\": \"2024-07-03T10:00:00\"}";

        BookingCreatedDto dto = json.parseObject(content);

        assertThat(dto.getItemId()).isEqualTo(2L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2024, 7, 2, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2024, 7, 3, 10, 0));
    }
}
