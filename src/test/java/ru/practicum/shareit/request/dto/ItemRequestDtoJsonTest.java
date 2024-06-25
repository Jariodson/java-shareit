package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> jsonItemRequestCreateDto;

    @Autowired
    private JacksonTester<ItemRequestDto> jsonItemRequestDto;

    @Test
    void testSerializeItemRequestCreateDto() throws Exception {
        ItemRequestCreateDto itemRequestCreateDto = ItemRequestCreateDto.builder()
                .description("Test Description")
                .build();

        JsonContent<ItemRequestCreateDto> result = jsonItemRequestCreateDto.write(itemRequestCreateDto);

        assertThat(result).hasJsonPathStringValue("$.description");

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Description");
    }

    @Test
    void testDeserializeItemRequestCreateDto() throws Exception {
        String jsonContent = "{\"description\":\"Test Description\"}";

        ObjectContent<ItemRequestCreateDto> result = jsonItemRequestCreateDto.parse(jsonContent);

        assertThat(result).isInstanceOf(ItemRequestCreateDto.class);
        assertThat(result.getObject().getDescription()).isEqualTo("Test Description");
    }

    @Test
    void testSerializeItemRequestDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.of(2023, 1, 1, 12, 0))
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> result = jsonItemRequestDto.write(itemRequestDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).hasJsonPathArrayValue("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id")
                .isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Test Description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2023-01-01T12:00:00");
        assertThat(result).extractingJsonPathArrayValue("$.items")
                .hasSize(1);
    }

    @Test
    void testDeserializeItemRequestDto() throws Exception {
        String jsonContent = "{\"id\":1,\"description\":\"Test Description\",\"created\":\"2023-01-01T12:00:00\"," +
                "\"items\":[{\"id\":1,\"name\":\"Test Item\"," +
                "\"description\":\"Test Description\",\"available\":true}]}";

        ObjectContent<ItemRequestDto> result = jsonItemRequestDto.parse(jsonContent);

        assertThat(result).isInstanceOf(ItemRequestDto.class);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getDescription()).isEqualTo("Test Description");
        assertThat(result.getObject().getCreated()).isEqualTo(
                LocalDateTime.of(2023, 1, 1, 12, 0));
        assertThat(result.getObject().getItems()).hasSize(1);
        assertThat(result.getObject().getItems().get(0).getId()).isEqualTo(1);
        assertThat(result.getObject().getItems().get(0).getName()).isEqualTo("Test Item");
        assertThat(result.getObject().getItems().get(0).getDescription()).isEqualTo("Test Description");
        assertThat(result.getObject().getItems().get(0).getAvailable()).isEqualTo(true);
    }
}
