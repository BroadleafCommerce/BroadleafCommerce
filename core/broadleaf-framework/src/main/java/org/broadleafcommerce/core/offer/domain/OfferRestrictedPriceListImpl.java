package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.pricelist.domain.PriceList;
import org.broadleafcommerce.common.pricelist.domain.PriceListImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_OFFER_PRICELIST_XREF")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
public class OfferRestrictedPriceListImpl  implements OfferRestrictedPriceList {

    protected static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferRestrictedPriceListId")
    @GenericGenerator(
        name="OfferRestrictedPriceListId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="OfferRestrictedPriceListImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.OfferRestrictedPriceListImpl")
        }
    )
    @Column(name = "ID")
    protected Long id;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    protected Offer offer;

    @ManyToOne(targetEntity = PriceListImpl.class, optional=false)
    @JoinColumn(name = "PRICE_LIST_ID")
    protected PriceList priceList;

    @Override
    public Offer getOffer() {
        return offer;
    }

    @Override
    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    @Override
    public PriceList getPriceList() {
        return priceList;
    }

    @Override
    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }
}
