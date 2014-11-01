/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM_ADJUSTMENT")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OrderItemAdjustmentImpl_baseOrderItemAdjustment")
public class OrderItemAdjustmentImpl implements OrderItemAdjustment, CurrencyCodeIdentifiable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OrderItemAdjustmentId")
    @GenericGenerator(
        name="OrderItemAdjustmentId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderItemAdjustmentImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl")
        }
    )
    @Column(name = "ORDER_ITEM_ADJUSTMENT_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    @Index(name="OIADJUST_ITEM_INDEX", columnNames={"ORDER_ITEM_ID"})
    @AdminPresentation(excluded = true)
    protected OrderItem orderItem;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    @Index(name="OIADJUST_OFFER_INDEX", columnNames={"OFFER_ID"})
    @AdminPresentation(friendlyName = "OrderItemAdjustmentImpl_Offer", order=1000,
            group = "OrderItemAdjustmentImpl_Description", prominent = true, gridOrder = 1000)
    @AdminPresentationToOneLookup()
    protected Offer offer;

    @Column(name = "ADJUSTMENT_REASON", nullable=false)
    @AdminPresentation(friendlyName = "OrderItemAdjustmentImpl_Item_Adjustment_Reason", order=2000)
    protected String reason;

    @Column(name = "ADJUSTMENT_VALUE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderItemAdjustmentImpl_Item_Adjustment_Value", order=3000,
            fieldType = SupportedFieldType.MONEY, prominent = true,
            gridOrder = 2000)
    protected BigDecimal value = Money.ZERO.getAmount();

    @Column(name = "APPLIED_TO_SALE_PRICE")
    @AdminPresentation(friendlyName = "OrderItemAdjustmentImpl_Apply_To_Sale_Price", order=4000)
    protected boolean appliedToSalePrice;
    
    @Transient
    protected Money retailValue;

    @Transient
    protected Money salesValue;


    @Override
    public void init(OrderItem orderItem, Offer offer, String reason){
        this.orderItem = orderItem;
        this.offer = offer;
        this.reason = reason;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public OrderItem getOrderItem() {
        return orderItem;
    }

    @Override
    public Offer getOffer() {
        return offer;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public Money getValue() {
        return value == null ? null : BroadleafCurrencyUtils.getMoney(value, getOrderItem().getOrder().getCurrency());
    }
    
    @Override
    public void setValue(Money value) {
        this.value = value.getAmount();
    }

    @Override
    public boolean isAppliedToSalePrice() {
        return appliedToSalePrice;
    }

    @Override
    public void setAppliedToSalePrice(boolean appliedToSalePrice) {
        this.appliedToSalePrice = appliedToSalePrice;
    }

    @Override
    public Money getRetailPriceValue() {
        if (retailValue == null) {
            return BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrderItem().getOrder().getCurrency());
        }
        return this.retailValue;
    }

    @Override
    public void setRetailPriceValue(Money retailPriceValue) {
        this.retailValue = retailPriceValue;
    }

    @Override
    public Money getSalesPriceValue() {
        if (salesValue == null) {
            return BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrderItem().getOrder().getCurrency());
        }
        return salesValue;
    }

    @Override
    public void setSalesPriceValue(Money salesPriceValue) {
        this.salesValue = salesPriceValue;
    }

    @Override
    public String getCurrencyCode() {
        return ((CurrencyCodeIdentifiable) orderItem).getCurrencyCode();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((orderItem == null) ? 0 : orderItem.hashCode());
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        OrderItemAdjustmentImpl other = (OrderItemAdjustmentImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (offer == null) {
            if (other.offer != null) {
                return false;
            }
        } else if (!offer.equals(other.offer)) {
            return false;
        }
        if (orderItem == null) {
            if (other.orderItem != null) {
                return false;
            }
        } else if (!orderItem.equals(other.orderItem)) {
            return false;
        }
        if (reason == null) {
            if (other.reason != null) {
                return false;
            }
        } else if (!reason.equals(other.reason)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
