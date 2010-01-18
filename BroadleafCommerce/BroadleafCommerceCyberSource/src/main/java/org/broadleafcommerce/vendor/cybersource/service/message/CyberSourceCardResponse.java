package org.broadleafcommerce.vendor.cybersource.service.message;

public class CyberSourceCardResponse extends CyberSourcePaymentResponse {
	
	private static final long serialVersionUID = 1L;
	
	private CyberSourceAuthResponse authResponse;

	public CyberSourceAuthResponse getAuthResponse() {
		return authResponse;
	}

	public void setAuthResponse(CyberSourceAuthResponse authResponse) {
		this.authResponse = authResponse;
	}

}
