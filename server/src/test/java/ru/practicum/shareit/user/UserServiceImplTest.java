package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);

        user = User.builder()
                .id(1L)
                .name("TestUserName")
                .email("UserEmail@test.com")
                .build();

        userDto = UserMapper.toUserDto(user);

        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));
    }

    @Test
    void findAll() {
        List<UserDto> result = userService.findAll().stream().toList();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(userDto.getId(), result.getFirst().getId());
        Assertions.assertEquals(userDto.getName(), result.getFirst().getName());
        Assertions.assertEquals(userDto.getEmail(), result.getFirst().getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void create() {
        UserDto result = userService.create(userDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository, times(1)).save(any());
    }

    @Test
    void update() {
        UserDto result = userService.update(user.getId(), userDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserDtoById() {
        UserDto result = userService.getById(user.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void delete() {
        userService.delete(user.getId());

        verify(userRepository, times(1)).deleteById(anyLong());
    }
}