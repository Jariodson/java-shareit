package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserCreateDto> jsonUserCreateDto;

    @Autowired
    private JacksonTester<UserDto> jsonUserDto;

    @Autowired
    private JacksonTester<UserUpdatedDto> jsonUserUpdatedDto;

    @Test
    void testSerializeUserCreateDto() throws Exception {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        JsonContent<UserCreateDto> result = jsonUserCreateDto.write(userCreateDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void testDeserializeUserCreateDto() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        ObjectContent<UserCreateDto> result = jsonUserCreateDto.parse(jsonContent);

        assertThat(result).isInstanceOf(UserCreateDto.class);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Test User");
        assertThat(result.getObject().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        JsonContent<UserDto> result = jsonUserDto.write(userDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@example.com");
    }

    @Test
    void testDeserializeUserDto() throws Exception {
        String jsonContent = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\"}";

        ObjectContent<UserDto> result = jsonUserDto.parse(jsonContent);

        assertThat(result).isInstanceOf(UserDto.class);
        assertThat(result.getObject().getId()).isEqualTo(1);
        assertThat(result.getObject().getName()).isEqualTo("Test User");
        assertThat(result.getObject().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testSerializeUserUpdatedDto() throws Exception {
        UserUpdatedDto userUpdatedDto = UserUpdatedDto.builder()
                .name("Updated User")
                .email("updated@example.com")
                .build();

        JsonContent<UserUpdatedDto> result = jsonUserUpdatedDto.write(userUpdatedDto);

        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Updated User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("updated@example.com");
    }

    @Test
    void testDeserializeUserUpdatedDto() throws Exception {
        String jsonContent = "{\"name\":\"Updated User\",\"email\":\"updated@example.com\"}";

        ObjectContent<UserUpdatedDto> result = jsonUserUpdatedDto.parse(jsonContent);

        assertThat(result).isInstanceOf(UserUpdatedDto.class);
        assertThat(result.getObject().getName()).isEqualTo("Updated User");
        assertThat(result.getObject().getEmail()).isEqualTo("updated@example.com");
    }
}
