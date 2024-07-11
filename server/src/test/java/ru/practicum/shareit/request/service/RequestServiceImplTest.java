package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.RequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class RequestServiceImplTest {

    @Mock
    private RequestStorage storage;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateRequest() {
        User mockUser = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.validateUserDto(ArgumentMatchers.anyLong())).thenReturn(mockUser);

        ItemRequestCreateDto requestCreateDto = new ItemRequestCreateDto("Test request");
        ItemRequest itemRequest = new ItemRequest(1L, "Test request", LocalDateTime.now(), mockUser, new ArrayList<>());
        when(itemRequestMapper.transformItemRequestCreateDtoToItemRequest(requestCreateDto, mockUser))
                .thenReturn(itemRequest);

        when(storage.save(itemRequest)).thenReturn(itemRequest);

        ItemRequestDto expectedDto = new ItemRequestDto(1L, "Test request", LocalDateTime.now(), new ArrayList<>());
        when(itemRequestMapper.transformItemRequestToItemRequestDto(itemRequest)).thenReturn(expectedDto);

        ItemRequestDto createdDto = requestService.createRequest(requestCreateDto, 1L);

        assertNotNull(createdDto);
        assertEquals(1L, createdDto.getId());
        assertEquals("Test request", createdDto.getDescription());
        assertNotNull(createdDto.getCreated());
        assertEquals(0, createdDto.getItems().size());
    }

    @Test
    public void testGetRequests() {
        User mockUser = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.validateUserDto(ArgumentMatchers.anyLong())).thenReturn(mockUser);

        List<ItemRequest> mockRequests = new ArrayList<>();
        mockRequests.add(new ItemRequest(1L, "Request 1", LocalDateTime.now(), mockUser, new ArrayList<>()));
        mockRequests.add(new ItemRequest(2L, "Request 2", LocalDateTime.now(), mockUser, new ArrayList<>()));
        when(storage.findAllByRequesterId(1L)).thenReturn(mockRequests);

        List<ItemRequestDto> expectedDtos = mockRequests.stream()
                .map(r -> new ItemRequestDto(r.getId(), r.getDescription(), r.getTimeCreated(), new ArrayList<>()))
                .collect(Collectors.toList());
        when(itemRequestMapper.transformItemRequestToItemRequestDto(any(ItemRequest.class)))
                .thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        List<ItemRequestDto> requestDtos = requestService.getRequests(1L);

        assertNotNull(requestDtos);
        assertEquals(2, requestDtos.size());
        assertEquals(1L, requestDtos.get(0).getId());
        assertEquals("Request 1", requestDtos.get(0).getDescription());
        assertEquals(2L, requestDtos.get(1).getId());
        assertEquals("Request 2", requestDtos.get(1).getDescription());
    }

    @Test
    public void testGetRequestsByParameter() {
        User mockUser = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.validateUserDto(ArgumentMatchers.anyLong())).thenReturn(mockUser);

        List<ItemRequest> mockRequests = new ArrayList<>();
        mockRequests.add(new ItemRequest(1L, "Request 1", LocalDateTime.now(), mockUser, new ArrayList<>()));
        mockRequests.add(new ItemRequest(2L, "Request 2", LocalDateTime.now(), mockUser, new ArrayList<>()));
        when(storage.findAllByRequesterIdNot(1L, PageRequest.of(0, 10, Sort.by("created").descending())))
                .thenReturn(mockRequests);

        List<ItemRequestDto> expectedDtos = mockRequests.stream()
                .map(r -> new ItemRequestDto(r.getId(), r.getDescription(), r.getTimeCreated(), new ArrayList<>()))
                .collect(Collectors.toList());
        when(itemRequestMapper.transformItemRequestToItemRequestDto(any(ItemRequest.class)))
                .thenReturn(expectedDtos.get(0), expectedDtos.get(1));

        List<ItemRequestDto> requestDtos = requestService.getRequestsByParameter(1L, 0, 10);

        assertNotNull(requestDtos);
        assertEquals(2, requestDtos.size());
        assertEquals(1L, requestDtos.get(0).getId());
        assertEquals("Request 1", requestDtos.get(0).getDescription());
        assertEquals(2L, requestDtos.get(1).getId());
        assertEquals("Request 2", requestDtos.get(1).getDescription());
    }

    @Test
    public void testGetRequestById() {
        User mockUser = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.validateUserDto(ArgumentMatchers.anyLong())).thenReturn(mockUser);

        ItemRequest mockRequest = new ItemRequest(1L, "Test request", LocalDateTime.now(), mockUser, new ArrayList<>());
        when(storage.findById(1L)).thenReturn(Optional.of(mockRequest));

        ItemRequestDto expectedDto = new ItemRequestDto(1L, "Test request", LocalDateTime.now(), new ArrayList<>());
        when(itemRequestMapper.transformItemRequestToItemRequestDto(mockRequest)).thenReturn(expectedDto);

        ItemRequestDto requestDto = requestService.getRequestById(1L, 1L);

        assertNotNull(requestDto);
        assertEquals(1L, requestDto.getId());
        assertEquals("Test request", requestDto.getDescription());
    }

    @Test
    public void testGetRequestByIdNotFound() {
        User mockUser = new User(1L, "John Doe", "john.doe@example.com");
        when(userService.validateUserDto(ArgumentMatchers.anyLong())).thenReturn(mockUser);

        when(storage.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequestById(1L, 1L));
    }

    @Test
    public void testValidateItemRequestFound() {
        ItemRequest mockRequest = new ItemRequest(1L, "Test request", LocalDateTime.now(),
                new User(), Collections.EMPTY_LIST);
        when(storage.findById(1L)).thenReturn(Optional.of(mockRequest));

        ItemRequest validatedRequest = requestService.validateItemRequest(1L);

        assertNotNull(validatedRequest);
        assertEquals(1L, validatedRequest.getId());
        assertEquals("Test request", validatedRequest.getDescription());
    }

    @Test
    public void testValidateItemRequestNotFound() {
        when(storage.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.validateItemRequest(1L));
    }

    @Test
    public void testGetRequestsByParameterNegativeFrom() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> requestService.getRequestsByParameter(1L, -1, 10));

        assertEquals("Значение from не может быть отрицательным", exception.getMessage());
    }

    @Test
    public void testGetRequestsByParameterNegativeSize() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> requestService.getRequestsByParameter(1L, 0, 0));

        assertEquals("Значение size не может быть меньше 10", exception.getMessage());
    }
}
