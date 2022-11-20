package com.zpi.accommodationservice.accommodation;

import com.zpi.accommodationservice.dto.AccommodationDto;
import com.zpi.accommodationservice.exceptions.DataExtractionNotSupported;
import com.zpi.accommodationservice.exceptions.SiteNotFoundException;
import com.zpi.accommodationservice.proxies.UserGroupProxy;
import com.zpi.accommodationservice.security.CustomUsernamePasswordAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.zpi.accommodationservice.exceptions.ExceptionsInfo.DATA_EXTRACTION_EXCEPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AccommodationServiceTests {
    @MockBean
    AccommodationRepository accommodationRepository;

    @Autowired
    @InjectMocks
    AccommodationService accommodationService;

    @MockBean
    UserGroupProxy userGroupProxy;

    private final static String BOOKING_TEST_URL = "https://www.booking.com/hotel/pl/hornigold-w-zielonej-kamienicy.pl.html?aid=304142&label=gen173nr-1FCAEoggI46AdIHlgEaLYBiAEBmAEeuAEXyAEM2AEB6AEB-AECiAIBqAIDuAK7iMWbBsACAdICJDlmNmQ1NmY1LTgyN2EtNGY4Ny1hNmU1LTViNjEwZjk2NzcyMdgCBeACAQ&sid=d1a3e492f31416c795760c610d8aeabd&all_sr_blocks=2346602_95147485_0_2_0;checkin=2022-11-18;checkout=2022-11-19;dest_id=-507224;dest_type=city;dist=0;group_adults=2;group_children=0;hapos=1;highlighted_blocks=2346602_95147485_0_2_0;hpos=1;matching_block_id=2346602_95147485_0_2_0;no_rooms=1;req_adults=2;req_children=0;room1=A%2CA;sb_price_type=total;sr_order=late_escape_deals_upsorter;sr_pri_blocks=2346602_95147485_0_2_0__32130;srepoch=1668367428;srpvid=5fdb886171210379;type=total;ucfs=1&#hotelTmpl";
    private final static String AIRBNB_TEST_URL = "https://www.airbnb.pl/rooms/654017285821150998?check_in=2022-12-30&check_out=2023-01-02&guests=1&adults=15&s=67&unique_share_id=72e7b39a-9552-4fac-b21e-918a47000775";
    private final static String KAYAK_TEST_URL = "https://www.kayak.pl/hotels/Malediwy-u149/2022-11-14/2022-11-15/2adults?sort=rank_a";
    private final static String BAD_URL = "https://www.airbnb.pl/Pdsadsad/Polska/";

    void mockAuthorizePartOfTheGroupAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserPartOfTheGroup(any(), any(), any());
    }

    void mockAuthorizeAuthorOrCoordinatorExpenditureAspect(){
        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, 1L);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(accommodationRepository.findById(any())).thenReturn(Optional.of(new Accommodation()));
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserPartOfTheGroup(any(), any(), any());
        doReturn(Boolean.TRUE).when(userGroupProxy).isUserCoordinator(any(), any(), any());
    }

    @Test
    void shouldExtractDataFromBooking() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationDto = new AccommodationDto(1L, 1L, BOOKING_TEST_URL, "test", new BigDecimal(0));

        //when
        when(accommodationRepository.save(any(Accommodation.class))).thenAnswer(i -> i.getArguments()[0]);
        var result = accommodationService.addAccommodation(accommodationDto);
        //then
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(1L);
                    assertThat(actual.getCreator_id()).isEqualTo(1L);
                    assertThat(actual.getName()).isEqualTo("Aparthotel Hornigold \"W Zielonej Kamienicy\"");
                    assertThat(actual.getStreetAddress()).isEqualTo("Ul. Kopernika 6, 40-064 Katowice, Polska");
                    assertThat(actual.getCity()).isEqualTo("Katowice");
                    assertThat(actual.getCountry()).isEqualTo("Polska");
                    assertThat(actual.getRegion()).isEqualTo("śląskie");
                    assertThat(actual.getDescription()).isEqualTo("test");
                    assertThat(actual.getSourceLink()).isEqualTo("https://www.booking.com/hotel/pl/hornigold-w-zielonej-kamienicy.pl.html");
                    assertThat(actual.getGivenVotes()).isEqualTo(0);
                    assertThat(actual.getPrice()).isEqualTo(new BigDecimal(0));
                    assertThat(actual.getLatitude()).isEqualTo(50.25);
                    assertThat(actual.getLongitude()).isEqualTo(19.02);
                }
        );
    }

    @Test
    void shouldExtractDataFromAirbnb() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationDto = new AccommodationDto(1L, 1L, AIRBNB_TEST_URL, "test", new BigDecimal(0));

        //when
        when(accommodationRepository.save(any(Accommodation.class))).thenAnswer(i -> i.getArguments()[0]);
        var result = accommodationService.addAccommodation(accommodationDto);

        //then
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(1L);
                    assertThat(actual.getCreator_id()).isEqualTo(1L);
                    assertThat(actual.getName()).isEqualTo("Zamek Rychvald ***");
                    assertThat(actual.getStreetAddress()).isEqualTo("Rychvald, Moravskoslezský kraj, Czechy");
                    assertThat(actual.getCity()).isEqualTo("Rychvald");
                    assertThat(actual.getCountry()).isEqualTo("Czechy");
                    assertThat(actual.getRegion()).isEqualTo("Moravskoslezskýkraj");
                    assertThat(actual.getDescription()).isEqualTo("test");
                    assertThat(actual.getImageLink()).isEqualTo("https://a0.muscache.com/pictures/miso/Hosting-654017285821150998/original/7ce75e7f-0110-4b3d-967e-334b88ffca90.jpeg");
                    assertThat(actual.getSourceLink()).isEqualTo("https://www.airbnb.pl/rooms/654017285821150998?check_in=2022-12-30&check_out=2023-01-02&guests=1&adults=15&s=67&unique_share_id=72e7b39a-9552-4fac-b21e-918a47000775");
                    assertThat(actual.getGivenVotes()).isEqualTo(0);
                    assertThat(actual.getPrice()).isEqualTo(new BigDecimal(0));
                    assertThat(actual.getLatitude()).isEqualTo(49.87);
                    assertThat(actual.getLongitude()).isEqualTo(18.38);
                }
        );
    }

    @Test
    void shouldThrowDataExtractionNotSupported() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationDto = new AccommodationDto(1L, 1L, KAYAK_TEST_URL, "test", new BigDecimal(0));

        //then
        assertThatThrownBy(() -> accommodationService.addAccommodation(accommodationDto))
                .isInstanceOf(DataExtractionNotSupported.class)
                .hasMessage(DATA_EXTRACTION_EXCEPTION);
    }

    @Test
    void shouldThrowSiteNotFoundException() {
        //given
        mockAuthorizePartOfTheGroupAspect();
        var accommodationDto = new AccommodationDto(1L, 1L, BAD_URL, "test", new BigDecimal(0));

        //then
        assertThatThrownBy(() -> accommodationService.addAccommodation(accommodationDto))
                .isInstanceOf(SiteNotFoundException.class);
    }

    @Test
    void shouldReturnAccommodationsForGivenGroup() {
        //given
        mockAuthorizePartOfTheGroupAspect();

        //when
        when(accommodationRepository.findAllByGroupId(anyLong())).thenReturn(Optional.of(List.of(new Accommodation())));
        accommodationService.getAllAccommodationsForGroup(1L);

        //then
        verify(accommodationRepository).findAllByGroupId(1L);
    }

    @Test
    void shouldDeleteAccommodation() {
        //given
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();

        //when
        accommodationService.deleteAccommodation(1L);

        //then
        verify(accommodationRepository).deleteById(1L);
    }

    @Test
    void shouldEditAccommodation() {
        //given
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();
        var accommodation = new Accommodation();
        var accommodationDto = new AccommodationDto(1L, 1L, AIRBNB_TEST_URL, "test", new BigDecimal(0));

        //when
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(any(Accommodation.class))).thenAnswer(i -> i.getArguments()[0]);
        accommodationService.deleteAccommodation(1L);
        var result = accommodationService.editAccommodation(1L, 1L, accommodationDto);

        //then
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(null);
                    assertThat(actual.getCreator_id()).isEqualTo(null);
                    assertThat(actual.getName()).isEqualTo("Zamek Rychvald ***");
                    assertThat(actual.getStreetAddress()).isEqualTo("Rychvald, Moravskoslezský kraj, Czechy");
                    assertThat(actual.getCity()).isEqualTo("Rychvald");
                    assertThat(actual.getCountry()).isEqualTo("Czechy");
                    assertThat(actual.getRegion()).isEqualTo("Moravskoslezskýkraj");
                    assertThat(actual.getDescription()).isEqualTo("test");
                    assertThat(actual.getImageLink()).isEqualTo("https://a0.muscache.com/pictures/miso/Hosting-654017285821150998/original/7ce75e7f-0110-4b3d-967e-334b88ffca90.jpeg");
                    assertThat(actual.getSourceLink()).isEqualTo("https://www.airbnb.pl/rooms/654017285821150998?check_in=2022-12-30&check_out=2023-01-02&guests=1&adults=15&s=67&unique_share_id=72e7b39a-9552-4fac-b21e-918a47000775");
                    assertThat(actual.getGivenVotes()).isEqualTo(0);
                    assertThat(actual.getPrice()).isEqualTo(new BigDecimal(0));
                    assertThat(actual.getLatitude()).isEqualTo(49.87);
                    assertThat(actual.getLongitude()).isEqualTo(18.38);
                }
        );
    }

    @Test
    void shouldEditAccommodationWithoutLink() {
        //given
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();
        var accommodation = new Accommodation();
        var accommodationDto = new AccommodationDto(1L, 1L, null, "test", new BigDecimal(1));

        //when
        when(accommodationRepository.findById(anyLong())).thenReturn(Optional.of(accommodation));
        when(accommodationRepository.save(any(Accommodation.class))).thenAnswer(i -> i.getArguments()[0]);
        accommodationService.deleteAccommodation(1L);
        var result = accommodationService.editAccommodation(1L, 1L, accommodationDto);

        //then
        assertThat(result).satisfies(
                actual -> {
                    assertThat(actual.getGroupId()).isEqualTo(null);
                    assertThat(actual.getCreator_id()).isEqualTo(null);
                    assertThat(actual.getName()).isEqualTo(null);
                    assertThat(actual.getStreetAddress()).isEqualTo(null);
                    assertThat(actual.getCity()).isEqualTo(null);
                    assertThat(actual.getCountry()).isEqualTo(null);
                    assertThat(actual.getRegion()).isEqualTo(null);
                    assertThat(actual.getDescription()).isEqualTo("test");
                    assertThat(actual.getImageLink()).isEqualTo(null);
                    assertThat(actual.getSourceLink()).isEqualTo(null);
                    assertThat(actual.getGivenVotes()).isEqualTo(0);
                    assertThat(actual.getPrice()).isEqualTo(new BigDecimal(1));
                    assertThat(actual.getLatitude()).isEqualTo(null);
                    assertThat(actual.getLongitude()).isEqualTo(null);
                }
        );
    }

    @Test
    void shouldThrowExceptionsOnInvalidInput() {
        mockAuthorizeAuthorOrCoordinatorExpenditureAspect();
        mockAuthorizePartOfTheGroupAspect();

        assertThrows(IllegalArgumentException.class, () -> accommodationService.getAllAccommodationsForGroup(null));
        assertThrows(IllegalArgumentException.class, () -> accommodationService.deleteAccommodation(null));
        assertThrows(IllegalArgumentException.class, () -> accommodationService.editAccommodation(null, 1L, null));
        assertThrows(IllegalArgumentException.class, () -> accommodationService.editAccommodation(1L, null, null));
        assertThrows(IllegalArgumentException.class, () -> accommodationService.editAccommodation(1L, null, null));
    }
}
