package ru.practicum.shareit.booking.client;

import java.util.Map;

import io.micrometer.core.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.model.BadRequestException;

@Service
public class BookingClient extends BaseClient {
    private static final String BASE_PATH = "/bookings";

    public BookingClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(url + BASE_PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> create(long userId, BookItemRequestDto bookingCreateDto) {
        if (!bookingCreateDto.getStart().isBefore(bookingCreateDto.getEnd())) {
            throw new BadRequestException("Дата окончания должна быть больше даты начала");
        }
        return exchange("", HttpMethod.POST, bookingCreateDto, defaultHeaders(userId), null);
    }

    public ResponseEntity<Object> approveBooking(long userId, long bookingId, boolean approved) {
        Map<String, Object> params = Map.of("approved", approved);

        return exchange("/" + bookingId + "?approved={approved}", HttpMethod.PATCH, null,
                defaultHeaders(userId), params);
    }

    public ResponseEntity<Object> getById(long userId, long bookingId) {
        return exchange("/" + bookingId, HttpMethod.GET, null, defaultHeaders(userId), null);
    }


    public ResponseEntity<Object> getAll(long userId, BookingState state, boolean isOwner,
                                         int fromIndex, int size) {

        Map<String, Object> params = Map.of("state", state, "from", fromIndex, "size", size);

        String path = "?state={state}&from={from}&size={size}";

        return exchange(isOwner ? "/owner" + path : path, HttpMethod.GET, null, defaultHeaders(userId), params);
    }
}
