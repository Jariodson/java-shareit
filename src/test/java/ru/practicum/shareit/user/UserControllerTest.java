package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.storage.UserStorage;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private ObjectMapper objectMapper;

    private long userId;

    @BeforeEach
    public void setup() throws Exception {
        // Удаляем всех пользователей перед каждым тестом
        userStorage.deleteAll();

        // Создаем тестового пользователя
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();

        String userJson = objectMapper.writeValueAsString(userCreateDto);

        // Добавляем пользователя и сохраняем его ID для последующих тестов
        String response = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserDto userDto = objectMapper.readValue(response, UserDto.class);
        userId = userDto.getId();
    }

    @Test
    public void getUserById_ExistingUserId_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    public void createUser_ValidUser_ShouldReturnOk() throws Exception {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("New User")
                .email("newuser@example.com")
                .build();

        String userJson = objectMapper.writeValueAsString(userCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("New User"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    public void updateUser_ExistingUserId_ShouldReturnUpdatedUser() throws Exception {
        UserUpdatedDto userUpdatedDto = UserUpdatedDto.builder()
                .name("Updated User")
                .build();

        String userJson = objectMapper.writeValueAsString(userUpdatedDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated User"));
    }

    @Test
    public void deleteUser_ExistingUserId_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUsers_ShouldReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }
}