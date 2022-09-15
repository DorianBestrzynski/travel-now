package com.zpi.accommodationservice.accommodationservice.accomodation_strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.SiteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.zpi.accommodationservice.accommodationservice.comons.BookingMapKeys.*;

@Component
@RequiredArgsConstructor
public class BookingExtractionStrategy implements AccommodationDataExtractionStrategy {
    private static final String BOOKING_URL = "booking.com";
    @Override
    public AccommodationDataDto extractDataFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                       .get();
        } catch (IOException e) {
            throw new SiteNotFoundException(e.getMessage());
        }

        Elements body = doc.select("head > script[type=application/ld+json]");
        var plainJson = body.outerHtml()
                            .substring(body.outerHtml()
                                           .indexOf('\n') + 1);

        ObjectMapper mapper = new ObjectMapper();
        Map dataMap;
        try {
            dataMap = mapper.readValue(plainJson, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new AccommodationDataDto(dataMap.get(NAME_KEY).toString(),
                                        dataMap.get(ADDRESS_KEY).toString(),
                                        dataMap.get(IMAGE_KEY).toString());
    }

    @Override
    public String getServiceName() {
        return BOOKING_URL;
    }
}
