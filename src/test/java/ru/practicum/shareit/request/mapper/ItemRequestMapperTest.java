package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ItemRequestMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTransformItemRequestCreateDtoToItemRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Test Description");

        User user = new User();

        ItemRequest itemRequest = itemRequestMapper.transformItemRequestCreateDtoToItemRequest(createDto, user);

        assertEquals(createDto.getDescription(), itemRequest.getDescription());
        assertEquals(user, itemRequest.getRequester());
    }

    @Test
    public void testTransformItemRequestToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test Description");
        itemRequest.setCreated(LocalDateTime.now());

        List<ItemDto> itemDtoList = Arrays.asList(new ItemDto(), new ItemDto());

        when(itemStorage.findAllByItemRequestId(itemRequest.getId())).thenReturn(List.of(new Item(), new Item()));
        when(itemMapper.transformItemToItemDto(new Item())).thenReturn(new ItemDto());

        ItemRequestDto requestDto = itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), requestDto.getId());
        assertEquals(itemRequest.getDescription(), requestDto.getDescription());
        assertEquals(itemRequest.getCreated(), requestDto.getCreated());
        assertEquals(itemDtoList.size(), requestDto.getItems().size());
    }
}
