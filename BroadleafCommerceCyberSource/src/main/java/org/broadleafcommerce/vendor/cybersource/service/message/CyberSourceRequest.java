package org.broadleafcommerce.vendor.cybersource.service.message;

import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceTransactionType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceVenueType;

public abstract class CyberSourceRequest implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private CyberSourceTransactionType transactionType;
	private CyberSourceServiceType serviceType;
	private CyberSourceVenueType venueType;
	
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

	public CyberSourceVenueType getVenueType() {
		return venueType;
	}

	public void setVenueType(CyberSourceVenueType venueType) {
		this.venueType = venueType;
	}
	
}
