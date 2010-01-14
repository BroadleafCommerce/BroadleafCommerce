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

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.configuration.FileProvider;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.broadleafcommerce.profile.service.IdGenerationService;
import org.broadleafcommerce.vendor.cybersource.service.api.BillTo;
import org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessorStub;
import org.broadleafcommerce.vendor.cybersource.service.api.Item;
import org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals;
import org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.TransactionProcessorLocator;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourcePaymentRequest;
import org.broadleafcommerce.vendor.service.monitor.ServiceStatusDetectable;
import org.broadleafcommerce.vendor.service.type.ServiceStatusType;

public abstract class AbstractCyberSourcePaymentService implements ServiceStatusDetectable {
	
	private String merchantId;
	private String serverUrl;
	private IdGenerationService idGenerationService;
	private String libVersion;
	protected Integer failureReportingThreshold;
    protected Integer failureCount = 0;
    protected Boolean isUp = true;
    
    protected void clearStatus() {
        synchronized(failureCount) {
            isUp = true;
            failureCount = 0;
        }
    }

    protected void incrementFailure() {
        synchronized(failureCount) {
            if (failureCount >= failureReportingThreshold) {
                isUp = false;
            } else {
                failureCount++;
            }
        }
    }
	
	public Integer getFailureReportingThreshold() {
		return failureReportingThreshold;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public String getServerUrl() {
		return serverUrl;
	}
	
	public String getLibVersion() {
		return libVersion;
	}

	public String getServiceName() {
		return getClass().getName();
	}

	public ServiceStatusType getServiceStatus() {
		synchronized(failureCount) {
            if (isUp) {
                return ServiceStatusType.UP;
            } else {
                return ServiceStatusType.DOWN;
            }
        }
	}

	public void setFailureReportingThreshold(Integer failureReportingThreshold) {
		this.failureReportingThreshold = failureReportingThreshold;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	public void setLibVersion(String libVersion) {
		this.libVersion = libVersion;
	}
	
	public IdGenerationService getIdGenerationService() {
		return idGenerationService;
	}
	
	public void setIdGenerationService(IdGenerationService idGenerationService) {
		this.idGenerationService = idGenerationService;
	}
	
	protected ReplyMessage sendRequest(RequestMessage request) throws AxisFault, MalformedURLException, RemoteException, ServiceException {
		EngineConfiguration config = new FileProvider("CyberSourceDeploy.wsdd");
		TransactionProcessorLocator service = new TransactionProcessorLocator(config);
        URL endpoint = new URL(serverUrl);
        ITransactionProcessorStub stub = (ITransactionProcessorStub) service.getportXML(endpoint);
        stub._setProperty(WSHandlerConstants.USER, request.getMerchantID());
        ReplyMessage reply = stub.runTransaction(request);
        
        return reply;
	}
	
	protected RequestMessage buildRequestMessage(CyberSourcePaymentRequest paymentRequest) {
		RequestMessage request = new RequestMessage();
        request.setMerchantID(merchantId);
        request.setMerchantReferenceCode(idGenerationService.findNextId("org.broadleafcommerce.vendor.cybersource.service.CyberSourceService").toString());
        request.setClientLibrary("Java Axis WSS4J");
        request.setClientLibraryVersion(libVersion);
        request.setClientEnvironment(
          System.getProperty("os.name") + "/" +
          System.getProperty("os.version") + "/" +
          System.getProperty("java.vendor") + "/" +
          System.getProperty("java.version")
        );
        
        setBillingInformation(paymentRequest, request);
        PurchaseTotals purchaseTotals = new PurchaseTotals();
        purchaseTotals.setCurrency(paymentRequest.getCurrency());
        request.setPurchaseTotals(purchaseTotals);
        Item[] items = new Item[paymentRequest.getItemRequests().size()];
        for (int j=0;j<items.length;j++) {
        	items[j] = new Item();
        	items[j].setId(new BigInteger(String.valueOf(paymentRequest.getItemRequests().get(j).getId())));
        	items[j].setUnitPrice(paymentRequest.getItemRequests().get(j).getUnitPrice().toString());
        	items[j].setQuantity(new BigInteger(String.valueOf(paymentRequest.getItemRequests().get(j).getQuantity())));
        }
        request.setItem(items);
        return request;
	}
	
	protected void setBillingInformation(CyberSourcePaymentRequest paymentRequest, RequestMessage request) {
		BillTo billTo = new BillTo();
		billTo.setCity(paymentRequest.getBillingRequest().getCity());
		billTo.setCompany(paymentRequest.getBillingRequest().getCompany());
		billTo.setCompanyTaxID(paymentRequest.getBillingRequest().getCompanyTaxID());
		billTo.setCountry(paymentRequest.getBillingRequest().getCountry());
		billTo.setCounty(paymentRequest.getBillingRequest().getCounty());
		billTo.setDateOfBirth(paymentRequest.getBillingRequest().getDateOfBirth());
		billTo.setDriversLicenseNumber(paymentRequest.getBillingRequest().getDriversLicenseNumber());
		billTo.setDriversLicenseState(paymentRequest.getBillingRequest().getDriversLicenseState());
		billTo.setEmail(paymentRequest.getBillingRequest().getEmail());
		billTo.setFirstName(paymentRequest.getBillingRequest().getFirstName());
		billTo.setIpAddress(paymentRequest.getBillingRequest().getIpAddress());
		billTo.setIpNetworkAddress(paymentRequest.getBillingRequest().getIpNetworkAddress());
		billTo.setLastName(paymentRequest.getBillingRequest().getLastName());
		billTo.setMiddleName(paymentRequest.getBillingRequest().getMiddleName());
		billTo.setPhoneNumber(paymentRequest.getBillingRequest().getPhoneNumber());
		billTo.setPostalCode(paymentRequest.getBillingRequest().getPostalCode());
		billTo.setSsn(paymentRequest.getBillingRequest().getSsn());
		billTo.setState(paymentRequest.getBillingRequest().getState());
		billTo.setStreet1(paymentRequest.getBillingRequest().getStreet1());
		billTo.setStreet2(paymentRequest.getBillingRequest().getStreet2());
		billTo.setStreet3(paymentRequest.getBillingRequest().getStreet3());
		billTo.setStreet4(paymentRequest.getBillingRequest().getStreet4());
		billTo.setSuffix(paymentRequest.getBillingRequest().getSuffix());
		billTo.setTitle(paymentRequest.getBillingRequest().getTitle());

        request.setBillTo( billTo );
	}

}
