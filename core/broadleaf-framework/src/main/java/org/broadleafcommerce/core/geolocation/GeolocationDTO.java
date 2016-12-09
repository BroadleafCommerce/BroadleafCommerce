package org.broadleafcommerce.core.geolocation;

public class GeolocationDTO {

    protected String source;
    protected String ipAddress;
    protected String countryCode;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        builder.append("source:[" + source + "]; ")
                .append("ip address:[" + ipAddress + "]; ")
                .append("country code:[ " + countryCode + "]}");
        return builder.toString();
    }
}
