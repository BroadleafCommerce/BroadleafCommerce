package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.util.money.Money;

// TODO: Auto-generated Javadoc
/**
 * The Class BasePriceImpl.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_BASE_PRICE")
public class BasePriceImpl implements BasePrice, Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue
    @Column(name = "BASE_PRICE_ID")
    private Long id;

    /** The auditable. */
    @Embedded
    private Auditable auditable;

    /** The sku. */
    @ManyToOne(targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID", nullable = false)
    private Sku sku;

    /** The amount. */
    @Column(name = "AMOUNT")
    private BigDecimal amount;

    /** The start date. */
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    /** The end date. */
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getAmount()
     */
    public Money getAmount() {
        return amount == null ? null : new Money(amount);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setAmount(org.broadleafcommerce.util.money.Money)
     */
    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getStartDate()
     */
    public Date getStartDate() {
        return startDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setStartDate(java.util.Date)
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getEndDate()
     */
    public Date getEndDate() {
        return endDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setEndDate(java.util.Date)
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getSku()
     */
    public Sku getSku() {
        return sku;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setSku(org.broadleafcommerce.catalog.domain.Sku)
     */
    public void setSku(Sku sku) {
        this.sku = sku;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getAuditable()
     */
    public Auditable getAuditable() {
        return auditable;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setAuditable(org.broadleafcommerce.common.domain.Auditable)
     */
    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }
}
