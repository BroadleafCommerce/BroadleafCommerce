package org.broadleafcommerce.util;

public interface WebserviceConsumer {
    String getWebserviceUrl();
    void setWebserviceUrl(String webserviceUrl);

    int getWebserviceTimeout();
    void setWebserviceTimeout(int webserviceTimeout);
}
