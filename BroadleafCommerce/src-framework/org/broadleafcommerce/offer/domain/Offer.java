package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.broadleafcommerce.type.OfferDiscountType;
import org.broadleafcommerce.type.OfferScopeType;
import org.broadleafcommerce.type.OfferType;

public interface Offer {
	public void setId(Long id) ;
	
	public Long getId();

	public String getName() ;

	public void setName(String name) ;

	public OfferType getType();
	
	public void setType(OfferType offerType);

	public OfferDiscountType getDiscountType() ;
	
	public void setDiscountType(OfferDiscountType type) ;

	public OfferScopeType getScopeType();
	
	public void setScopeType(OfferScopeType scopeType);
	
	public BigDecimal getValue();
	
	public void setValue(BigDecimal value);
	
	public int getPriority() ;

	public void setPriority(int priority) ;

	public Date getStartDate() ;

	public void setStartDate(Date startDate) ;

	public Date getEndDate() ;

	public void setEndDate(Date endDate) ;

	public boolean isStackable() ;

	public void setStackable(boolean stackable) ;

	public boolean isTargetSystem() ;

	public void setTargetSystem(boolean targetSystem) ;
	
	public List<OfferOrderItem> getOfferOrderItems();
	
	public void setOfferOrderItems(List<OfferOrderItem> offerOrderItems);

}
