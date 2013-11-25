/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.common.util.TransactionUtils;
import org.broadleafcommerce.core.offer.dao.OfferAuditDao;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long countUsesByCustomer(Long customerId, Long offerId) {
        return offerAuditDao.countUsesByCustomer(customerId, offerId);
    }

    @Override
    public Long countOfferCodeUses(Long offerCodeId) {
        return offerAuditDao.countOfferCodeUses(offerCodeId);
    }


}
