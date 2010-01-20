package org.broadleafcommerce.vendor.cybersource.service.message;

import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceMethodType;

public class CyberSourceResponse implements java.io.Serializable {

private static final long serialVersionUID = 1L;
	
	private CyberSourceTransactionType transactionType;
	private CyberSourceServiceType serviceType;
	private CyberSourceMethodType methodType;
	
	public CyberSourceTransactionType getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(CyberSourceTransactionType transactionType) {
		this.transactionType = transactionType;
	}
	
	public CyberSourceServiceType getServiceType() {
		return serviceType;
	}
	
	public void setServiceType(CyberSourceServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public CyberSourceMethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(CyberSourceMethodType venueType) {
		this.methodType = venueType;
	}
	
}
