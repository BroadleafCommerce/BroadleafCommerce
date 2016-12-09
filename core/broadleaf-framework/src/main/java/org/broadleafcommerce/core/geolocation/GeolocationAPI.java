package org.broadleafcommerce.core.geolocation;

import java.io.IOException;

public interface GeolocationAPI {
    GeolocationDTO populate(String address) throws IOException;
}
