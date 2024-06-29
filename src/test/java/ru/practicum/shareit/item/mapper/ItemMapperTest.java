package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreatedDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdatedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    private ItemMapper itemMapper;

    @BeforeEach
    public void setUp() {
        itemMapper = new ItemMapper();
    }

    @Test
    public void testTransformItemToItemDto() {
        Item item = Item.builder()
                .id(1L)
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        ItemDto itemDto = itemMapper.transformItemToItemDto(item);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    public void testTransformItemCreatedDtoToItem() {
        ItemCreatedDto itemCreatedDto = new ItemCreatedDto();
        itemCreatedDto.setName("New Item");
        itemCreatedDto.setDescription("New Description");
        itemCreatedDto.setAvailable(true);

        Item item = itemMapper.transformItemCreatedDtoToItem(itemCreatedDto);

        assertThat(item.getName()).isEqualTo("New Item");
        assertThat(item.getDescription()).isEqualTo("New Description");
        assertThat(item.getAvailable()).isTrue();
    }

    @Test
    public void testTransformItemUpdatedDtoToItem() {
        ItemUpdatedDto itemUpdatedDto = new ItemUpdatedDto();
        itemUpdatedDto.setId(2L);
        itemUpdatedDto.setName("Updated Item");
        itemUpdatedDto.setDescription("Updated Description");
        itemUpdatedDto.setAvailable(false);

        Item item = itemMapper.transformItemUpdatedDtoToItem(itemUpdatedDto);

        assertThat(item.getId()).isEqualTo(2L);
        assertThat(item.getName()).isEqualTo("Updated Item");
        assertThat(item.getDescription()).isEqualTo("Updated Description");
        assertThat(item.getAvailable()).isFalse();
    }

    @Test
    public void testTransformListItemToListItemDto() {
        Item item1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Description1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Description2")
                .available(false)
                .build();

        List<Item> items = List.of(item1, item2);

        List<ItemDto> itemDtos = (List<ItemDto>) itemMapper.transformListItemToListItemDto(items);

        assertThat(itemDtos).hasSize(2);
        assertThat(itemDtos.get(0).getId()).isEqualTo(1L);
        assertThat(itemDtos.get(0).getName()).isEqualTo("Item1");
        assertThat(itemDtos.get(0).getDescription()).isEqualTo("Description1");
        assertThat(itemDtos.get(0).getAvailable()).isTrue();
        assertThat(itemDtos.get(1).getId()).isEqualTo(2L);
        assertThat(itemDtos.get(1).getName()).isEqualTo("Item2");
        assertThat(itemDtos.get(1).getDescription()).isEqualTo("Description2");
        assertThat(itemDtos.get(1).getAvailable()).isFalse();
    }
}
