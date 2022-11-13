package com.zpi.userservice.user;

import com.zpi.userservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
        AppUser appUser = new AppUser(1L, "username", "email", "firstName", "surname",
                LocalDate.now(), new Password("hashedPassword"));
        AppUser appUser2 = new AppUser(2L, "username2", "email2", "firstName2", "surname2",
                LocalDate.now(), new Password("hashedPassword2"));
        var appUserList = List.of(appUser, appUser2);

        //when
        when(appUserRepository.findAllById(userIdsList)).thenReturn(appUserList);
        var actualResult = appUserService.getUsers(userIdsList);

        //then
        var expectedResult = List.of(new UserDto(1L, "username", "firstName", "surname"),
                new UserDto(2L, "username2", "firstName2", "surname2"));
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
}