/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.order.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.BankersRounding;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.util.ApplicationContextHolder;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustmentImpl;
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableItemFactoryImpl;
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusService;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM_PRICE_DTL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class OrderItemPriceDetailImpl implements OrderItemPriceDetail, CurrencyCodeIdentifiable {

    private static final long serialVersionUID = 1L;

    private static RoundingMode _roundingMode;

    protected static final Log LOG = LogFactory.getLog(OrderItemPriceDetailImpl.class);

    @Id
    @GeneratedValue(generator = "OrderItemPriceDetailId")
    @GenericGenerator(
        name="OrderItemPriceDetailId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderItemPriceDetailImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.OrderItemPriceDetailImpl")
        }
    )
    @Column(name = "ORDER_ITEM_PRICE_DTL_ID")
    @AdminPresentation(friendlyName = "OrderItemPriceDetailImpl_Id", group = "OrderItemPriceDetailImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    @AdminPresentation(excluded = true)
    protected OrderItem orderItem;

    @OneToMany(mappedBy = "orderItemPriceDetail", targetEntity = OrderItemPriceDetailAdjustmentImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blOrderElements")
    @AdminPresentationCollection(addType = AddMethodType.PERSIST, friendlyName = "OrderItemPriceDetailImpl_orderItemPriceDetailAdjustments")
    protected List<OrderItemPriceDetailAdjustment> orderItemPriceDetailAdjustments = new ArrayList<OrderItemPriceDetailAdjustment>();

    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "OrderItemPriceDetailImpl_quantity", order = 5, group = "OrderItemPriceDetailImpl_Pricing", prominent = true)
    protected int quantity;

    @Column(name = "USE_SALE_PRICE")
    @AdminPresentation(friendlyName = "OrderItemPriceDetailImpl_useSalePrice", order = 5, group = "OrderItemPriceDetailImpl_Pricing", prominent = true)
    protected Boolean useSalePrice = true;

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
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public List<OrderItemPriceDetailAdjustment> getOrderItemPriceDetailAdjustments() {
        return orderItemPriceDetailAdjustments;
    }

    @Override
    public List<OrderItemPriceDetailAdjustment> getFutureCreditOrderItemPriceDetailAdjustments() {
        List<OrderItemPriceDetailAdjustment> orderDiscountAdjustments = new ArrayList<>();
        for (OrderItemPriceDetailAdjustment adjustment : orderItemPriceDetailAdjustments) {
            if (adjustment.isFutureCredit()) {
                orderDiscountAdjustments.add(adjustment);
            }
        }
        return orderDiscountAdjustments;
    }

    @Override
    public void setOrderItemAdjustments(List<OrderItemPriceDetailAdjustment> orderItemPriceDetailAdjustments) {
        this.orderItemPriceDetailAdjustments = orderItemPriceDetailAdjustments;
        
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    protected BroadleafCurrency getCurrency() {
        return getOrderItem().getOrder().getCurrency();
    }

    @Override
    public Money getAdjustmentValue() {
        Money adjustmentValue = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (OrderItemPriceDetailAdjustment adjustment : orderItemPriceDetailAdjustments) {
            if (! adjustment.isFutureCredit()) {
                // preserve highest scale / allows adjustments to maintain more precision
                // adjustments should only have more precision if the roundingScale was
                // overridden
                if (adjustment.getValue().getAmount().scale() >
                        adjustmentValue.getAmount().scale()) {
                    adjustmentValue = adjustment.getValue().add(adjustmentValue);
                } else {
                    adjustmentValue = adjustmentValue.add(adjustment.getValue());
                }
            }
        }
        return adjustmentValue;
    }

    @Override
    public Money getFutureCreditAdjustmentValue() {
        Money adjustmentValue = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getCurrency());
        for (OrderItemPriceDetailAdjustment adjustment : orderItemPriceDetailAdjustments) {
            if (adjustment.isFutureCredit()) {
                // preserve highest scale / allows adjustments to maintain more precision
                // adjustments should only have more precision if the roundingScale was
                // overridden
                if (adjustment.getValue().getAmount().scale() >
                        adjustmentValue.getAmount().scale()) {
                    adjustmentValue = adjustment.getValue().add(adjustmentValue);
                } else {
                    adjustmentValue = adjustmentValue.add(adjustment.getValue());
                }
            }
        }
        return adjustmentValue;
    }

    public RoundingMode getRoundingModeForAdj() {
        RoundingMode mode = RoundingMode.HALF_EVEN;
        if (_roundingMode == null) {
            ApplicationContext ctx = ApplicationContextHolder.getApplicationContext();
            if (ctx != null) {
                String modeStr = ctx.getEnvironment().getProperty(
                        "item.offer.percent.rounding.mode");
                if (StringUtils.isNotEmpty(modeStr)) {
                    try {
                        mode = RoundingMode.valueOf(modeStr);
                    } catch (Exception e) {
                        LOG.error("Unable to initialize rounding mode, using default. Value set for " +
                                "item.offer.percent.rounding.mode was " + modeStr, e);
                    }
                }
            }
            _roundingMode = mode;
        }

        return _roundingMode;
    }

    @Override
    public Money getTotalAdjustmentValue() {
        return getAdjustmentValue().multiplyWithRounding(quantity,
                getRoundingModeForAdj());
    }

    @Override
    public Money getFutureCreditTotalAdjustmentValue() {
        return getFutureCreditAdjustmentValue().multiplyWithRounding(quantity,
                getRoundingModeForAdj());
    }

    @Override
    public Money getTotalAdjustedPrice() {
        Money basePrice = orderItem.getPriceBeforeAdjustments(getUseSalePrice());
        return basePrice.multiply(quantity).subtract(getTotalAdjustmentValue());
    }

    @Override
    public boolean getUseSalePrice() {
        if (useSalePrice == null) {
            return false;
        } else {
            return useSalePrice;
        }
    }

    @Override
    public void setUseSalePrice(boolean useSalePrice) {
        this.useSalePrice = Boolean.valueOf(useSalePrice);
    }

    @Override
    public String getCurrencyCode() {
        if (getCurrency() != null) {
            return getCurrency().getCurrencyCode();
        }
        return null;
    }

    @Override
    public <G extends OrderItemPriceDetail> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        OrderItemPriceDetail cloned = createResponse.getClone();
        cloned.setQuantity(quantity);
        cloned.setUseSalePrice(useSalePrice);
        // dont clone
        cloned.setOrderItem(orderItem);
        for(OrderItemPriceDetailAdjustment entry : orderItemPriceDetailAdjustments){
            OrderItemPriceDetailAdjustment clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            clonedEntry.setOrderItemPriceDetail(cloned);
            cloned.getOrderItemPriceDetailAdjustments().add(clonedEntry);
        }
        return  createResponse;
    }
}
