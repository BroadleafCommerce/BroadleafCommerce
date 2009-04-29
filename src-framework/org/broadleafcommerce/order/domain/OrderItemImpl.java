package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
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
    private Long id;

    @ManyToOne(targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID", nullable = false)
    private Sku sku;

    @ManyToOne(targetEntity = ProductImpl.class)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "RETAIL_PRICE")
    private BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "QUANTITY")
    private int quantity;

    @ManyToOne(targetEntity = PersonalMessageImpl.class)
    @JoinColumn(name = "PERSONAL_MESSAGE_ID")
    private PersonalMessage personalMessage;

    @Transient
    // TODO: Need to persist this
    private List<CandidateItemOffer> candidateItemOffers;

    @Transient
    // TODO: Need to persist this
    private List<Offer> appliedItemOffers;

    @Transient
    private int markedForOffer = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Money getRetailPrice() {
        return retailPrice == null ? null : new Money(retailPrice);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
}
