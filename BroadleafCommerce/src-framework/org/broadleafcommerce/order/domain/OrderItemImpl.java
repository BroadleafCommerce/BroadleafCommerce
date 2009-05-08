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
import org.broadleafcommerce.offer.domain.Offer;
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

    @ManyToOne(targetEntity = PersonalMessageImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    protected PersonalMessage personalMessage;

    // TODO: Add OrderItemAdjustments
    // Make sure that when you add appliedItemOffers you add them to the adjustments.

    // TODO: Need to persist this
    @Transient
    protected List<CandidateItemOffer> candidateItemOffers;

    //This does not need to be persisted since the adjustments will be persisted.
    //It just helps keep track of offers that were applied.
    @Transient
    protected List<Offer> appliedItemOffers;

    @Transient
    protected int markedForOffer = 0;

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
        if (this.candidateItemOffers == null) {
            this.candidateItemOffers = new ArrayList<CandidateItemOffer>();
        }
        this.candidateItemOffers.add(candidateOffer);
        return candidateItemOffers;
    }

    public void setAppliedItemOffers(List<Offer> appliedOffers) {
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

    public void removeAllOffers() {
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
    public boolean getIsBundle() {
        return this instanceof BundleOrderItem;
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


}
