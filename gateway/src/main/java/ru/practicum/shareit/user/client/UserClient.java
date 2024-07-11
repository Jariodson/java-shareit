package ru.practicum.shareit.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Service
public class UserClient extends BaseClient {

    private static final String BASE_PATH = "/users";

    public UserClient(@Value("${shareit-server.url}") String url, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(url + BASE_PATH))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> getAll() {
        return exchange("", HttpMethod.GET, null, getHeaders(), null);
    }

    public ResponseEntity<Object> get(long userId) {
        return exchange("/" + userId, HttpMethod.GET, null, getHeaders(), null);
    }

    public void delete(long userId) {
        exchange("/" + userId, HttpMethod.DELETE, null, defaultHeaders(userId), null);
    }

    public ResponseEntity<Object> create(UserCreateDto user) {
        return exchange("", HttpMethod.POST, user, getHeaders(), null);
    }

    public ResponseEntity<Object> update(long userId, UserUpdateDto user) {
        return exchange("/" + userId, HttpMethod.PATCH, user, defaultHeaders(userId), null);
    }
}
