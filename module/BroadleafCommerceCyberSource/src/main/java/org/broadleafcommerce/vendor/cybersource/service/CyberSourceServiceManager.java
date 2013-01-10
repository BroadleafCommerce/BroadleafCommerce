package org.broadleafcommerce.vendor.cybersource.service;

import java.util.List;

import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;

public interface CyberSourceServiceManager {

    public abstract List<CyberSourceService> getRegisteredServices();

    public abstract void setRegisteredServices(List<CyberSourceService> registeredServices);

    public abstract String getMerchantId();

    public abstract void setMerchantId(String merchantId);

    public abstract String getServerUrl();

    public abstract void setServerUrl(String serverUrl);

    public abstract String getLibVersion();

    public abstract void setLibVersion(String libVersion);

    public abstract CyberSourceService getValidService(CyberSourceRequest request);

}