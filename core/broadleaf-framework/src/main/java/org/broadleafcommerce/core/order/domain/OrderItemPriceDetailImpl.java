/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemPriceDetailAdjustmentImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM_PRICE_DTL")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OrderItemPriceDetailImpl implements OrderItemPriceDetail {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemPriceDetailId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderItemPriceDetailId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderItemPriceDetailImpl", allocationSize = 100)
    @Column(name = "ORDER_ITEM_PRICE_DTL_ID")
    @AdminPresentation(friendlyName = "OrderItemPriceDetailImpl_Id", group = "OrderItemPriceDetailImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    @AdminPresentation(excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected OrderItem orderItem;

    @OneToMany(mappedBy = "orderItemPriceDetail", targetEntity = OrderItemPriceDetailAdjustmentImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    @AdminPresentationCollection(addType = AddMethodType.PERSIST, friendlyName = "OrderItemPriceDetailImpl_orderItemPriceDetailAdjustments")
    protected List<OrderItemPriceDetailAdjustment> orderItemPriceDetailAdjustments = new ArrayList<OrderItemPriceDetailAdjustment>();

    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "OrderItemPriceDetailImpl_quantity", order = 5, group = "OrderItemPriceDetailImpl_Pricing", prominent = true)
    protected int quantity;

    @Column(name = "USE_SALE_PRICE")
    @AdminPresentation(friendlyName = "OrderItemPriceDetailImpl_useSalePrice", order = 5, group = "OrderItemPriceDetailImpl_Pricing", prominent = true)
    protected Boolean useSalePrice;

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
            adjustmentValue = adjustmentValue.add(adjustment.getValue());
        }
        return adjustmentValue;
    }

    @Override
    public Money getTotalAdjustmentValue() {
        return getAdjustmentValue().multiply(quantity);
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

}
