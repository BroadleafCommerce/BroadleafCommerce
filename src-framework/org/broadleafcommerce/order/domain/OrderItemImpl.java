package org.broadleafcommerce.order.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.domain.SkuImpl;
import org.broadleafcommerce.offer.domain.ItemOffer;
import org.broadleafcommerce.util.money.Money;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ORDER_ITEM")
public class OrderItemImpl implements OrderItem, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ORDER_ITEM_ID")
    private Long id;

    @ManyToOne(targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID", nullable = false)
    private Sku sku;

    @ManyToOne(targetEntity = OrderImpl.class)
    @JoinColumn(name = "SC_ORDER_ID")
    private Order order;

    @Column(name = "RETAIL_PRICE")
    private BigDecimal retailPrice;

    @Column(name = "SALE_PRICE")
    private BigDecimal salePrice;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "QUANTITY")
    private int quantity;

    @Transient
    // TODO: Need to persist this
    private List<ItemOffer> candidateItemOffers;

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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

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

    public List<ItemOffer> getCandidateItemOffers() {
        return candidateItemOffers;
    }

    public void setCandidateItemOffers(List<ItemOffer> itemOffers) {
        this.candidateItemOffers = itemOffers;
    }

    public List<ItemOffer> addCandidateItemOffer(ItemOffer candidateOffer) {
    	// TODO: if stacked, add all of the items to the persisted structure and add just the stacked version
    	//       to this collection
        this.candidateItemOffers.add(candidateOffer);
        return candidateItemOffers;
    }

    public void removeAllOffers() {
    	if (candidateItemOffers != null) {
    		candidateItemOffers.clear();
    	}
    }

    
}
