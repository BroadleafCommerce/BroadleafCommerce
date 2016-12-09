package org.broadleafcommerce.core.geolocation;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component("blIpApiAPI")
public class IpApiAPI implements GeolocationAPI {

    public static final String JSON_API_URL = "http://ip-api.com/json/";
    public static final String FRIENDLY_NAME = "IP API";

    @Override
    public GeolocationDTO populate(String address) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = objectMapper.readValue(new URL(JSON_API_URL + address), HashMap.class);

        GeolocationDTO geolocationDTO = new GeolocationDTO();
        geolocationDTO.setSource(FRIENDLY_NAME);
        geolocationDTO.setIpAddress((String) response.get("query"));
        geolocationDTO.setCountryCode((String) response.get("countryCode"));

        return geolocationDTO;
    }
}
