/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.offer.dao.OfferAuditDao;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.order.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.annotation.Resource;


/**
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Service("blOfferAuditService")
public class OfferAuditServiceImpl implements OfferAuditService {
    
    @Resource(name = "blOfferAuditDao")
    protected OfferAuditDao offerAuditDao;
    
    @Override
    public OfferAudit readAuditById(Long offerAuditId) {
        return offerAuditDao.readAuditById(offerAuditId);
    }

    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public OfferAudit save(OfferAudit offerAudit) {
        return offerAuditDao.save(offerAudit);
    }
    
    @Override
    @Transactional(TransactionUtils.DEFAULT_TRANSACTION_MANAGER)
    public void delete(OfferAudit offerAudit) {
        offerAuditDao.delete(offerAudit);
    }

    @Override
    public OfferAudit create() {
        return offerAuditDao.create();
    }

    @Override
    public Long countUsesByCustomer(Order order, Long customerId, Long offerId) {
        return offerAuditDao.countUsesByCustomer(order, customerId, offerId);
    }
    
    
    @Deprecated
    @Override
    public Long countUsesByCustomer(Long customerId, Long offerId) {
        return offerAuditDao.countUsesByCustomer(customerId, offerId);
    }

    @Override
    public Long countOfferCodeUses(Order order, Long offerCodeId) {
        return offerAuditDao.countOfferCodeUses(order, offerCodeId);
    }
    
    @Deprecated
    @Override
    public Long countOfferCodeUses(Long offerCodeId) {
        return offerAuditDao.countOfferCodeUses(offerCodeId);
    }

    @Override
    public List<OfferAudit> readOfferAuditsByOrderId(Long orderId) {
        return offerAuditDao.readOfferAuditsByOrderId(orderId);
    }


}
