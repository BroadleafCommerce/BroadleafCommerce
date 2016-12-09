package org.broadleafcommerce.core.geolocation;

public interface GeolocationService {
    GeolocationDTO getLocationData(String ipAddress);
}

