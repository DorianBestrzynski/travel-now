package com.zpi.accommodationservice.accommodationservice.accomodation_strategy;
import com.google.maps.GeoApiContext;
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
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.*;
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.LONGITUDE_INDEX;
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.BOOKING_CSS_QUERY;
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.NEW_LINE;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.PARSE_ERROR_JSON;

@Component
@RequiredArgsConstructor
public class BookingExtractionStrategy implements AccommodationDataExtractionStrategy {
    private static final String BOOKING_URL = "booking.com";
    private final GeoApiContext context;
    @Override
    public AccommodationDataDto extractDataFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new SiteNotFoundException(e.getMessage());
        }

        Elements body = doc.select(BOOKING_CSS_QUERY);
        var plainJson = body.outerHtml().substring(body.outerHtml().indexOf(NEW_LINE) + 1);

        String name, sourceLink, imageLink, street, country, region;
        Double lat, lng;
        try {
            JSONObject json = new JSONObject(plainJson);
            name = json.getString(NAME_KEY);

            sourceLink = json.getString(URL);
            imageLink = json.getString(IMAGE_KEY);

            var address = json.getJSONObject(ADDRESS_KEY);
            street = address.getString(STREET_ADDRESS);
            country = address.getString(ADDRESS_COUNTRY);
            region = address.getString(ADDRESS_REGION);

            var coordinates = getStreetCoordinates(street);
            lat = coordinates[LATITUDE_INDEX];
            lng = coordinates[LONGITUDE_INDEX];
        }catch (JSONException ex){
            throw new JsonParseException(new Throwable(PARSE_ERROR_JSON));
        }

        return new AccommodationDataDto(name, street, country, region, imageLink, sourceLink, lat, lng);
    }
    @Override
    public String getServiceName() {
        return BOOKING_URL;
    }

    @Override
    public GeoApiContext context() {
        return context;
    }
}
