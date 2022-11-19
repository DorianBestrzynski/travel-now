package com.zpi.accommodationservice.testconfig;

import com.zpi.accommodationservice.accommodation.Accommodation;
import com.zpi.accommodationservice.accommodation.AccommodationRepository;
import com.zpi.accommodationservice.accommodation.AccommodationService;
import com.zpi.accommodationservice.votes.AccommodationVoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeData {
    @Value("${spring.profiles.active:default}")
    private String profile;

    private final AccommodationRepository accommodationRepository;

    private final AccommodationVoteRepository accommodationVoteRepository;

    private final AccommodationService accommodationService;

    @PostConstruct
    public void addAccommodations() {
        if (!profile.equals("test"))
            return;

        var accommodations = List.of(
                new Accommodation(1L, 1L, "CITY CENTER 2 MINUTES TO SUBWAY BATOREGO 18/226", "Warszawa, Mazowieckie, Polska", "Warszawa", "Polska", "Mazowieckie", "Warsaw Airbnb", "https://a0.muscache.com/pictures/ab594054-d0aa-4160-910b-9ffde078c36c.jpg", "https://www.airbnb.pl/rooms/626559268372534118?adults=1&children=0&infants=0&pets=0&check_in=2022-11-20&check_out=2022-11-27&source_impression_id=p3_1668615964_by7Xh1V0NBb7x90F", BigDecimal.TEN, 52.23, 21.01),
                new Accommodation(1L, 2L, "Hampton by Hilton Warsaw Airport", "ul. Komitetu Obrony Robotników 39F, Włochy, 02-148 Warszawa, Polska", "Warszawa", "Polska", "Mazowieckie", "Warsaw Booking", "https://t-cf.bstatic.com/xdata/images/hotel/max500/214248952.jpg?k=8e1e8cd6d8515dca31a237bf05f73051fb51f9543a5209df54a6c443c7114a80&o=&hp=1", "https://www.booking.com/hotel/pl/hampton-by-hilton-warsaw-airport.pl.html", BigDecimal.TEN, 52.18, 20.98),
                new Accommodation(1L, 2L, "Studio 88", "Wrocław, Dolnośląskie, Polska", "Wrocław", "Polska", "Dolnośląskie", "Wrocław Airbnb", "https://a0.muscache.com/pictures/756ca0bd-3c86-4d6a-87a5-a4510ac8f256.jpg", "https://www.airbnb.pl/rooms/736968436514932376?adults=1&children=0&infants=0&pets=0&check_in=2022-11-20&check_out=2022-11-25&source_impression_id=p3_1668616188_yoJ4RBp%2FQ5UUfPIJ", BigDecimal.TEN, 51.11, 17.04),
                new Accommodation(5L, 2L, "Hotel Śląsk", "Oporowska 60, Fabryczna, 53-434 Wrocław, Polska", "Wrocław", "Polska", "Dolnośląskie", "Wrocław Booking", "https://t-cf.bstatic.com/xdata/images/hotel/max500/284134490.jpg?k=f052bdea87d5f5fb432d145d53930481c0199c572cbc35bb854565ff66e981c8&o=&hp=1", "https://www.booking.com/hotel/pl/slask-wroclaw.pl.html", BigDecimal.TEN, 51.1, 17.0),
                new Accommodation(5L, 4L, "Studio 88", "Wrocław, Dolnośląskie, Polska", "Wrocław", "Polska", "Dolnośląskie", "Wrocław Airbnb", "https://a0.muscache.com/pictures/756ca0bd-3c86-4d6a-87a5-a4510ac8f256.jpg", "https://www.airbnb.pl/rooms/736968436514932376?adults=1&children=0&infants=0&pets=0&check_in=2022-11-20&check_out=2022-11-25&source_impression_id=p3_1668616188_yoJ4RBp%2FQ5UUfPIJ", BigDecimal.TEN, 51.11, 17.04)
        );

        accommodationRepository.saveAll(accommodations);
    }

//    @PostConstruct
//    public void addVotes() {
//        if (!profile.equals("test"))
//            return;
//
//        var votes = List.of(
//                new AccommodationVote(new AccommodationVoteId(1L, 1L)),
//                new AccommodationVote(new AccommodationVoteId(2L, 1L)),
//                new AccommodationVote(new AccommodationVoteId(2L, 4L))
//        );
//
//        accommodationVoteRepository.saveAll(votes);
//    }
}
