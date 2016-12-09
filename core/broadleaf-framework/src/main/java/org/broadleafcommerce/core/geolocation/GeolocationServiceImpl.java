package org.broadleafcommerce.core.geolocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service("blGeolocationService")
public class GeolocationServiceImpl implements GeolocationService {

    @Value("${geolocation.api}")
    protected String selectedAPI;

    @Autowired
    protected Map<String, GeolocationAPI> geolocationMap;

    @Override
    public GeolocationDTO getLocationData(String ipAddress) {
        GeolocationAPI api = geolocationMap.get(selectedAPI);
        GeolocationDTO data = null;
        if (api != null) {
            try {
                data = api.populate(ipAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return data;
    }
}
