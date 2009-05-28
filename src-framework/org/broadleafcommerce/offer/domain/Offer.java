package org.broadleafcommerce.offer.domain;

import java.util.Date;

import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.offer.service.type.OfferScopeType;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.util.money.Money;

public interface Offer {

    public void setId(Long id);

    public Long getId();

    public String getCode();

    public void setCode(String code);

    public String getName();

    public void setName(String name);

    public OfferType getType();

    public void setType(OfferType offerType);

    public OfferDiscountType getDiscountType();

    public void setDiscountType(OfferDiscountType type);

    public OfferScopeType getScopeType();

    public void setScopeType(OfferScopeType scopeType);

    public Money getValue();

    public void setValue(Money value);

    public int getPriority();

    public void setPriority(int priority);

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    public boolean isStackable();

    public void setStackable(boolean stackable);

    public String getTargetSystem();

    public void setTargetSystem(String targetSystem);

//    public boolean getApplyDiscountToSalePrice();

//    public void setApplyDiscountToSalePrice(boolean applyToSalePrice);

    public String getAppliesToOrderRules();

    public void setAppliesToOrderRules(String appliesToRules);

    public String getAppliesToCustomerRules();

    public void setAppliesToCustomerRules(String appliesToCustomerRules);

    public Money getDiscountPrice() ;

    public void setDiscountPrice(Money calculatedDiscount);

    public boolean isApplyDiscountToMarkedItems();

    public void setApplyDiscountToMarkedItems(boolean applyDiscountToMarkedItems);

    public boolean isCombinableWithOtherOffers();

    public void setCombinableWithOtherOffers(boolean combinableWithOtherOffers);

}
