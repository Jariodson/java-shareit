package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collections;
import java.util.Map;


@Service
public class ItemClient extends BaseClient {
    private static final String BASE_PATH = "/items";

    public ItemClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(url + BASE_PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAll(long userId, int fromIndex, int size) {
        Map<String, Object> params = Map.of("fromIndex", fromIndex, "size", size);

        return exchange("", HttpMethod.GET, null, defaultHeaders(userId), params);
    }

    public ResponseEntity<Object> get(long itemId, long userId) {
        return exchange("/" + itemId, HttpMethod.GET, null, defaultHeaders(userId), null);
    }

    public ResponseEntity<Object> create(long userId, ItemCreateDto item) {
        return exchange("", HttpMethod.POST, item, defaultHeaders(userId), null);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemUpdateDto item) {
        return exchange("/" + itemId, HttpMethod.PATCH, item, defaultHeaders(userId), null);
    }

    public ResponseEntity<Object> search(String text, int fromIndex, int size) {

        if (text.isBlank()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }

        Map<String, Object> params = Map.of("text", text, "from", fromIndex, "size", size);
        return exchange("/search?text={text}&from={from}&size={size}", HttpMethod.GET, null, getHeaders(), params);
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentDto comment) {
        return exchange("/" + itemId + "/comment", HttpMethod.POST, comment, defaultHeaders(userId), null);
    }
}
