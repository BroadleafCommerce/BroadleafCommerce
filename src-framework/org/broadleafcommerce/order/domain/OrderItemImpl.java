package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.OrderItemAdjustment;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM")
public class OrderItemImpl implements OrderItem, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OrderItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OrderItemImpl", allocationSize = 1)
    @Column(name = "ORDER_ITEM_ID")
    protected Long id;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    protected Category category;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "ORDER_ID")
    protected Order order;

    @Column(name = "RETAIL_PRICE")
    protected BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    protected BigDecimal salePrice;

    @Column(name = "PRICE")
    protected BigDecimal price;

    @Column(name = "QUANTITY")
    protected int quantity;

    @Transient
    private BigDecimal adjustmentPrice;  // retailPrice with adjustments

    @ManyToOne(targetEntity = PersonalMessageImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    protected PersonalMessage personalMessage;

    @ManyToOne(targetEntity = GiftWrapOrderItemImpl.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "GIFT_WRAP_ITEM_ID", nullable = true)
    protected GiftWrapOrderItem giftWrapOrderItem;

    // TODO: need to persist
    @Transient
    private List<OrderItemAdjustment> orderItemAdjustments = new ArrayList<OrderItemAdjustment>();

    // TODO: need to persist
    @Transient
    protected List<CandidateItemOffer> candidateItemOffers = new ArrayList<CandidateItemOffer>();

    @Transient
    protected int markedForOffer = 0;

    @Column(name = "ORDER_ITEM_TYPE")
    protected String orderItemType;

    public Money getRetailPrice() {
        return retailPrice == null ? null : new Money(retailPrice);
    }

    public void setRetailPrice(Money retailPrice) {
        this.retailPrice = Money.toAmount(retailPrice);
    }

    public Money getSalePrice() {
        return salePrice == null ? null : new Money(salePrice);
    }

    public void setSalePrice(Money salePrice) {
        this.salePrice = Money.toAmount(salePrice);
    }

    public Money getPrice() {
        return price == null ? null : new Money(price);
    }

    public void setPrice(Money finalPrice) {
        this.price = Money.toAmount(finalPrice);
    }

    public void assignFinalPrice() {
        price = getCurrentPrice().getAmount();
    }

    public Money getCurrentPrice() {
        Money currentPrice = null;
        if (adjustmentPrice != null) {
            currentPrice = new Money(adjustmentPrice);
        } else if (salePrice != null) {
            currentPrice = new Money(salePrice);
        } else {
            currentPrice = new Money(retailPrice);
        }
        return currentPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<CandidateItemOffer> getCandidateItemOffers() {
        return candidateItemOffers;
    }

    public void setCandidateItemOffers(List<CandidateItemOffer> itemOffers) {
        this.candidateItemOffers = itemOffers;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<CandidateItemOffer> addCandidateItemOffer(CandidateItemOffer candidateOffer) {
        // TODO: if stacked, add all of the items to the persisted structure and
        // add just the stacked version
        // to this collection
        this.candidateItemOffers.add(candidateOffer);
        return candidateItemOffers;
    }

    /*    public void setAppliedItemOffers(List<Offer> appliedOffers) {
        this.appliedItemOffers = appliedOffers;
    }

    public List<Offer> getAppliedItemOffers() {
        return this.appliedItemOffers;
    }

    public List<Offer> addAppliedItemOffer(Offer appliedOffer) {
        if (this.appliedItemOffers == null) {
            this.appliedItemOffers = new ArrayList<Offer>();
        }
        this.appliedItemOffers.add(appliedOffer);
        return this.appliedItemOffers;
    }

     */
    public void removeAllCandidateOffers() {
        if (candidateItemOffers != null) {
            candidateItemOffers.clear();
        }
    }

    public boolean markForOffer() {
        if (markedForOffer >= quantity) {
            return false;
        }
        markedForOffer++;
        return true;
    }

    public int getMarkedForOffer() {
        return markedForOffer;
    }

    public boolean unmarkForOffer() {
        if (markedForOffer < 1) {
            return false;
        }
        markedForOffer--;
        return true;
    }

    public boolean isAllQuantityMarkedForOffer() {
        if (markedForOffer >= quantity) {
            return true;
        }
        return false;
    }

    public PersonalMessage getPersonalMessage() {
        return personalMessage;
    }

    public void setPersonalMessage(PersonalMessage personalMessage) {
        this.personalMessage = personalMessage;
    }

    public Order getOrder() {
        return order;
    }

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
    public boolean isInCategory(String categoryName) {
        Category currentCategory = category;
        if(currentCategory != null) {
            if(currentCategory.getName().equals(categoryName)){
                return true;
            }
            while((currentCategory = currentCategory.getDefaultParentCategory()) != null) {
                if(currentCategory.getName().equals(categoryName)) {
                    return true;
                }
            }
        }
        return false;

    }

    public List<OrderItemAdjustment> getOrderItemAdjustments() {
        return this.orderItemAdjustments;
    }

    /*
     * Adds the adjustment to the order item's adjustment list an discounts the order item's adjustment
     * price by the value of the adjustment.
     */
    public List<OrderItemAdjustment> addOrderItemAdjustment(OrderItemAdjustment orderItemAdjustment) {
        if (this.orderItemAdjustments.size() == 0) {
            adjustmentPrice = retailPrice;
        }
        adjustmentPrice = adjustmentPrice.subtract(orderItemAdjustment.getValue().getAmount());
        this.orderItemAdjustments.add(orderItemAdjustment);
        return this.orderItemAdjustments;
    }

    public void removeAllAdjustments() {
        if (orderItemAdjustments != null) {
            orderItemAdjustments.clear();
        }
        adjustmentPrice = null;

    }

    public void setOrderItemAdjustments(List<OrderItemAdjustment> orderItemAdjustments) {
        this.orderItemAdjustments = orderItemAdjustments;
    }

    public Money getAdjustmentPrice() {
        return adjustmentPrice == null ? null : new Money(adjustmentPrice);
    }

    public void setAdjustmentPrice(Money adjustmentPrice) {
        this.adjustmentPrice = Money.toAmount(adjustmentPrice);
    }

    public GiftWrapOrderItem getGiftWrapOrderItem() {
        return giftWrapOrderItem;
    }

    public void setGiftWrapOrderItem(GiftWrapOrderItem giftWrapOrderItem) {
        this.giftWrapOrderItem = giftWrapOrderItem;
    }

    public String getOrderItemType() {
        return orderItemType;
    }

    public void setOrderItemType(String orderItemType) {
        this.orderItemType = orderItemType;
    }

    public boolean getIsOnSale() {
        return getPrice() != getRetailPrice();
    }
}
