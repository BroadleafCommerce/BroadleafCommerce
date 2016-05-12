/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.TaxDetail;
import org.broadleafcommerce.core.order.service.OrderItemService;
import org.broadleafcommerce.core.order.service.call.FulfillmentGroupItemRequest;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around FulfillmentGroupItem.
 *
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.FulfillmentGroupItemWrapper}
 *
 * User: Elbert Bautista
 * Date: 4/10/12
 */
@Deprecated
@XmlRootElement(name = "fulfillmentGroupItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FulfillmentGroupItemWrapper extends BaseWrapper implements APIWrapper<FulfillmentGroupItem>, APIUnwrapper<FulfillmentGroupItemRequest> {

    @XmlElement
    protected Long id;

    @XmlElement
    protected Long fulfillmentGroupId;

    @XmlElement
    protected Long orderItemId;

    @XmlElement
    protected Money totalTax;

    @XmlElement
    protected Integer quantity;

    @XmlElement
    protected Money totalItemAmount;

    @XmlElement(name = "taxDetail")
    @XmlElementWrapper(name = "taxDetails")
    protected List<TaxDetailWrapper> taxDetails;

    @Override
    public void wrapDetails(FulfillmentGroupItem model, HttpServletRequest request) {
        this.id = model.getId();

        if (model.getFulfillmentGroup() != null) {
            this.fulfillmentGroupId = model.getFulfillmentGroup().getId();
        }

        if (model.getOrderItem() != null) {
            this.orderItemId = model.getOrderItem().getId();
        }

        this.totalTax = model.getTotalTax();
        this.quantity = model.getQuantity();
        this.totalItemAmount = model.getTotalItemAmount();

        List<TaxDetail> taxes = model.getTaxes();
        if (taxes != null && !taxes.isEmpty()) {
            this.taxDetails = new ArrayList<TaxDetailWrapper>();
            for (TaxDetail detail : taxes) {
                TaxDetailWrapper taxDetailWrapper = (TaxDetailWrapper) context.getBean(TaxDetailWrapper.class.getName());
                taxDetailWrapper.wrapSummary(detail, request);
                this.taxDetails.add(taxDetailWrapper);
            }
        }
    }

    @Override
    public void wrapSummary(FulfillmentGroupItem model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    @Override
    public FulfillmentGroupItemRequest unwrap(HttpServletRequest request, ApplicationContext appContext) {
        OrderItemService orderItemService = (OrderItemService) appContext.getBean("blOrderItemService");
        OrderItem orderItem = orderItemService.readOrderItemById(this.orderItemId);
        if (orderItem != null) {
            FulfillmentGroupItemRequest fulfillmentGroupItemRequest = new FulfillmentGroupItemRequest();
            fulfillmentGroupItemRequest.setOrderItem(orderItem);
            fulfillmentGroupItemRequest.setQuantity(this.quantity);
            return fulfillmentGroupItemRequest;
        }

        return null;
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the fulfillmentGroupId
     */
    public Long getFulfillmentGroupId() {
        return fulfillmentGroupId;
    }

    
    /**
     * @param fulfillmentGroupId the fulfillmentGroupId to set
     */
    public void setFulfillmentGroupId(Long fulfillmentGroupId) {
        this.fulfillmentGroupId = fulfillmentGroupId;
    }

    
    /**
     * @return the orderItemId
     */
    public Long getOrderItemId() {
        return orderItemId;
    }

    
    /**
     * @param orderItemId the orderItemId to set
     */
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    
    /**
     * @return the totalTax
     */
    public Money getTotalTax() {
        return totalTax;
    }

    
    /**
     * @param totalTax the totalTax to set
     */
    public void setTotalTax(Money totalTax) {
        this.totalTax = totalTax;
    }

    
    /**
     * @return the quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    
    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    
    /**
     * @return the totalItemAmount
     */
    public Money getTotalItemAmount() {
        return totalItemAmount;
    }

    
    /**
     * @param totalItemAmount the totalItemAmount to set
     */
    public void setTotalItemAmount(Money totalItemAmount) {
        this.totalItemAmount = totalItemAmount;
    }

    
    /**
     * @return the taxDetails
     */
    public List<TaxDetailWrapper> getTaxDetails() {
        return taxDetails;
    }

    
    /**
     * @param taxDetails the taxDetails to set
     */
    public void setTaxDetails(List<TaxDetailWrapper> taxDetails) {
        this.taxDetails = taxDetails;
    }
}
