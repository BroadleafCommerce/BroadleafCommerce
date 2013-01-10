package org.broadleafcommerce.vendor.cybersource.service;

import java.util.List;

import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;

public class CyberSourceServiceManagerImpl implements CyberSourceServiceManager {

    private List<CyberSourceService> registeredServices;
    private String merchantId;
    private String serverUrl;
    private String libVersion;
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#getRegisteredServices()
     */
    public List<CyberSourceService> getRegisteredServices() {
        return registeredServices;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#setRegisteredServices(java.util.List)
     */
    public void setRegisteredServices(List<CyberSourceService> registeredServices) {
        this.registeredServices = registeredServices;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#getMerchantId()
     */
    public String getMerchantId() {
        return merchantId;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#setMerchantId(java.lang.String)
     */
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#getServerUrl()
     */
    public String getServerUrl() {
        return serverUrl;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#setServerUrl(java.lang.String)
     */
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#getLibVersion()
     */
    public String getLibVersion() {
        return libVersion;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#setLibVersion(java.lang.String)
     */
    public void setLibVersion(String libVersion) {
        this.libVersion = libVersion;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager#getValidService(org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest)
     */
    public CyberSourceService getValidService(CyberSourceRequest request) {
        for (CyberSourceService service : registeredServices) {
            if (service.isValidService(request)) {
                service.setMerchantId(merchantId);
                service.setServerUrl(serverUrl);
                service.setLibVersion(libVersion);
                return service;
            }
        }
        return null;
    }
}
