package com.zpi.accommodationservice.accommodationservice.accomodation_strategy;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.SiteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Component;
import java.io.IOException;
import static com.zpi.accommodationservice.accommodationservice.comons.BookingMapKeys.*;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.PARSE_ERROR_JSON;

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

        String name, bookingUrl, imageLink, street, country, region;
        try {
            JSONObject json = new JSONObject(plainJson);
            name = json.getString(NAME_KEY);
            bookingUrl = json.getString(URL);
            imageLink = json.getString(IMAGE_KEY);
            var address = json.getJSONObject(ADDRESS_KEY);
            street = address.getString(STREET_ADDRESS);
            country = address.getString(ADDRESS_COUNTRY);
            region = address.getString(ADDRESS_REGION);
        }catch (JSONException ex){
            throw new JsonParseException(new Throwable(PARSE_ERROR_JSON));
        }

        return new AccommodationDataDto(name, street, country, region, imageLink, bookingUrl);
    }

    @Override
    public String getServiceName() {
        return BOOKING_URL;
    }
}
