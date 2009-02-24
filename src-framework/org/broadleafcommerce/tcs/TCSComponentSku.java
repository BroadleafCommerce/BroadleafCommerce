package org.broadleafcommerce.tcs;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.Sku;

@Entity
@Table(name = "TCS_COMPONENT_SKU")
public class TCSComponentSku implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne(targetEntity = TCSSku.class)
    @JoinColumn(name = "SKU_ID")
    private Sku sku;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "CUT_LENGTH")
    private Integer cutlength;

    @Column(name = "QUANTITY")
    private Integer quantity;

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCutlength() {
        return cutlength;
    }

    public void setCutlength(Integer cutlength) {
        this.cutlength = cutlength;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
