package org.broadleafcommerce.offer.domain;

public interface OfferCode {
	public Long getId() ;

	public void setId(Long id) ;

	public Offer getOffer() ;

	public void setOffer(Offer offer) ;

	public String getOfferCode();
	
	public void setOfferCode(String offerCode);
	
	public int getMaxUses() ;

	public void setMaxUses(int maxUses) ;

	public int getUses() ;

	public void setUses(int uses) ;
	
	

}
