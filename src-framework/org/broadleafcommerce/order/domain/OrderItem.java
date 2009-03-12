package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.offer.domain.OfferAudit;
import org.broadleafcommerce.util.money.Money;

public interface OrderItem {

    public Long getId();

    public void setId(Long id);

    public Sku getSku();

    public void setSku(Sku sku);

    public Order getOrder();

    public void setOrder(Order order);

    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getPrice();

    public void setPrice(Money price);

    public int getQuantity();

    public void setQuantity(int quantity);

    public List<Offer> getCandidateOffers();

    public void setCandidateOffers(List<Offer> canidateOffers);

    public List<Offer> addCandidateOffer(Offer candidateOffer);

    public List<OfferAudit> getAppliedOffers();

    public void setAppliedOffers(List<OfferAudit> appliedOffers);

    public List<OfferAudit> addAppliedOffer(OfferAudit offerAudit);
}
