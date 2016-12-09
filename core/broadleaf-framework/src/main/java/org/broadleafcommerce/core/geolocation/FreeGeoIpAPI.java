package org.broadleafcommerce.core.geolocation;

import org.broadleafcommerce.core.geolocation.GeolocationAPI;
import org.broadleafcommerce.core.geolocation.GeolocationDTO;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component("blFreeGeoIpAPI")
public class FreeGeoIpAPI implements GeolocationAPI {

    public static final String JSON_API_URL = "http://freegeoip.net/json/";
    public static final String FRIENDLY_NAME = "Free Geo IP";

    @Override
    public GeolocationDTO populate(String address) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = objectMapper.readValue(new URL(JSON_API_URL + address), HashMap.class);

        GeolocationDTO geolocationDTO = new GeolocationDTO();
        geolocationDTO.setSource(FRIENDLY_NAME);
        geolocationDTO.setIpAddress((String) response.get("ip"));
        geolocationDTO.setCountryCode((String) response.get("country_code"));

        return geolocationDTO;
    }
}
