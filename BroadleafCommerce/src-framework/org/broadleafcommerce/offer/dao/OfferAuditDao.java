package org.broadleafcommerce.offer.dao;

import org.broadleafcommerce.offer.domain.OfferAudit;

public interface OfferAuditDao {
	public OfferAudit readAuditById(Long offerAuditId);
	
	public OfferAudit maintainOfferAudit(OfferAudit offerAudit);
	
	public void deleteOfferAudit(OfferAudit offerAudit);
	
	public OfferAudit create();
}
