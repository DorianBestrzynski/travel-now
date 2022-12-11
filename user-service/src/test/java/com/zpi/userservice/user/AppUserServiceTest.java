package com.zpi.userservice.user;

import com.zpi.userservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class AppUserServiceTest {

    @MockBean
    AppUserRepository appUserRepository;

    @Autowired
    @InjectMocks
    AppUserService appUserService;


    @Test
    void shouldReturnListOfUsersDto() {
        //given
        var userIdsList = List.of(1L, 2L);
        AppUser appUser = new AppUser(1L, "phoneNumber", "email", "firstName", "surname",
                LocalDate.now(), new Password("hashedPassword"));
        AppUser appUser2 = new AppUser(2L, "username2", "email2", "firstName2", "surname2",
                LocalDate.now(), new Password("hashedPassword2"));
        var appUserList = List.of(appUser, appUser2);

        //when
        when(appUserRepository.findAllById(userIdsList)).thenReturn(appUserList);
        var actualResult = appUserService.getUsers(userIdsList);

        //then
        var expectedResult = List.of(new UserDto(1L, "phoneNumber", "email", "firstName", "surname"),
                new UserDto(2L, "username2", "email2", "firstName2", "surname2"));
        verify(appUserRepository, times(1)).findAllById(userIdsList);
        assertThat(actualResult).hasSameElementsAs(expectedResult);
    }

    @Test
    void shouldReturnEmptyListOfUsersDto() {
        //given
        var userIdsList = List.of(1L, 2L);
        List<AppUser> appUserList = List.of();

        //when
        when(appUserRepository.findAllById(userIdsList)).thenReturn(appUserList);
        var actualResult = appUserService.getUsers(userIdsList);

        //then
        List<UserDto> expectedResult = List.of();
        verify(appUserRepository, times(1)).findAllById(userIdsList);
        assertThat(actualResult).hasSameElementsAs(expectedResult);
    }

    @Test
    void shouldReturnEditedUser() {
        //given
        AppUser editedAppUser = new AppUser(1L, "editedPhoneNumber", "email", "editedFirstName", "editedSurname",
                LocalDate.now(), new Password("hashedPassword"));
        AppUser appUser = new AppUser(1L, "phoneNumber", "email", "firstName", "surname",
                LocalDate.now(), new Password("hashedPassword"));
        //when
        when(appUserRepository.findById(editedAppUser.getUserId())).thenReturn(Optional.of(appUser));
        when(appUserRepository.save(any(AppUser.class))).then(i -> i.getArguments()[0]);
        var actualResult = appUserService.editUser(editedAppUser);

        //then
        var expectedResult = new UserDto(1L,"editedPhoneNumber", "email", "editedFirstName", "editedSurname");
        verify(appUserRepository, times(1)).findById(any());
        verify(appUserRepository, times(1)).save(any());

        assertThat(actualResult).satisfies(
                res -> {
                    assertThat(res.userId()).isEqualTo(expectedResult.userId());
                    assertThat(res.phoneNumber()).isEqualTo(expectedResult.phoneNumber());
                    assertThat(res.email()).isEqualTo(expectedResult.email());
                    assertThat(res.firstName()).isEqualTo(expectedResult.firstName());
                    assertThat(res.surname()).isEqualTo(expectedResult.surname());
                }
        );
    }

    @Test
    void shouldThrowErrorWhenUserIdIsNull() {
        //given
        AppUser editedAppUser = new AppUser(null, "editedPhoneNumber", "email", "editedFirstName", "editedSurname",
                LocalDate.now(), new Password("hashedPassword"));

        //when
        var exception = assertThrows(IllegalArgumentException.class,
                () -> appUserService.editUser(editedAppUser));

        //then
        verify(appUserRepository, never()).findById(any());
        verify(appUserRepository, never()).save(any());

        assertThat(exception.getMessage()).isEqualTo("UserId is missing");

    }

    @Test
    void shouldThrowErrorWhenAppUserNotFound() {
        //given
        AppUser editedAppUser = new AppUser(1L, "editedPhoneNumber", "email", "editedFirstName", "editedSurname",
                LocalDate.now(), new Password("hashedPassword"));

        //when
        when(appUserRepository.findById(editedAppUser.getUserId())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () -> appUserService.editUser(editedAppUser));

        //then
        verify(appUserRepository, times(1)).findById(any());
        verify(appUserRepository, never()).save(any());

        assertThat(exception.getMessage()).isEqualTo("User with given id was not found");

    }

    @Test
    void shouldGetUser() {
        //given
        Long userId = 1L;
        AppUser user = new AppUser(1L, "editedPhoneNumber", "email", "editedFirstName", "editedSurname",
                LocalDate.now(), new Password("hashedPassword"));
        //when
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(user));
        appUserService.getUser(userId);

        //then
        verify(appUserRepository, times(1)).findById(1L);

    }

    @Test
    void shouldThrowErrorWhenAppUserNotFoundInGetUser() {
        //given
        AppUser editedAppUser = new AppUser(1L, "editedPhoneNumber", "email", "editedFirstName", "editedSurname",
                LocalDate.now(), new Password("hashedPassword"));

        //when
        when(appUserRepository.findById(editedAppUser.getUserId())).thenReturn(Optional.empty());
        var exception = assertThrows(EntityNotFoundException.class,
                () -> appUserService.getUser(1L));

        //then
        verify(appUserRepository, times(1)).findById(1L);

        assertThat(exception.getMessage()).isEqualTo("User with given id was not found");

    }
}