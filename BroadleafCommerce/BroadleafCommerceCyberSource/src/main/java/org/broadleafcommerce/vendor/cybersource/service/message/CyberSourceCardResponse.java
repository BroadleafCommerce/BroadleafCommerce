package org.broadleafcommerce.vendor.cybersource.service.message;


public class CyberSourceCardResponse extends CyberSourcePaymentResponse {
	
	private static final long serialVersionUID = 1L;
	
	private CyberSourceAuthResponse authResponse;
	private CyberSourceCaptureResponse captureResponse;
	private CyberSourceCreditResponse creditResponse;
	private CyberSourceVoidResponse voidResponse;

	public CyberSourceAuthResponse getAuthResponse() {
		return authResponse;
	}

	public void setAuthResponse(CyberSourceAuthResponse authResponse) {
		this.authResponse = authResponse;
	}

	public CyberSourceCaptureResponse getCaptureResponse() {
		return captureResponse;
	}

	public void setCaptureResponse(CyberSourceCaptureResponse captureResponse) {
		this.captureResponse = captureResponse;
	}

	public CyberSourceCreditResponse getCreditResponse() {
		return creditResponse;
	}

	public void setCreditResponse(CyberSourceCreditResponse creditResponse) {
		this.creditResponse = creditResponse;
	}

	public CyberSourceVoidResponse getVoidResponse() {
		return voidResponse;
	}

	public void setVoidResponse(CyberSourceVoidResponse voidResponse) {
		this.voidResponse = voidResponse;
	}

}
