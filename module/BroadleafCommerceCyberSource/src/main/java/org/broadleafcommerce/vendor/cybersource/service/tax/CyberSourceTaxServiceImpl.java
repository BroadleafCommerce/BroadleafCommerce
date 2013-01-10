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
package org.broadleafcommerce.vendor.cybersource.service.tax;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Comparator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.util.money.Money;
import org.broadleafcommerce.vendor.cybersource.service.AbstractCyberSourceService;
import org.broadleafcommerce.vendor.cybersource.service.api.BillTo;
import org.broadleafcommerce.vendor.cybersource.service.api.Item;
import org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals;
import org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage;
import org.broadleafcommerce.vendor.cybersource.service.api.TaxReply;
import org.broadleafcommerce.vendor.cybersource.service.api.TaxReplyItem;
import org.broadleafcommerce.vendor.cybersource.service.api.TaxService;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.message.CyberSourceRequest;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxItemRequest;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxItemResponse;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxRequest;
import org.broadleafcommerce.vendor.cybersource.service.tax.message.CyberSourceTaxResponse;
import org.broadleafcommerce.vendor.cybersource.service.type.CyberSourceServiceType;
import org.broadleafcommerce.vendor.service.cache.ServiceResponseCacheable;
import org.broadleafcommerce.vendor.service.exception.TaxException;
import org.broadleafcommerce.vendor.service.exception.TaxHostException;

import edu.emory.mathcs.backport.java.util.Arrays;

public class CyberSourceTaxServiceImpl extends AbstractCyberSourceService implements CyberSourceTaxService, ServiceResponseCacheable {
    
    private static final Log LOG = LogFactory.getLog(CyberSourceTaxServiceImpl.class);
    
    protected Cache cache = null;
    protected Boolean isCacheEnabled = false;

    public CyberSourceTaxResponse process(CyberSourceTaxRequest taxRequest) throws TaxException {
        //TODO add validation for the request
        if (isCacheEnabled) {
            synchronized(this) {
                if (cache == null) {
                    cache = CacheManager.getInstance().getCache("CyberSourceTaxRequests");
                }
            }
        }
        CyberSourceTaxResponse taxResponse = new CyberSourceTaxResponse();
        taxResponse.setServiceType(taxRequest.getServiceType());
        if (isCacheEnabled && cache.isKeyInCache(taxRequest.cacheKey())) {
            CyberSourceTaxResponse cachedResponse = (CyberSourceTaxResponse) cache.get(taxRequest.cacheKey()).getValue();
            buildResponse(taxResponse, cachedResponse, taxRequest);
        } else {
            ReplyMessage reply;
            RequestMessage request = buildRequestMessage(taxRequest);
            try {
                reply = sendRequest(request);
            } catch (Exception e) {
                incrementFailure();
                throw new TaxException(e);
            }
            clearStatus();
            buildResponse(taxResponse, reply, taxRequest);
            String[] invalidFields = reply.getInvalidField();
            String[] missingFields = reply.getMissingField();
            if ((invalidFields != null && invalidFields.length > 0) || (missingFields != null && missingFields.length > 0)) {
                TaxHostException e = new TaxHostException();
                taxResponse.setErrorDetected(true);
                StringBuffer sb = new StringBuffer();
                if (invalidFields != null && invalidFields.length > 0) {
                    sb.append("invalid fields :[ ");
                    for (String invalidField : invalidFields) {
                        sb.append(invalidField);
                    }
                    sb.append(" ]\n");
                }
                if (missingFields != null && missingFields.length > 0) {
                    sb.append("missing fields: [ ");
                    for (String missingField : missingFields) {
                        sb.append(missingField);
                    }
                    sb.append(" ]");
                }
                taxResponse.setErrorText(sb.toString());
                e.setTaxResponse(taxResponse);
                throw e;
            }
            if (isCacheEnabled && taxResponse.getDecision().equals("ACCEPT") && taxResponse.getReasonCode().equals(100)) {
                Element element = new Element(taxRequest.cacheKey(), taxResponse);
                cache.put(element);
            }
        }
        
        return taxResponse;
    }

    public boolean isValidService(CyberSourceRequest request) {
        return CyberSourceServiceType.TAX.equals(request.getServiceType());
    }
    
    public void clearCache() {
        cache.removeAll();
    }

    public Cache getCache() {
        return cache;
    }
    
    protected void buildResponse(CyberSourceTaxResponse taxResponse, CyberSourceTaxResponse cachedResponse, CyberSourceTaxRequest taxRequest) throws TaxException {
        if (
                cachedResponse.getCityRate() == null ||
                cachedResponse.getCountyRate() == null ||
                cachedResponse.getDistrictRate() == null ||
                cachedResponse.getTotalRate() == null
        ) {
            throw new TaxException("The cached response [merchant reference code: " + cachedResponse.getMerchantReferenceCode() + "] is missing one or more tax rates. Cannot use the cached response to calculate tax.");
        }
        taxResponse.setDecision("ACCEPT");
        taxResponse.setInvalidField(new String[]{});
        taxResponse.setMerchantReferenceCode(cachedResponse.getMerchantReferenceCode());
        taxResponse.setMissingField(new String[]{});
        taxResponse.setReasonCode(100);
        taxResponse.setRequestToken("from-cache");
        taxResponse.setPostalCode(cachedResponse.getPostalCode());
        CyberSourceTaxItemResponse[] itemResponses = new CyberSourceTaxItemResponse[taxRequest.getItemRequests().size()];
        int counter = 0;
        for (CyberSourceItemRequest itemRequest : taxRequest.getItemRequests()) {
            CyberSourceTaxItemResponse itemResponse = new CyberSourceTaxItemResponse();
            itemResponse.setId(itemRequest.getId());
            itemResponse.setCityTaxAmount(itemRequest.getUnitPrice().multiply(cachedResponse.getCityRate()));
            itemResponse.setCountyTaxAmount(itemRequest.getUnitPrice().multiply(cachedResponse.getCountyRate()));
            itemResponse.setDistrictTaxAmount(itemRequest.getUnitPrice().multiply(cachedResponse.getDistrictRate()));
            itemResponse.setTotalTaxAmount(itemRequest.getUnitPrice().multiply(cachedResponse.getTotalRate()));
            itemResponses[counter] = itemResponse;
            counter++;
        }
        taxResponse.setItemResponses(itemResponses);
    }
    
    protected void buildResponse(CyberSourceTaxResponse taxResponse, ReplyMessage reply, CyberSourceTaxRequest taxRequest) {
        logReply(reply);
        taxResponse.setDecision(reply.getDecision());
        taxResponse.setInvalidField(reply.getInvalidField());
        taxResponse.setMerchantReferenceCode(reply.getMerchantReferenceCode());
        taxResponse.setMissingField(reply.getMissingField());
        if (reply.getReasonCode() != null) {
            taxResponse.setReasonCode(reply.getReasonCode().intValue());
        }
        taxResponse.setRequestID(reply.getRequestID());
        taxResponse.setRequestToken(reply.getRequestToken());
        
        TaxReply taxReply = reply.getTaxReply();
        if (reply != null) {
            taxResponse.setCity(taxReply.getCity());
            taxResponse.setCounty(taxReply.getCounty());
            taxResponse.setCurrency(taxReply.getCurrency());
            taxResponse.setGeocode(taxReply.getGeocode());
            if (taxReply.getGrandTotalAmount() != null) {
                taxResponse.setGrandTotalAmount(new Money(taxReply.getGrandTotalAmount()));
            }
            taxResponse.setPostalCode(taxReply.getPostalCode());
            taxResponse.setState(taxReply.getState());
            if (taxReply.getTotalCityTaxAmount() != null) {
                taxResponse.setTotalCityTaxAmount(new Money(taxReply.getTotalCityTaxAmount()));
            }
            if (taxReply.getTotalCountyTaxAmount() != null) {
                taxResponse.setTotalCountyTaxAmount(new Money(taxReply.getTotalCountyTaxAmount()));
            }
            if (taxReply.getTotalDistrictTaxAmount() != null) {
                taxResponse.setTotalDistrictTaxAmount(new Money(taxReply.getTotalDistrictTaxAmount()));
            }
            if (taxReply.getTotalStateTaxAmount() != null) {
                taxResponse.setTotalStateTaxAmount(new Money(taxReply.getTotalStateTaxAmount()));
            }
            if (taxReply.getTotalTaxAmount() != null) {
                taxResponse.setTotalTaxAmount(new Money(taxReply.getTotalTaxAmount()));
            }
            TaxReplyItem[] replyItems = taxReply.getItem();
            setTaxRates(taxResponse, taxRequest, replyItems);
            if (replyItems != null) {
                CyberSourceTaxItemResponse[] itemResponses = new CyberSourceTaxItemResponse[replyItems.length];
                for (int j=0;j<replyItems.length;j++) {
                    TaxReplyItem replyItem = replyItems[j];
                    CyberSourceTaxItemResponse taxItem = new CyberSourceTaxItemResponse();
                    if (replyItem.getCityTaxAmount() != null) {
                        taxItem.setCityTaxAmount(new Money(replyItem.getCityTaxAmount()));
                    }
                    if (replyItem.getCountyTaxAmount() != null) {
                        taxItem.setCountyTaxAmount(new Money(replyItem.getCountyTaxAmount()));
                    }
                    if (replyItem.getDistrictTaxAmount() != null) {
                        taxItem.setDistrictTaxAmount(new Money(replyItem.getDistrictTaxAmount()));
                    }
                    taxItem.setId(replyItem.getId().longValue());
                    if (replyItem.getStateTaxAmount() != null) {
                        taxItem.setStateTaxAmount(new Money(replyItem.getStateTaxAmount()));
                    }
                    if (replyItem.getTotalTaxAmount() != null) {
                        taxItem.setTotalTaxAmount(new Money(replyItem.getTotalTaxAmount()));
                    }
                    itemResponses[j] = taxItem;
                }
                taxResponse.setItemResponses(itemResponses);
            }
        }
    }

    protected void setTaxRates(CyberSourceTaxResponse taxResponse, CyberSourceTaxRequest taxRequest, TaxReplyItem[] replyItems) {
        CyberSourceTaxItemRequest requestItem = (CyberSourceTaxItemRequest) taxRequest.getItemRequests().get(0);
        BigDecimal unitPrice = requestItem.getUnitPrice().getAmount();
        BigInteger requestId = new BigInteger(String.valueOf(requestItem.getId()));
        TaxReplyItem key = new TaxReplyItem();
        key.setId(requestId);
        int pos = Arrays.binarySearch(replyItems, key, new Comparator<TaxReplyItem>() {

            public int compare(TaxReplyItem one, TaxReplyItem two) {
                return one.getId().compareTo(two.getId());
            }
            
        });
        if (pos >= 0) {
            TaxReplyItem replyItem = replyItems[pos];
            if (replyItem.getCityTaxAmount() != null) {
                BigDecimal cityRate = new BigDecimal(replyItem.getCityTaxAmount()).divide(unitPrice, 5, RoundingMode.HALF_EVEN);
                taxResponse.setCityRate(cityRate);
            }
            if (replyItem.getCountyTaxAmount() != null) {
                BigDecimal countyRate = new BigDecimal(replyItem.getCountyTaxAmount()).divide(unitPrice, 5, RoundingMode.HALF_EVEN);
                taxResponse.setCountyRate(countyRate);
            }
            if (replyItem.getDistrictTaxAmount() != null) {
                BigDecimal districtRate = new BigDecimal(replyItem.getDistrictTaxAmount()).divide(unitPrice, 5, RoundingMode.HALF_EVEN);
                taxResponse.setDistrictRate(districtRate);
            }
            if (replyItem.getStateTaxAmount() != null) {
                BigDecimal stateRate = new BigDecimal(replyItem.getStateTaxAmount()).divide(unitPrice, 5, RoundingMode.HALF_EVEN);
                taxResponse.setStateRate(stateRate);
            }
            if (replyItem.getTotalTaxAmount() != null) {
                BigDecimal totalRate = new BigDecimal(replyItem.getTotalTaxAmount()).divide(unitPrice, 5, RoundingMode.HALF_EVEN);
                taxResponse.setTotalRate(totalRate);
            }
        }
    }

    protected RequestMessage buildRequestMessage(CyberSourceTaxRequest taxRequest) {
        RequestMessage request = new RequestMessage();
        request.setMerchantID(getMerchantId());
        request.setMerchantReferenceCode(getIdGenerationService().findNextId("org.broadleafcommerce.vendor.cybersource.service.CyberSourceTaxService").toString());
        request.setClientLibrary("Java Axis WSS4J");
        request.setClientLibraryVersion(getLibVersion());
        request.setClientEnvironment(
          System.getProperty("os.name") + "/" +
          System.getProperty("os.version") + "/" +
          System.getProperty("java.vendor") + "/" +
          System.getProperty("java.version")
        );
        
        PurchaseTotals purchaseTotals = new PurchaseTotals();
        purchaseTotals.setCurrency(taxRequest.getCurrency());
        if (taxRequest.getUseGrandTotal().booleanValue() && taxRequest.getGrandTotal() != null) {
            purchaseTotals.setGrandTotalAmount(taxRequest.getGrandTotal().toString());
        }
        request.setPurchaseTotals(purchaseTotals);
        
        setItemInformation(taxRequest, request);
        setBillingInformation(taxRequest, request);
        
        TaxService taxService = new TaxService();
        taxService.setNexus(taxRequest.getNexus());
        taxService.setNoNexus(taxRequest.getNoNexus());
        taxService.setOrderAcceptanceCity(taxRequest.getOrderAcceptanceCity());
        taxService.setOrderAcceptanceCountry(taxRequest.getOrderAcceptanceCountry());
        taxService.setOrderAcceptanceCounty(taxRequest.getOrderAcceptanceCounty());
        taxService.setOrderAcceptancePostalCode(taxRequest.getOrderAcceptancePostalCode());
        taxService.setOrderAcceptanceState(taxRequest.getOrderAcceptanceState());
        taxService.setOrderOriginCity(taxRequest.getOrderOriginCity());
        taxService.setOrderOriginCountry(taxRequest.getOrderOriginCountry());
        taxService.setOrderOriginCounty(taxRequest.getOrderOriginCounty());
        taxService.setOrderOriginPostalCode(taxRequest.getOrderOriginPostalCode());
        taxService.setOrderOriginState(taxRequest.getOrderOriginState());
        taxService.setRun("true");
        
        request.setTaxService(taxService);
        
        return request;
    }
    
    protected void setItemInformation(CyberSourceTaxRequest taxRequest, RequestMessage request) {
        Item[] items = new Item[taxRequest.getItemRequests().size()];
        for (int j=0;j<items.length;j++) {
            CyberSourceTaxItemRequest itemRequest = (CyberSourceTaxItemRequest) taxRequest.getItemRequests().get(j);
            items[j] = new Item();
            items[j].setId(new BigInteger(String.valueOf(itemRequest.getId())));
            if (itemRequest.getUnitPrice() != null) {
                items[j].setUnitPrice(itemRequest.getUnitPrice().toString());
            }
            if (itemRequest.getQuantity() != null) {
                items[j].setQuantity(new BigInteger(String.valueOf(itemRequest.getQuantity())));
            }
            if (itemRequest.getAlternateTaxAmount() != null) {
                items[j].setAlternateTaxAmount(itemRequest.getAlternateTaxAmount().toString());
            }
            items[j].setAlternateTaxID(itemRequest.getAlternateTaxID());
            if (itemRequest.getAlternateTaxRate() != null) {
                items[j].setAlternateTaxRate(itemRequest.getAlternateTaxRate().toString());
            }
            items[j].setAlternateTaxType(itemRequest.getAlternateTaxType());
            if (itemRequest.getAlternateTaxTypeApplied() != null) {
                items[j].setAlternateTaxTypeApplied(itemRequest.getAlternateTaxTypeApplied().toString());
            }
            items[j].setBuyerRegistration(itemRequest.getBuyerRegistration());
            if (itemRequest.getCityOverrideAmount() != null) {
                items[j].setCityOverrideAmount(itemRequest.getCityOverrideAmount().toString());
            }
            if (itemRequest.getCityOverrideRate() != null) {
                items[j].setCityOverrideRate(itemRequest.getCityOverrideRate().toString());
            }
            if (itemRequest.getCommodityCode() != null) {
                items[j].setCommodityCode(itemRequest.getCommodityCode().toString());
            }
            if (itemRequest.getCountryOverrideAmount() != null) {
                items[j].setCountryOverrideAmount(itemRequest.getCountryOverrideAmount().toString());
            }
            if (itemRequest.getCountryOverrideRate() != null) {
                items[j].setCountryOverrideRate(itemRequest.getCountryOverrideRate().toString());
            }
            if (itemRequest.getCountyOverrideAmount() != null) {
                items[j].setCountyOverrideAmount(itemRequest.getCountyOverrideAmount().toString());
            }
            if (itemRequest.getCountyOverrideRate() != null) {
                items[j].setCountyOverrideRate(itemRequest.getCountyOverrideRate().toString());
            }
            if (itemRequest.getDiscountAmount() != null) {
                items[j].setDiscountAmount(itemRequest.getDiscountAmount().toString());
            }
            items[j].setDiscountIndicator(itemRequest.getDiscountIndicator());
            if (itemRequest.getDiscountRate() != null) {
                items[j].setDiscountRate(itemRequest.getDiscountRate().toString());
            }
            if (itemRequest.getDistrictOverrideAmount() != null) {
                items[j].setDistrictOverrideAmount(itemRequest.getDistrictOverrideAmount().toString());
            }
            if (itemRequest.getDistrictOverrideRate() != null) {
                items[j].setDistrictOverrideRate(itemRequest.getDistrictOverrideRate().toString());
            }
            items[j].setExport(itemRequest.getExport());
            items[j].setGiftCategory(itemRequest.getGiftCategory());
            items[j].setGrossNetIndicator(itemRequest.getGrossNetIndicator());
            items[j].setHostHedge(itemRequest.getHostHedge());
            if (itemRequest.getLocalTax() != null) {
                items[j].setLocalTax(itemRequest.getLocalTax().toString());
            }
            if (itemRequest.getNationalTax() != null) {
                items[j].setNationalTax(itemRequest.getNationalTax().toString());
            }
            items[j].setNoExport(itemRequest.getNoExport());
            items[j].setNonsensicalHedge(itemRequest.getNonsensicalHedge());
            items[j].setObscenitiesHedge(itemRequest.getObscenitiesHedge());
            items[j].setOrderAcceptanceCity(itemRequest.getOrderAcceptanceCity());
            items[j].setOrderAcceptanceCountry(itemRequest.getOrderAcceptanceCountry());
            items[j].setOrderAcceptanceCounty(itemRequest.getOrderAcceptanceCounty());
            items[j].setOrderAcceptancePostalCode(itemRequest.getOrderAcceptancePostalCode());
            items[j].setOrderAcceptanceState(itemRequest.getOrderAcceptanceState());
            items[j].setOrderOriginCity(itemRequest.getOrderOriginCity());
            items[j].setOrderOriginCountry(itemRequest.getOrderOriginCountry());
            items[j].setOrderOriginCounty(itemRequest.getOrderOriginCounty());
            items[j].setOrderOriginPostalCode(itemRequest.getOrderOriginPostalCode());
            items[j].setOrderOriginState(itemRequest.getOrderOriginState());
            items[j].setPhoneHedge(itemRequest.getPhoneHedge());
            items[j].setPointOfTitleTransfer(itemRequest.getPointOfTitleTransfer());
            items[j].setProductCode(itemRequest.getProductCode());
            items[j].setProductName(itemRequest.getProductName());
            items[j].setProductRisk(itemRequest.getProductRisk());
            items[j].setProductSKU(itemRequest.getProductSKU());
            items[j].setSellerRegistration(itemRequest.getSellerRegistration());
            items[j].setShipFromCity(itemRequest.getShipFromCity());
            items[j].setShipFromCountry(itemRequest.getShipFromCountry());
            items[j].setShipFromCounty(itemRequest.getShipFromCounty());
            items[j].setShipFromPostalCode(itemRequest.getShipFromPostalCode());
            items[j].setShipFromState(itemRequest.getShipFromState());
            if (itemRequest.getStateOverrideAmount() != null) {
                items[j].setStateOverrideAmount(itemRequest.getStateOverrideAmount().toString());
            }
            if (itemRequest.getStateOverrideRate() != null) {
                items[j].setStateOverrideRate(itemRequest.getStateOverrideRate().toString());
            }
            if (itemRequest.getTaxAmount() != null) {
                items[j].setTaxAmount(itemRequest.getTaxAmount().toString());
            }
            if (itemRequest.getTaxRate() != null) {
                items[j].setTaxRate(itemRequest.getTaxRate().toString());
            }
            items[j].setTaxTypeApplied(itemRequest.getTaxTypeApplied());
            items[j].setTimeCategory(itemRequest.getTimeCategory());
            items[j].setTimeHedge(itemRequest.getTimeHedge());
            if (itemRequest.getTotalAmount() != null) {
                items[j].setTotalAmount(itemRequest.getTotalAmount().toString());
            }
            items[j].setUnitOfMeasure(itemRequest.getUnitOfMeasure());
            if (itemRequest.getVatRate() != null) {
                items[j].setVatRate(itemRequest.getVatRate().toString());
            }
            items[j].setVelocityHedge(itemRequest.getVelocityHedge());
            items[j].setZeroCostToCustomerIndicator(itemRequest.getZeroCostToCustomerIndicator());
        }
        request.setItem(items);
    }
    
    protected void setBillingInformation(CyberSourceTaxRequest taxRequest, RequestMessage request) {
        BillTo billTo = new BillTo();
        billTo.setCity(taxRequest.getBillingRequest().getCity());
        billTo.setCompany(taxRequest.getBillingRequest().getCompany());
        billTo.setCompanyTaxID(taxRequest.getBillingRequest().getCompanyTaxID());
        billTo.setCountry(taxRequest.getBillingRequest().getCountry());
        billTo.setCounty(taxRequest.getBillingRequest().getCounty());
        billTo.setDateOfBirth(taxRequest.getBillingRequest().getDateOfBirth());
        billTo.setDriversLicenseNumber(taxRequest.getBillingRequest().getDriversLicenseNumber());
        billTo.setDriversLicenseState(taxRequest.getBillingRequest().getDriversLicenseState());
        billTo.setEmail(taxRequest.getBillingRequest().getEmail());
        billTo.setFirstName(taxRequest.getBillingRequest().getFirstName());
        billTo.setIpAddress(taxRequest.getBillingRequest().getIpAddress());
        billTo.setIpNetworkAddress(taxRequest.getBillingRequest().getIpNetworkAddress());
        billTo.setLastName(taxRequest.getBillingRequest().getLastName());
        billTo.setMiddleName(taxRequest.getBillingRequest().getMiddleName());
        billTo.setPhoneNumber(taxRequest.getBillingRequest().getPhoneNumber());
        billTo.setPostalCode(taxRequest.getBillingRequest().getPostalCode());
        billTo.setSsn(taxRequest.getBillingRequest().getSsn());
        billTo.setState(taxRequest.getBillingRequest().getState());
        billTo.setStreet1(taxRequest.getBillingRequest().getStreet1());
        billTo.setStreet2(taxRequest.getBillingRequest().getStreet2());
        billTo.setStreet3(taxRequest.getBillingRequest().getStreet3());
        billTo.setStreet4(taxRequest.getBillingRequest().getStreet4());
        billTo.setSuffix(taxRequest.getBillingRequest().getSuffix());
        billTo.setTitle(taxRequest.getBillingRequest().getTitle());

        request.setBillTo( billTo );
    }
    
    protected void logReply(ReplyMessage reply) {
        if (LOG.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            sb.append("Decision: ");
            sb.append(reply.getDecision());
            sb.append("\nMerchant Reference Code: ");
            sb.append(reply.getMerchantReferenceCode());
            sb.append("\nInvalid Fields[]: ");
            if (reply.getInvalidField() != null) {
                for (String invalidField: reply.getInvalidField()) {
                    sb.append(invalidField);
                    sb.append(";");
                }
            }
            sb.append("\nMissing Fields[]: ");
            if (reply.getMissingField() != null) {
                for (String missingField: reply.getMissingField()) {
                    sb.append(missingField);
                    sb.append(";");
                }
            }
            sb.append("\nReason Code: ");
            sb.append(reply.getReasonCode());
            sb.append("\nRequest ID: ");
            sb.append(reply.getRequestID());
            sb.append("\nRequest Token: ");
            sb.append(reply.getRequestToken());
            
            TaxReply taxReply = reply.getTaxReply();
            if (taxReply != null) {
                sb.append("\nCity: ");
                sb.append(taxReply.getCity());
                sb.append("\nCounty: ");
                sb.append(taxReply.getCounty());
                sb.append("\nCurrency: ");
                sb.append(taxReply.getCurrency());
                sb.append("\nGeoCode: ");
                sb.append(taxReply.getGeocode());
                sb.append("\nGrand Total Amount: ");
                sb.append(taxReply.getGrandTotalAmount());
                sb.append("\nPostal Code: ");
                sb.append(taxReply.getPostalCode());
                sb.append("\nState: ");
                sb.append(taxReply.getState());
                sb.append("\nWarning! If response caching is enabled, the total tax values logged here may not be correct.");
                sb.append("\nTotal City Tax: ");
                sb.append(taxReply.getTotalCityTaxAmount());
                sb.append("\nTotal County Tax: ");
                sb.append(taxReply.getTotalCountyTaxAmount());
                sb.append("\nTotal District Tax: ");
                sb.append(taxReply.getTotalDistrictTaxAmount());
                sb.append("\nTotal State Tax: ");
                sb.append(taxReply.getTotalStateTaxAmount());
                sb.append("\nTotal Tax: ");
                sb.append(taxReply.getTotalTaxAmount());
                
                TaxReplyItem[] replies = taxReply.getItem();
                if (replies != null) {
                    for (TaxReplyItem item : replies) {
                        sb.append("\nITEM");
                        sb.append("\nCity Tax: ");
                        sb.append(item.getCityTaxAmount());
                        sb.append("\nCounty Tax: ");
                        sb.append(item.getCountyTaxAmount());
                        sb.append("\nDistrict Tax: ");
                        sb.append(item.getDistrictTaxAmount());
                        sb.append("\nState Tax: ");
                        sb.append(item.getStateTaxAmount());
                        sb.append("\nTotal Tax: ");
                        sb.append(item.getTotalTaxAmount());
                        sb.append("\nId: ");
                        sb.append(item.getId());
                    }
                }
            }
            
            LOG.debug("CyberSource Response:\n" + sb.toString());
        }
    }

    public Boolean isCacheEnabled() {
        return isCacheEnabled;
    }

    public void setIsCacheEnabled(Boolean isCacheEnabled) {
        this.isCacheEnabled = isCacheEnabled;
    }
}
