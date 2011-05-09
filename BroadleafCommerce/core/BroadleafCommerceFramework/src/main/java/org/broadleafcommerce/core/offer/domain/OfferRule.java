package org.broadleafcommerce.core.offer.domain;

import java.io.Serializable;

public interface OfferRule extends Serializable {

	public Long getId();

	public void setId(Long id);

	public String getMatchRule();

	public void setMatchRule(String matchRule);

}