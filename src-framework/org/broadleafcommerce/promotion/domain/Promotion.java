package org.broadleafcommerce.promotion.domain;

import java.io.Serializable;
import java.math.BigDecimal;

//@Entity
//@Table(name = "BLC_PROMOTION")
public class Promotion implements Serializable {

    private static final long serialVersionUID = 1L;

    // @Id
    // @GeneratedValue
    // @Column(name = "PROMOTION_ID")
    private Long id;

    // @Column(name = "PROMOTION_TYPE")
    private String type;

    // @Column(name = "PROMOTION_REFERENCE")
    private String reference;

    // @Column(name = "PROMOTION_DISCOUNT")
    private BigDecimal discount;

    // @Column(name = "PROMOTION_USES")
    private int uses;

    // @Column(name = "PROMOTIONS_APPLIED")
    private int applied;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    public int getApplied() {
        return applied;
    }

    public void setApplied(int applied) {
        this.applied = applied;
    }
}
