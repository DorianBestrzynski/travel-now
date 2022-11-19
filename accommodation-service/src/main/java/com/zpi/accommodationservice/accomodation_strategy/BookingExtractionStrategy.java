package com.zpi.accommodationservice.accomodation_strategy;
import com.google.maps.GeoApiContext;
import com.zpi.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.exceptions.SiteNotFoundException;
import com.zpi.accommodationservice.comons.BookingMapKeys;
import com.zpi.accommodationservice.comons.Utils;
import com.zpi.accommodationservice.exceptions.ExceptionsInfo;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Component;
import java.io.IOException;

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

        Elements body = doc.select(Utils.BOOKING_CSS_QUERY);
        var plainJson = body.outerHtml().substring(body.outerHtml().indexOf(Utils.NEW_LINE) + 1);

        String name, sourceLink, imageLink, street, city, country, region;
        Double lat, lng;
        try {
            JSONObject json = new JSONObject(plainJson);
            name = json.getString(BookingMapKeys.NAME_KEY);

            sourceLink = json.getString(BookingMapKeys.URL);
            imageLink = json.getString(BookingMapKeys.IMAGE_KEY);

            var address = json.getJSONObject(BookingMapKeys.ADDRESS_KEY);
            street = address.getString(BookingMapKeys.STREET_ADDRESS);
            city = getCity(street);
            country = address.getString(BookingMapKeys.ADDRESS_COUNTRY);
            region = address.getString(BookingMapKeys.ADDRESS_REGION);

            var coordinates = getStreetCoordinates(street);
            lat = coordinates[Utils.LATITUDE_INDEX];
            lng = coordinates[Utils.LONGITUDE_INDEX];
        }catch (JSONException ex){
            throw new JsonParseException(new Throwable(ExceptionsInfo.PARSE_ERROR_JSON));
        }

        return new AccommodationDataDto(name, street, city, country, region, imageLink, sourceLink, lat, lng);
    }

    private String getCity(String street) {
        var removedLastComma = street.substring(0, street.lastIndexOf(","));
        var cityAndCode = removedLastComma.substring(removedLastComma.lastIndexOf(",") + 1);
        return cityAndCode.replaceAll("\\d", "").replace("-","").replaceAll("\\s", "");

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
