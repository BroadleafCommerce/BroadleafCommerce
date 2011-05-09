package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;

public interface CandidateOffer extends Serializable {

	public Long getId();

	public void setId(Long id);
	
	public Offer getOffer();
	
	public void setOffer(Offer offer);
	
	public int getPriority();
}
