package org.broadleafcommerce.tcs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.Sku;

@Entity
@Table(name = "TCS_ACCESSORY_SKU")
public class TCSAccessorySku implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @OneToOne(targetEntity = TCSSku.class)
    @JoinColumn(name = "SKU_ID")
    private Sku sku;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private TCSProduct product;

    @Column(name = "SALES_TEXT")
    private String salesText;
}
