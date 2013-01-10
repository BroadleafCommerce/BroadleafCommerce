package org.broadleafcommerce.vendor.cybersource.service;

import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;
import org.broadleafcommerce.vendor.service.type.ServiceStatusType;

public interface CyberSourceService {

    public ServiceStatusType getServiceStatus();

    public Integer getFailureReportingThreshold();

    public void setFailureReportingThreshold(Integer failureReportingThreshold);

    public String getServiceName();
    
    public boolean isValidService(CyberSourceRequest request);
    
    public String getMerchantId();
    
    public void setMerchantId(String merchantId);
    
    public String getServerUrl();
    
    public void setServerUrl(String serverUrl);
    
    public String getLibVersion();
    
    public void setLibVersion(String libVersion);
    
}
