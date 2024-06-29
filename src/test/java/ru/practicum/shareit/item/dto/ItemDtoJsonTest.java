package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemCreatedDto> jsonItemCreatedDto;

    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    @Autowired
    private JacksonTester<ItemUpdatedDto> jsonItemUpdatedDto;

    @Test
    void testSerializeItemCreatedDto() throws Exception {
        ItemCreatedDto itemCreatedDto = ItemCreatedDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemCreatedDto> result = jsonItemCreatedDto.write(itemCreatedDto);

        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathNumberValue("$.requestId");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testDeserializeItemCreatedDto() throws Exception {
        String jsonContent = "{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":1}";

        ObjectContent<ItemCreatedDto> result = jsonItemCreatedDto.parse(jsonContent);

        assertThat(result).isInstanceOf(ItemCreatedDto.class);
        assertThat(result.getObject().getName()).isEqualTo("Test Item");
        assertThat(result.getObject().getDescription()).isEqualTo("Test Description");
        assertThat(result.getObject().getAvailable()).isEqualTo(true);
        assertThat(result.getObject().getRequestId()).isEqualTo(1);
    }

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(List.of())
                .requestId(1L)
                .build();

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathArrayValue("$.comments");
        assertThat(result).hasJsonPathNumberValue("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testDeserializeItemDto() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"comments\":[],\"requestId\":1}";

        ObjectContent<ItemDto> result = jsonItemDto.parse(jsonContent);

        assertThat(result).isInstanceOf(ItemDto.class);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Test Item");
        assertThat(result.getObject().getDescription()).isEqualTo("Test Description");
        assertThat(result.getObject().getAvailable()).isEqualTo(true);
        assertThat(result.getObject().getComments()).isEmpty();
        assertThat(result.getObject().getRequestId()).isEqualTo(1);
    }

    @Test
    void testSerializeItemUpdatedDto() throws Exception {
        ItemUpdatedDto itemUpdatedDto = ItemUpdatedDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(true)
                .build();

        JsonContent<ItemUpdatedDto> result = jsonItemUpdatedDto.write(itemUpdatedDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Updated Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testDeserializeItemUpdatedDto() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Updated Item\",\"description\":\"Updated Description\",\"available\":true}";

        ObjectContent<ItemUpdatedDto> result = jsonItemUpdatedDto.parse(jsonContent);

        assertThat(result).isInstanceOf(ItemUpdatedDto.class);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Updated Item");
        assertThat(result.getObject().getDescription()).isEqualTo("Updated Description");
        assertThat(result.getObject().getAvailable()).isEqualTo(true);
    }
}
