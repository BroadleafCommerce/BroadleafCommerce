package org.broadleafcommerce.vendor.cybersource.service;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourcePaymentRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourcePaymentResponse;
import org.broadleafcommerce.vendor.service.exception.PaymentException;
import org.broadleafcommerce.vendor.service.type.ServiceStatusType;

public class CyberSourcePaymentServiceImpl implements CyberSourcePaymentService {
	
	private String merchantId;
	private String transactionKey;
	private String serverUrl;

	public CyberSourcePaymentResponse authorize(CyberSourcePaymentRequest request) throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public CyberSourcePaymentResponse capture(CyberSourcePaymentRequest request)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public CyberSourcePaymentResponse credit(CyberSourcePaymentRequest request)
			throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CyberSourcePaymentResponse voidTransaction(CyberSourcePaymentRequest request) throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CyberSourcePaymentResponse reverseAuthorize(CyberSourcePaymentRequest request) throws PaymentException {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getFailureReportingThreshold() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public String getServiceName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceStatusType getServiceStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTransactionKey() {
		return transactionKey;
	}

	public void setFailureReportingThreshold(Integer failureReportingThreshold) {
		// TODO Auto-generated method stub
		
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setTransactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
	}
	
	private class SamplePWCallback implements CallbackHandler {
	    
	    /**
	     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	     */
	    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
	        for (int i = 0; i < callbacks.length; i++) {
	            if (callbacks[i] instanceof WSPasswordCallback) {
	                WSPasswordCallback pc = (WSPasswordCallback)callbacks[i];

	                // This sample returns one password for all merchants.
	                // To support multiple passwords, return the password
	                // corresponding to pc.getIdentifier().
	                pc.setPassword(transactionKey);
	            } else {
	                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
	            }
	        }
	    }
	}

}
