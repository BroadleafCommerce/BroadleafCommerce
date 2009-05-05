package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.OfferAudit;

public interface OfferAuditDao {
	public OfferAudit readAuditById(Long offerAuditId);
	
	public OfferAudit save(OfferAudit offerAudit);
	
	public void delete(OfferAudit offerAudit);
	
	public OfferAudit create();
}
