package com.zpi.transportservice.accommodation_transport;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
class AccommodationTransportServiceTest {
    @MockBean
    AccommodationTransportRepository accommodationTransportRepository;

    @Autowired
    @InjectMocks
    AccommodationTransportService accommodationTransportService;

    @Captor
    ArgumentCaptor<AccommodationTransport> accommodationTransportArgumentCaptor;

    @Test
    void shouldReturnListOfAccommodationTransports() {
        //given
        var accommodationTransport = List.of(new AccommodationTransport());

        //when
        when(accommodationTransportRepository.findAccommodationTransportByAccommodationId(anyLong())).thenReturn(accommodationTransport);
        var result = accommodationTransportService.findAccommodationTransport(1L);

        //then
        assertThat(result).isEqualTo(accommodationTransport);
        verify(accommodationTransportRepository, times(1)).findAccommodationTransportByAccommodationId(1L);
    }

    @Test
    void shouldCreateAccommodationTransport() {
        //given
        var accommodationTransport = new AccommodationTransport(new AccommodationTransportId(1L, 2L));

        //when
        accommodationTransportService.createAccommodationTransport(1L, 2L);

        //then
        verify(accommodationTransportRepository, times(1)).save(accommodationTransportArgumentCaptor.capture());
        assertThat(accommodationTransportArgumentCaptor.getValue()).satisfies(
                at -> {
                    assertThat(at.getId().getTransportId()).isEqualTo(accommodationTransport.getId().getTransportId());
                    assertThat(at.getId().getAccommodationId()).isEqualTo(accommodationTransport.getId().getAccommodationId());
                }
        );
    }
}