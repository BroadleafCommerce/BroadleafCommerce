package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.broadleafcommerce.common.domain.Auditable;

//@Entity
//@Table(name = "BLC_BASE_PRICE")
public class BasePrice implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue
//    @Column(name = "BASE_PRICE_ID")
    private Long id;
    
//    @Embedded
    private Auditable auditable;

//    @ManyToOne
//    @JoinColumn(name = "SKU_ID", nullable = false)
    private Sku sku;

//    @Column(name = "AMOUNT")
    private BigDecimal amount;

//    @Column(name = "START_DATE")
//    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

//    @Column(name = "END_DATE")
//    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

}
