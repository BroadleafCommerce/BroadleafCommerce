package org.broadleafcommerce.promotion.domain;

import java.math.BigDecimal;
import java.sql.Date;

import org.broadleafcommerce.type.OfferType;
import org.broadleafcommerce.type.OfferUseType;

public interface Offer {
	public void setId(Long id) ;
	
	public Long getId();

	public String getName() ;

	public void setName(String name) ;

	public OfferType getType() ;

	public void setType(OfferType type) ;

	public OfferUseType getUseType();
	
	public void setUseType(OfferUseType useType);
	
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

	public String getScope() ;

	public void setScope(String scope);
	
}
