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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.CategoryImpl;
import org.broadleafcommerce.core.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.core.offer.domain.CandidateItemOfferImpl;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.core.offer.domain.OrderItemAdjustmentImpl;
import org.broadleafcommerce.core.order.service.type.OrderItemType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Parameter;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OrderItemImpl_baseOrderItem")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class OrderItemImpl implements OrderItem, Cloneable, AdminMainEntity, CurrencyCodeIdentifiable {

    private static final Log LOG = LogFactory.getLog(OrderItemImpl.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemId")
    @GenericGenerator(
        name="OrderItemId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OrderItemImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.OrderItemImpl")
        }
    )
    @Column(name = "ORDER_ITEM_ID")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    @Index(name="ORDERITEM_CATEGORY_INDEX", columnNames={"CATEGORY_ID"})
    @NotFound(action = NotFoundAction.IGNORE)
    @AdminPresentation(friendlyName = "OrderItemImpl_Category", order=Presentation.FieldOrder.CATEGORY,
            group = Presentation.Group.Name.Catalog, groupOrder = Presentation.Group.Order.Catalog)
    @AdminPresentationToOneLookup()
    protected Category category;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    @Index(name="ORDERITEM_ORDER_INDEX", columnNames={"ORDER_ID"})
    @AdminPresentation(excluded = true)
    protected Order order;

    @Column(name = "PRICE", precision = 19, scale = 5)
    @AdminPresentation(friendlyName = "OrderItemImpl_Item_Price", order = Presentation.FieldOrder.PRICE,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            fieldType = SupportedFieldType.MONEY, prominent = true, gridOrder = 3000)
    protected BigDecimal price;

    @Column(name = "QUANTITY", nullable = false)
    @AdminPresentation(friendlyName = "OrderItemImpl_Item_Quantity", order = Presentation.FieldOrder.QUANTITY,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            prominent = true, gridOrder = 2000)
    protected int quantity;

    @Column(name = "RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderItemImpl_Item_Retail_Price", order = Presentation.FieldOrder.RETAILPRICE,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            fieldType = SupportedFieldType.MONEY, prominent = true, gridOrder = 4000)
    protected BigDecimal retailPrice;

    @Column(name = "SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "OrderItemImpl_Item_Sale_Price", order = Presentation.FieldOrder.SALEPRICE,
            group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
            fieldType = SupportedFieldType.MONEY)
    protected BigDecimal salePrice;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "OrderItemImpl_Item_Name", order=Presentation.FieldOrder.NAME,
            group = Presentation.Group.Name.Description, prominent=true, gridOrder = 1000,
            groupOrder = Presentation.Group.Order.Description)
    protected String name;

    @ManyToOne(targetEntity = PersonalMessageImpl.class, cascade = { CascadeType.ALL })
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @Index(name="ORDERITEM_MESSAGE_INDEX", columnNames={"PERSONAL_MESSAGE_ID"})
    protected PersonalMessage personalMessage;

    @ManyToOne(targetEntity = GiftWrapOrderItemImpl.class, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "GIFT_WRAP_ITEM_ID", nullable = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @Index(name="ORDERITEM_GIFT_INDEX", columnNames={"GIFT_WRAP_ITEM_ID"})
    @AdminPresentation(excluded = true)
    protected GiftWrapOrderItem giftWrapOrderItem;

    @OneToMany(mappedBy = "orderItem", targetEntity = OrderItemAdjustmentImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderItemImpl_Adjustments", order = Presentation.FieldOrder.ADJUSTMENTS,
                    tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced)
    protected List<OrderItemAdjustment> orderItemAdjustments = new ArrayList<OrderItemAdjustment>();

    @OneToMany(mappedBy = "orderItem", targetEntity = OrderItemQualifierImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<OrderItemQualifier> orderItemQualifiers = new ArrayList<OrderItemQualifier>();

    @OneToMany(mappedBy = "orderItem", targetEntity = CandidateItemOfferImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<CandidateItemOffer> candidateItemOffers = new ArrayList<CandidateItemOffer>();

    @OneToMany(mappedBy = "orderItem", targetEntity = OrderItemPriceDetailImpl.class, cascade = { CascadeType.ALL },
            orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @AdminPresentationCollection(friendlyName="OrderItemImpl_Price_Details", order = Presentation.FieldOrder.PRICEDETAILS,
                    tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced)
    protected List<OrderItemPriceDetail> orderItemPriceDetails = new ArrayList<OrderItemPriceDetail>();
    
    @Column(name = "ORDER_ITEM_TYPE")
    @Index(name="ORDERITEM_TYPE_INDEX", columnNames={"ORDER_ITEM_TYPE"})
    protected String orderItemType;

    @Column(name = "ITEM_TAXABLE_FLAG")
    protected Boolean itemTaxable;

    @Column(name = "RETAIL_PRICE_OVERRIDE")
    protected Boolean retailPriceOverride;

    @Column(name = "SALE_PRICE_OVERRIDE")
    protected Boolean salePriceOverride;

    @Column(name = "DISCOUNTS_ALLOWED")
    @AdminPresentation(friendlyName = "OrderItemImpl_Discounts_Allowed", order=Presentation.FieldOrder.DISCOUNTALLOWED,
            tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced)
    protected Boolean discountsAllowed;

    @OneToMany(mappedBy = "orderItem", targetEntity = OrderItemAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    @MapKey(name="name")
    @AdminPresentationMap(friendlyName = "OrderItemImpl_Attributes",
        tab = Presentation.Tab.Name.Advanced, tabOrder = Presentation.Tab.Order.Advanced,
        deleteEntityUponRemove = true, forceFreeFormKeys = true, keyPropertyFriendlyName = "OrderItemAttributeImpl_Attribute_Name"
    )
    protected Map<String, OrderItemAttribute> orderItemAttributeMap = new HashMap<String, OrderItemAttribute>();

    @Column(name = "TOTAL_TAX")
    @AdminPresentation(friendlyName = "OrderItemImpl_Total_Tax", order = Presentation.FieldOrder.TOTALTAX,
                group = Presentation.Group.Name.Pricing, groupOrder = Presentation.Group.Order.Pricing,
                fieldType = SupportedFieldType.MONEY)
    protected BigDecimal totalTax;
    
    @OneToMany(mappedBy = "parentOrderItem", targetEntity = OrderItemImpl.class)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
    protected List<OrderItem> childOrderItems = new ArrayList<OrderItem>();

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "PARENT_ORDER_ITEM_ID")
    @Index(name="ORDERITEM_PARENTORDERITEM_INDEX", columnNames={"PARENT_ORDER_ITEM_ID"})
    protected OrderItem parentOrderItem;
    
    @Override
    public Money getRetailPrice() {
        if (retailPrice == null) {
            updateSaleAndRetailPrices();
        }
        return convertToMoney(retailPrice);
    }

    @Override
    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    @Override
    public Money getSalePrice() {
        if (salePrice == null) {
            updateSaleAndRetailPrices();
        }
        if (salePrice != null) {
            Money returnPrice = convertToMoney(salePrice);
            if (retailPrice != null && returnPrice.greaterThan(getRetailPrice())) {
                return getRetailPrice();
            } else {
                return returnPrice;
            }
        } else {
            return getRetailPrice();
        }
    }

    @Override
    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    @Override
    public Money getPrice() {
        return getAveragePrice();
    }

    @Override
    public void setPrice(Money finalPrice) {
        setRetailPrice(finalPrice);
        setSalePrice(finalPrice);
        setRetailPriceOverride(true);
        setSalePriceOverride(true);
        setDiscountingAllowed(false);
        this.price = Money.toAmount(finalPrice);
    }

    @Override
    public Money getTaxablePrice() {
        Money taxablePrice = BroadleafCurrencyUtils.getMoney(BigDecimal.ZERO, getOrder().getCurrency());
        if (isTaxable() == null || isTaxable()) {
            taxablePrice = getAveragePrice();
        }
        return taxablePrice;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public Category getCategory() {
        return category;
    }

    @Override
    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public List<CandidateItemOffer> getCandidateItemOffers() {
        return candidateItemOffers;
    }

    @Override
    public void setCandidateItemOffers(List<CandidateItemOffer> candidateItemOffers) {
        this.candidateItemOffers = candidateItemOffers;
    }

    @Override
    public PersonalMessage getPersonalMessage() {
        return personalMessage;
    }

    @Override
    public void setPersonalMessage(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }

    @Override
    public Order getOrder() {
        return order;
    }

    @Override
    public void setOrder(Order order) {
        this.order = order;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isInCategory(String categoryName) {
        Category currentCategory = category;
        if (currentCategory != null) {
            if (currentCategory.getName().equals(categoryName)) {
                return true;
            }
            while ((currentCategory = currentCategory.getDefaultParentCategory()) != null) {
                if (currentCategory.getName().equals(categoryName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<OrderItemQualifier> getOrderItemQualifiers() {
        return this.orderItemQualifiers;
    }

    @Override
    public void setOrderItemQualifiers(List<OrderItemQualifier> orderItemQualifiers) {
        this.orderItemQualifiers = orderItemQualifiers;
    }

    @Override
    public List<OrderItemAdjustment> getOrderItemAdjustments() {
        return this.orderItemAdjustments;
    }

    @Override
    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments) {       
        this.orderItemAdjustments = orderItemAdjustments;
    }

    @Override
    public Money getAdjustmentValue() {
        return getAverageAdjustmentValue();
    }

    @Override
    public GiftWrapOrderItem getGiftWrapOrderItem() {
        return giftWrapOrderItem;
    }

    @Override
    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem) {
        this.giftWrapOrderItem = giftWrapOrderItem;
    }

    @Override
    public OrderItemType getOrderItemType() {
        return convertOrderItemType(orderItemType);
    }

    @Override
    public void setOrderItemType(OrderItemType orderItemType) {
        this.orderItemType = orderItemType.getType();
    }

    @Override
    public boolean getIsOnSale() {
        if (getSalePrice() != null) {
            return !getSalePrice().equals(getRetailPrice());
        } else {
            return false;
        }
    }

    @Override
    public boolean getIsDiscounted() {
        if (getPrice() != null) {
            return !getPrice().equals(getRetailPrice());
        } else {
            return false;
        }
    }

    @Override
    public boolean updateSaleAndRetailPrices() {
        if (salePrice == null) {
            salePrice = retailPrice;
        }
        return false;
    }
    
    @Override
    public void finalizePrice() {
        price = getAveragePrice().getAmount();
    }

    @Override
    public void assignFinalPrice() {
        Money finalPrice = getTotalPrice().divide(quantity);
        price = finalPrice.getAmount();
    }

    @Override
    public Money getPriceBeforeAdjustments(boolean allowSalesPrice) {
        if (allowSalesPrice) {
            return getSalePrice();
        } else {
            return getRetailPrice();
        }
    }
    
    @Override
    public void addCandidateItemOffer(CandidateItemOffer candidateItemOffer) {
        getCandidateItemOffers().add(candidateItemOffer);
    }
    
    @Override
    public void removeAllCandidateItemOffers() {
        if (getCandidateItemOffers() != null) {
            for (CandidateItemOffer candidate : getCandidateItemOffers()) {
                candidate.setOrderItem(null);
            }
            getCandidateItemOffers().clear();
        }
    }
    
    @Override
    public int removeAllAdjustments() {
        int removedAdjustmentCount = 0;
        if (getOrderItemAdjustments() != null) {
            for (OrderItemAdjustment adjustment : getOrderItemAdjustments()) {
                adjustment.setOrderItem(null);
            }
            removedAdjustmentCount = getOrderItemAdjustments().size();
            getOrderItemAdjustments().clear();
        }
        assignFinalPrice();
        return removedAdjustmentCount;
    }
    
    /**
     * A list of arbitrary attributes added to this item.
     */
    @Override
    public Map<String,OrderItemAttribute> getOrderItemAttributes() {
        return orderItemAttributeMap;
    }

    /**
     * Sets the map of order item attributes.
     *
     * @param orderItemAttributes
     */
    @Override
    public void setOrderItemAttributes(Map<String,OrderItemAttribute> orderItemAttributes) {
        this.orderItemAttributeMap = orderItemAttributes;
    }

    @Override
    public Boolean isTaxable() {
        return itemTaxable == null ? true : itemTaxable;
    }

    @Override
    public void setTaxable(Boolean taxable) {
        this.itemTaxable = taxable;
    }



    @Override
    public void setOrderItemPriceDetails(List<OrderItemPriceDetail> orderItemPriceDetails) {
        this.orderItemPriceDetails = orderItemPriceDetails;
    }

    @Override
    public boolean isDiscountingAllowed() {
        if (discountsAllowed == null) {
            return true;
        } else {
            return discountsAllowed.booleanValue();
        }
    }

    @Override
    public void setDiscountingAllowed(boolean discountsAllowed) {
        this.discountsAllowed = discountsAllowed;
    }

    @Override
    public Money getAveragePrice() {
        if (quantity == 0) {
            return price == null ? null : BroadleafCurrencyUtils.getMoney(price, getOrder().getCurrency());
        }
        return getTotalPrice().divide(quantity);
    }

    @Override
    public Money getAverageAdjustmentValue() {
        if (quantity == 0) {
            return null;
        }
        return getTotalAdjustmentValue().divide(quantity);
    }

    @Override
    public Money getTotalAdjustmentValue() {
        Money totalAdjustmentValue = BroadleafCurrencyUtils.getMoney(getOrder().getCurrency());
        List<OrderItemPriceDetail> priceDetails = getOrderItemPriceDetails();
        if (priceDetails != null) {
            for (OrderItemPriceDetail priceDetail : getOrderItemPriceDetails()) {
                totalAdjustmentValue = totalAdjustmentValue.add(priceDetail.getTotalAdjustmentValue());
            }
        }

        return totalAdjustmentValue;
    }

    @Override
    public Money getTotalPrice() {
        Money returnValue = convertToMoney(BigDecimal.ZERO);
        if (orderItemPriceDetails != null && orderItemPriceDetails.size() > 0) {
            for (OrderItemPriceDetail oipd : orderItemPriceDetails) {
                returnValue = returnValue.add(oipd.getTotalAdjustedPrice());
            }
        } else {
            if (price != null) {
                returnValue = convertToMoney(price).multiply(quantity);
            } else {
                return getSalePrice().multiply(quantity);
            }
        }

        return returnValue;
    }

    @Override
    public Money getTotalPriceBeforeAdjustments(boolean allowSalesPrice) {
        return getPriceBeforeAdjustments(allowSalesPrice).multiply(getQuantity());
    }

    @Override
    public void setRetailPriceOverride(boolean override) {
        this.retailPriceOverride = Boolean.valueOf(override);
    }

    @Override
    public boolean isRetailPriceOverride() {
        if (retailPriceOverride == null) {
            return false;
        } else {
            return retailPriceOverride.booleanValue();
        }
    }

    @Override
    public void setSalePriceOverride(boolean override) {
        this.salePriceOverride = Boolean.valueOf(override);
    }

    @Override
    public boolean isSalePriceOverride() {
        if (salePriceOverride == null) {
            return false;
        } else {
            return salePriceOverride.booleanValue();
        }
    }

    @Override
    public List<OrderItemPriceDetail> getOrderItemPriceDetails() {
        return orderItemPriceDetails;
    }
    
    @Override
    public List<OrderItem> getChildOrderItems() {
        return childOrderItems;
    }
    
    @Override
    public void setChildOrderItems(List<OrderItem> childOrderItems) {
        this.childOrderItems = childOrderItems;
    }
    
    @Override
    public OrderItem getParentOrderItem() {
        return parentOrderItem;
    }
    
    @Override
    public void setParentOrderItem(OrderItem parentOrderItem) {
        this.parentOrderItem = parentOrderItem;
    }
    
    @Override
    public boolean isAParentOf(OrderItem candidateChild) {
        if (CollectionUtils.isNotEmpty(this.getChildOrderItems())) {
            for (OrderItem child : this.getChildOrderItems()) {
                if (child.equals(candidateChild)) {
                    return true;
                }
            }
            // Item wasn't a direct child. Let's check the hierarchy
            for (OrderItem child : this.getChildOrderItems()) {
                if (child.isAParentOf(candidateChild)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public String getCurrencyCode() {
        if (getOrder().getCurrency() != null) {
            return getOrder().getCurrency().getCurrencyCode();
        }
        return null;
    }

    public void checkCloneable(OrderItem orderItem) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
        Method cloneMethod = orderItem.getClass().getMethod("clone", new Class[]{});
        if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") &&
                !orderItem.getClass().getName().startsWith("org.broadleafcommerce")) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("Custom extensions and implementations should implement clone in " +
                    "order to guarantee split and merge operations are performed accurately");
        }
    }

    protected Money convertToMoney(BigDecimal amount) {
        return amount == null ? null : BroadleafCurrencyUtils.getMoney(amount, getOrder().getCurrency());
    }

    protected OrderItemType convertOrderItemType(String type) {
        return OrderItemType.getInstance(type);
    }
    
    @Override
    public OrderItem clone() {
        //this is likely an extended class - instantiate from the fully qualified name via reflection
        OrderItemImpl clonedOrderItem;
        try {
            clonedOrderItem = (OrderItemImpl) Class.forName(this.getClass().getName()).newInstance();
            try {
                checkCloneable(clonedOrderItem);
            } catch (CloneNotSupportedException e) {
                LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " +
                        clonedOrderItem.getClass().getName(), e);
            }
            if (candidateItemOffers != null) {
                for (CandidateItemOffer candidate : candidateItemOffers) {
                    CandidateItemOffer clone = candidate.clone();
                    clone.setOrderItem(clonedOrderItem);
                    clonedOrderItem.getCandidateItemOffers().add(clone);
                }
            }
            
            if (orderItemAttributeMap != null && !orderItemAttributeMap.isEmpty()) {
                for (OrderItemAttribute attribute : orderItemAttributeMap.values()) {
                    OrderItemAttribute clone = attribute.clone();
                    clone.setOrderItem(clonedOrderItem);
                    clonedOrderItem.getOrderItemAttributes().put(clone.getName(), clone);
                }
            }
            
            if (CollectionUtils.isNotEmpty(childOrderItems)) {
                for (OrderItem childOrderItem : childOrderItems) {
                    OrderItem clone = childOrderItem.clone();
                    clone.setParentOrderItem(clonedOrderItem);
                    clonedOrderItem.getChildOrderItems().add(clone);
                }
            }
            
            clonedOrderItem.setCategory(category);
            clonedOrderItem.setGiftWrapOrderItem(giftWrapOrderItem);
            clonedOrderItem.setName(name);
            clonedOrderItem.setOrder(order);
            clonedOrderItem.setOrderItemType(convertOrderItemType(orderItemType));
            clonedOrderItem.setPersonalMessage(personalMessage);
            clonedOrderItem.setQuantity(quantity);
            clonedOrderItem.retailPrice = retailPrice;
            clonedOrderItem.salePrice = salePrice;
            clonedOrderItem.discountsAllowed = discountsAllowed;
            clonedOrderItem.salePriceOverride = salePriceOverride;
            clonedOrderItem.retailPriceOverride = retailPriceOverride;
            clonedOrderItem.setParentOrderItem(parentOrderItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return clonedOrderItem;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((giftWrapOrderItem == null) ? 0 : giftWrapOrderItem.hashCode());
        result = prime * result + ((order == null) ? 0 : order.hashCode());
        result = prime * result + ((orderItemType == null) ? 0 : orderItemType.hashCode());
        result = prime * result + ((personalMessage == null) ? 0 : personalMessage.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + quantity;
        result = prime * result + ((retailPrice == null) ? 0 : retailPrice.hashCode());
        result = prime * result + ((salePrice == null) ? 0 : salePrice.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        OrderItemImpl other = (OrderItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (category == null) {
            if (other.category != null) {
                return false;
            }
        } else if (!category.equals(other.category)) {
            return false;
        }
        if (giftWrapOrderItem == null) {
            if (other.giftWrapOrderItem != null) {
                return false;
            }
        } else if (!giftWrapOrderItem.equals(other.giftWrapOrderItem)) {
            return false;
        }
        if (order == null) {
            if (other.order != null) {
                return false;
            }
        } else if (!order.equals(other.order)) {
            return false;
        }
        if (orderItemType == null) {
            if (other.orderItemType != null) {
                return false;
            }
        } else if (!orderItemType.equals(other.orderItemType)) {
            return false;
        }
        if (personalMessage == null) {
            if (other.personalMessage != null) {
                return false;
            }
        } else if (!personalMessage.equals(other.personalMessage)) {
            return false;
        }
        if (price == null) {
            if (other.price != null) {
                return false;
            }
        } else if (!price.equals(other.price)) {
            return false;
        }
        if (quantity != other.quantity) {
            return false;
        }
        if (retailPrice == null) {
            if (other.retailPrice != null) {
                return false;
            }
        } else if (!retailPrice.equals(other.retailPrice)) {
            return false;
        }
        if (salePrice == null) {
            if (other.salePrice != null) {
                return false;
            }
        } else if (!salePrice.equals(other.salePrice)) {
            return false;
        }
        return true;
    }

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String Advanced = "OrderImpl_Advanced";
            }

            public static class Order {
                public static final int Advanced = 2000;
            }
        }

        public static class Group {
            public static class Name {
                public static final String Description = "OrderItemImpl_Description";
                public static final String Pricing = "OrderItemImpl_Pricing";
                public static final String Catalog = "OrderItemImpl_Catalog";
            }

            public static class Order {
                public static final int Description = 1000;
                public static final int Pricing = 2000;
                public static final int Catalog = 3000;
            }
        }

        public static class FieldOrder {
            public static final int NAME = 1000;
            public static final int PRICE = 2000;
            public static final int QUANTITY = 3000;
            public static final int RETAILPRICE = 4000;
            public static final int SALEPRICE = 5000;
            public static final int TOTALTAX = 6000;
            public static final int CATEGORY = 1000;
            public static final int PRICEDETAILS = 1000;
            public static final int ADJUSTMENTS = 2000;
            public static final int DISCOUNTALLOWED = 3000;
        }
    }

    @Override
    public boolean isSkuActive() {
        //abstract method, by default return true
        return true;
    }
}
