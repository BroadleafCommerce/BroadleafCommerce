/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.vendor.cybersource.service;

import java.util.List;

import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;

/**
 * 
 * @author jfischer
 *
 */
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
