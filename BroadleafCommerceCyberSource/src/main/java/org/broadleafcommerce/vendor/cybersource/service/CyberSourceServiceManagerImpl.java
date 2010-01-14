package org.broadleafcommerce.vendor.cybersource.service;

import java.util.List;

import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;

public class CyberSourceServiceManagerImpl {

	private List<CyberSourceService> registeredServices;
	private String merchantId;
	private String serverUrl;
	
	public List<CyberSourceService> getRegisteredServices() {
		return registeredServices;
	}
	
	public void setRegisteredServices(List<CyberSourceService> registeredServices) {
		this.registeredServices = registeredServices;
	}
	
	public String getMerchantId() {
		return merchantId;
	}
	
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}
	
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	public CyberSourceService getValidService(CyberSourceRequest request) {
		for (CyberSourceService service : registeredServices) {
			if (service.isValidService(request)) {
				service.setMerchantId(merchantId);
				service.setServerUrl(serverUrl);
				return service;
			}
		}
		return null;
	}
}
