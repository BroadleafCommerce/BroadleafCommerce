package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.offer.domain.CandidateItemOffer;
import org.broadleafcommerce.offer.domain.Offer;
import org.broadleafcommerce.util.money.Money;

public interface OrderItem {

    public Long getId();

    public void setId(Long id);

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

    public Category getCategory();

    public void setCategory(Category category);

    public List<CandidateItemOffer> getCandidateItemOffers();

    public void setCandidateItemOffers(List<CandidateItemOffer> candidateOffers);

    public List<CandidateItemOffer> addCandidateItemOffer(CandidateItemOffer candidateOffer);

    public void setAppliedItemOffers(List<Offer> appliedOffers);

    public List<Offer> getAppliedItemOffers();

    public List<Offer> addAppliedItemOffer(Offer appliedOffer);

    public void removeAllOffers();

    public boolean markForOffer();

    public int getMarkedForOffer();

    public boolean unmarkForOffer();

    public boolean isAllQuantityMarkedForOffer();

    public PersonalMessage getPersonalMessage();

    public void setPersonalMessage(PersonalMessage personalMessage);

    public boolean isInCategory(String categoryName);
}
