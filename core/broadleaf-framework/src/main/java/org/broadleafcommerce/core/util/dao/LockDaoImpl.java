/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.util.dao;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.util.domain.Lock;
import org.broadleafcommerce.core.util.domain.LockImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(name = "inventory.guaranteed.check.enabled", havingValue = "true")
@Repository("blLockDao")
public class LockDaoImpl implements LockDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public Lock getLock(String lockId) {
        return em.find(LockImpl.class, lockId);
    }

    @Override
    @Transactional
    public void lock(String lockId) {
        Lock lock = em.find(LockImpl.class, lockId, LockModeType.PESSIMISTIC_WRITE);
        if (lock == null) {
            lock = entityConfiguration.createEntityInstance(Lock.class.getName(), Lock.class);
            lock.setId(lockId);
            em.persist(lock);
        }
    }

    @Override
    @Transactional
    public void unlock(String lockId) {
        Lock lock = em.find(LockImpl.class, lockId, LockModeType.PESSIMISTIC_WRITE);
        if (lock != null) {
            em.remove(lock);
        }
    }

    @Override
    @Transactional
    public void deleteLock(String lockId) {
        LockImpl lock = em.find(LockImpl.class, lockId);
        if (lock != null) {
            em.remove(lock);
        }
    }
}
