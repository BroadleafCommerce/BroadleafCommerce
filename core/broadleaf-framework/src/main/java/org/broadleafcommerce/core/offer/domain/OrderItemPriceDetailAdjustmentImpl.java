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

import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.DefaultPostLoaderDao;
import org.broadleafcommerce.common.persistence.PostLoaderDao;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM_DTL_ADJ")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
public class OrderItemPriceDetailAdjustmentImpl implements OrderItemPriceDetailAdjustment, CurrencyCodeIdentifiable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemPriceDetailAdjustmentId")
    @GenericGenerator(
        name = "OrderItemPriceDetailAdjustmentId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name = "segment_value", value = "OrderItemPriceDetailAdjustmentImpl"),
            @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustmentImpl")
        }
    )
    @Column(name = "ORDER_ITEM_DTL_ADJ_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderItemPriceDetailImpl.class)
    @JoinColumn(name = "ORDER_ITEM_PRICE_DTL_ID")
    @AdminPresentation(excluded = true)
    protected OrderItemPriceDetail orderItemPriceDetail;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    @AdminPresentation(friendlyName = "OrderItemPriceDetailAdjustmentImpl_Offer", order=1000,
            prominent = true, gridOrder = 1000)
    @AdminPresentationToOneLookup()
    protected Offer offer;

    @Column(name = "OFFER_NAME")
    protected String offerName;

    @Column(name = "ADJUSTMENT_REASON", nullable=false)
    @AdminPresentation(friendlyName = "OrderItemPriceDetailAdjustmentImpl_reason", order = 1,
            group = "OrderItemPriceDetailAdjustmentImpl_Description")
    protected String reason;

    @Column(name = "ADJUSTMENT_VALUE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderItemPriceDetailAdjustmentImpl_value", order = 2,
            group = "OrderItemPriceDetailAdjustmentImpl_Description", fieldType = SupportedFieldType.MONEY, prominent = true)
    protected BigDecimal value = Money.ZERO.getAmount();

    @Column(name = "APPLIED_TO_SALE_PRICE")
    @AdminPresentation(friendlyName = "OrderItemPriceDetailAdjustmentImpl_appliedToSalePrice", order = 3,
            group = "OrderItemPriceDetailAdjustmentImpl_Description")
    protected boolean appliedToSalePrice;
    
    @Transient
    protected Money retailValue;

    @Transient
    protected Money salesValue;

    @Transient
    protected Offer deproxiedOffer;

    @Override
    public void init(OrderItemPriceDetail orderItemPriceDetail, Offer offer, String reason) {
        setOrderItemPriceDetail(orderItemPriceDetail);
        setOffer(offer);
        setReason(reason);
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
    public OrderItemPriceDetail getOrderItemPriceDetail() {
        return orderItemPriceDetail;
    }

    @Override
    public Offer getOffer() {
        if (deproxiedOffer == null) {
            PostLoaderDao postLoaderDao = DefaultPostLoaderDao.getPostLoaderDao();

            if (postLoaderDao != null) {
                Long id = offer.getId();
                deproxiedOffer = postLoaderDao.find(OfferImpl.class, id);
            } else if (offer instanceof HibernateProxy) {
                deproxiedOffer = HibernateUtils.deproxy(offer);
            } else {
                deproxiedOffer = offer;
            }
        }

        return deproxiedOffer;
    }

    @Override
    public String getOfferName() {
        return offerName;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public void setReason(String reason) {
        if (reason != null) {
            this.reason = reason;
        } else if (this.offerName != null) {
            this.reason = this.offerName;
        }
    }

    @Override
    public void setOrderItemPriceDetail(OrderItemPriceDetail orderItemPriceDetail) {
        this.orderItemPriceDetail = orderItemPriceDetail;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        deproxiedOffer = null;
        if (offer != null) {
            this.offerName = offer.getMarketingMessage() != null ? offer.getMarketingMessage() : offer.getName();
        }
    }

    @Override
    public void setOfferName(String offerName) {
        this.offerName = offer.getName();
    }

    protected BroadleafCurrency getCurrency() {
        return getOrderItemPriceDetail().getOrderItem().getOrder().getCurrency();
    }

    @Override
    public Money getValue() {
        return value == null ? null : BroadleafCurrencyUtils.getMoney(value, getCurrency());
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
            return BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
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
            return BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        }
        return salesValue;
    }

    @Override
    public void setSalesPriceValue(Money salesPriceValue) {
        this.salesValue = salesPriceValue;
    }

    @Override
    public String getCurrencyCode() {
        if (getCurrency() != null) {
            return getCurrency().getCurrencyCode();
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((orderItemPriceDetail == null) ? 0 : orderItemPriceDetail.hashCode());
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
        OrderItemPriceDetailAdjustmentImpl other = (OrderItemPriceDetailAdjustmentImpl) obj;

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
        if (orderItemPriceDetail == null) {
            if (other.orderItemPriceDetail != null) {
                return false;
            }
        } else if (!orderItemPriceDetail.equals(other.orderItemPriceDetail)) {
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

    @Override
    public <G extends OrderItemPriceDetailAdjustment> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OrderItemPriceDetailAdjustment cloned = createResponse.getClone();
        cloned.setOfferName(offerName);
        cloned.setAppliedToSalePrice(appliedToSalePrice);
        // dont clone
        cloned.setOrderItemPriceDetail(orderItemPriceDetail);
        cloned.setSalesPriceValue(getSalesPriceValue());
        cloned.setRetailPriceValue(getRetailPriceValue());
        cloned.setReason(reason);
        cloned.setValue(getValue());
        return createResponse;
    }
}
