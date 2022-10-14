package com.zpi.accommodationservice.accommodationservice.accomodation_strategy;


import com.google.maps.GeoApiContext;
import com.zpi.accommodationservice.accommodationservice.dto.AccommodationDataDto;
import com.zpi.accommodationservice.accommodationservice.exceptions.DataExtractionNotSupported;
import com.zpi.accommodationservice.accommodationservice.exceptions.SiteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

import static com.zpi.accommodationservice.accommodationservice.comons.AirbnbMapKeys.*;
import static com.zpi.accommodationservice.accommodationservice.comons.Utils.*;
import static com.zpi.accommodationservice.accommodationservice.exceptions.ExceptionsInfo.PARSE_ERROR_JSON;

@Component
@RequiredArgsConstructor
public class AirbnbExtractionStrategy implements AccommodationDataExtractionStrategy {

    private static final String BOOKING_URL = "airbnb.";
    @Qualifier("airbnbRegexPattern")
    private final Pattern pattern;

    @Qualifier("context")
    private final GeoApiContext context;

    @Override
    public AccommodationDataDto extractDataFromUrl(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                       .get();
        } catch (IOException e) {
            throw new SiteNotFoundException(e.getMessage());
        }

        Elements accommodationHtmlElem = doc.select(AIRBNB_CSS_QUERY);
        var tittlePlainJson = extractPlainJson(accommodationHtmlElem.outerHtml());

        String name, sourceLink, imageLink, street, city, country, region;
        Double lat, lng;
        try {
            JSONObject json = new JSONObject(tittlePlainJson);

            int sectionIndex = findSectionWithData(json);
            name = json.query(NAME_KEY.replace(SECTION_INDEX, String.valueOf(sectionIndex))).toString();

            sourceLink = url;
            imageLink = json.query(IMAGE_KEY.replace(SECTION_INDEX, String.valueOf(sectionIndex))).toString();

            var address = json.query(ADDRESS_KEY).toString();
            var addressData = extractAddressData(address);
            street = addressData[0];
            city = getCity(street);
            region = addressData[1];
            country = addressData[2];

            var coordinates = getStreetCoordinates(street);
            lat = coordinates[LATITUDE_INDEX];
            lng = coordinates[LONGITUDE_INDEX];

        } catch (JSONException ex) {
            throw new JsonParseException(new Throwable(PARSE_ERROR_JSON));
        }
        
        return new AccommodationDataDto(name, street, city, country, region, imageLink, sourceLink, lat, lng);
    }

    private String getCity(String street) {
        return street.substring(0, street.indexOf(","));
    }

    private String extractPlainJson(String html) {
        var matcher = pattern.matcher(html);

        if (matcher.find())
            return matcher.group();

        throw new DataExtractionNotSupported("Cannot extract json from html");
    }

    private String[] extractAddressData(String address) {
        var streetAddress = address.substring(address.indexOf(COLON) + 1).strip();
        address = streetAddress.replaceAll(WHITESPACE, EMPTY_STRING);

        var result = address.split(COMMA);
        result[0] = streetAddress;
        return result;
    }

    private int findSectionWithData(JSONObject json) {
        var sections = (JSONArray) json.query(SECTIONS_KEY);

        for (int i = 0; i < sections.length(); i++) {
            var section = sections.getJSONObject(i);
            if (section.has(SECTION)) {
                var sectionData = section.getJSONObject(SECTION);
                if (sectionData.has(SHARE_SAVE) && section.get(SECTION_DEPENDENCIES).toString().equals(NULL_STRING)) {
                    try {
                        var sectionWithName = sectionData.getJSONObject(SHARE_SAVE);
                    } catch (JSONException ignored) {
                        continue;
                    }
                    return i;
                }
            }
        }

        return -1;
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
