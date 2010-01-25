package org.broadleafcommerce.vendor.cybersource.service.message;

import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;

public abstract class CyberSourceRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private CyberSourceServiceType serviceType;
	
	public CyberSourceServiceType getServiceType() {
		return serviceType;
	}
	
	public void setServiceType(CyberSourceServiceType serviceType) {
		this.serviceType = serviceType;
	}

}
