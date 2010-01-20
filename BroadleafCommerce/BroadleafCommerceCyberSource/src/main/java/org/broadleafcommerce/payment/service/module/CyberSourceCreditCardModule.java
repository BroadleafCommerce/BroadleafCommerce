package org.broadleafcommerce.payment.service.module;

import java.util.Currency;
import java.util.Date;

import org.broadleafcommerce.payment.domain.CreditCardPaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.PaymentResponseItem;
import org.broadleafcommerce.payment.domain.PaymentResponseItemImpl;
import org.broadleafcommerce.payment.service.PaymentContext;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourcePaymentService;
import org.broadleafcommerce.vendor.cybersource.service.CyberSourceServiceManager;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceBillingRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceCardRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceCardResponse;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceMethodType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceTransactionType;

public class CyberSourceCreditCardModule implements PaymentModule {
	
	private CyberSourceServiceManager serviceManager;

	public PaymentResponseItem authorize(PaymentContext paymentContext) throws PaymentException {
		CyberSourceCardRequest cardRequest = createCardRequest(paymentContext);
        cardRequest.setTransactionType(CyberSourceTransactionType.AUTHORIZE);
        Currency currency = paymentContext.getPaymentInfo().getAmount().getCurrency();
        if (currency == null) {
        	currency = Money.defaultCurrency();
        }
        cardRequest.setCurrency(currency.getCurrencyCode());
        
        CyberSourceBillingRequest billingRequest = createBillingRequest(paymentContext);
        cardRequest.setBillingRequest(billingRequest);
        
        CyberSourceItemRequest itemRequest = createItemRequest(paymentContext);
        cardRequest.getItemRequests().add(itemRequest);

        CyberSourcePaymentService service = (CyberSourcePaymentService) serviceManager.getValidService(cardRequest);
        CyberSourceCardResponse response;
		try {
			response = (CyberSourceCardResponse) service.process(cardRequest);
		} catch (org.broadleafcommerce.vendor.service.exception.PaymentException e) {
			throw new PaymentException(e);
		}
		
		PaymentResponseItem responseItem = new PaymentResponseItemImpl();
		responseItem.setTransactionTimestamp(new Date());
		responseItem.setProcessorResponseCode(response.getAuthResponse().getProcessorResponse());
		responseItem.setProcessorResponseText(response.getAuthResponse().getProcessorResponse());
		responseItem.setMiddlewareResponseCode(response.getReasonCode().toString());
		responseItem.setMiddlewareResponseText(response.getDecision());
		responseItem.setReferenceNumber(response.getMerchantReferenceCode());
		responseItem.setTransactionId(response.getRequestToken());
		responseItem.setAvsCode(response.getAuthResponse().getAvsCode());
		responseItem.setAuthorizationCode(response.getAuthResponse().getAuthorizationCode());
		responseItem.setTransactionSuccess(response.getReasonCode().intValue() == 100);
		responseItem.setAmountPaid(response.getAuthResponse().getAmount());
        
        return responseItem;
	}
	
	public PaymentResponseItem authorizeAndDebit(PaymentContext paymentContext) throws PaymentException {
		throw new PaymentException("authorize and debit not yet supported");
	}

	public PaymentResponseItem balance(PaymentContext paymentContext) throws PaymentException {
		throw new PaymentException("balance not supported");
	}

	public PaymentResponseItem credit(PaymentContext paymentContext) throws PaymentException {
		throw new PaymentException("credit not yet supported");
	}

	public PaymentResponseItem debit(PaymentContext paymentContext) throws PaymentException {
		throw new PaymentException("debit not yet supported");
	}

	public Boolean isValidCandidate(PaymentInfoType paymentType) {
		return PaymentInfoType.CREDIT_CARD.equals(paymentType);
	}

	public PaymentResponseItem voidPayment(PaymentContext paymentContext) throws PaymentException {
		throw new PaymentException("voidPayment not yet supported");
	}
	
	private CyberSourceCardRequest createCardRequest(PaymentContext paymentContext) {
		CyberSourceCardRequest cardRequest = new CyberSourceCardRequest();
		cardRequest.setServiceType(CyberSourceServiceType.PAYMENT);
        cardRequest.setMethodType(CyberSourceMethodType.CREDITCARD);
        CreditCardPaymentInfo ccInfo = (CreditCardPaymentInfo) paymentContext.getReferencedPaymentInfo();
        cardRequest.setAccountNumber(ccInfo.getPan());
        cardRequest.setExpirationMonth(ccInfo.getExpirationMonth());
        cardRequest.setExpirationYear(ccInfo.getExpirationYear());
        
        return cardRequest;
	}
	
	private CyberSourceItemRequest createItemRequest(PaymentContext paymentContext) {
		CyberSourceItemRequest itemRequest = new CyberSourceItemRequest();
        itemRequest.setDescription("Order Charge");
        itemRequest.setId(1L);
        itemRequest.setQuantity(1L);
        itemRequest.setShortDescription("Order Charge");
        itemRequest.setUnitPrice(paymentContext.getPaymentInfo().getAmount());
        
        return itemRequest;
	}
	
	private CyberSourceBillingRequest createBillingRequest(PaymentContext paymentContext) {
		CyberSourceBillingRequest billingRequest = new CyberSourceBillingRequest();
		PaymentInfo info = paymentContext.getPaymentInfo();
		Address address = info.getAddress();
		billingRequest.setCity(address.getCity());
		billingRequest.setCountry(address.getCountry().getAbbreviation());
		billingRequest.setCounty(address.getCounty());
		billingRequest.setEmail(info.getOrder().getEmailAddress());
		billingRequest.setFirstName(address.getFirstName());
		billingRequest.setIpAddress(info.getCustomerIpAddress());
		billingRequest.setLastName(address.getLastName());
		billingRequest.setPhoneNumber(address.getPrimaryPhone());
		billingRequest.setPostalCode(address.getPostalCode());
		billingRequest.setState(address.getState().getAbbreviation());
		billingRequest.setStreet1(address.getAddressLine1());
		billingRequest.setStreet2(address.getAddressLine2());
		
		return billingRequest;
	}

	public CyberSourceServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(CyberSourceServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

}
