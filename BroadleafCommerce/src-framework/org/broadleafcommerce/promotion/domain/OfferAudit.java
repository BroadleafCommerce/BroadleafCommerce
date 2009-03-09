package org.broadleafcommerce.promotion.domain;

import java.math.BigDecimal;
import java.sql.Date;

public interface OfferAudit {
	public Long getId() ;

	public void setId(Long id) ;

	public Offer getOffer() ;

	public void setOffer(Offer offer) ;

	public Long getOfferCodeId() ;

	public void setOfferCodeId(Long offerCodeId) ;

	public Long getCustomerId() ;

	public void setCustomerId(Long customerId) ;

	public Long getOrderId() ;

	public void setOrderId(Long orderId) ;

	public void setFullfillmentGroupId(Long fullfillmentGroupId);
	
	public Long getFullfillmentGroupId();
	
	public BigDecimal getRelatedRetailPrice() ;

	public void setRelatedRetailPrice(BigDecimal relatedRetailPrice) ;

	public BigDecimal getRelatedSalePrice() ;

	public void setRelatedSalePrice(BigDecimal relatedSalePrice) ;

	public BigDecimal getRelatedPrice() ;
	
	public void setRelatedPrice(BigDecimal relatedPrice) ;

	public Date getRedeemedDate() ;

	public void setRedeemedDate(Date redeemedDate) ;

}
