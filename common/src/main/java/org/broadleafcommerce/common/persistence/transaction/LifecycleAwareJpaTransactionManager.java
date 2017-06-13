/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * A customized {@link JpaTransactionManager} that will send Spring events at key lifecycle points during a transaction.
 * Listeners can perform additional work at the time of these events, such as logging (any persistence related activity
 * should be avoided). Event publishing attempts to be safe and will log exceptions without bubbling them. Event publishing
 * is disabled by default, but may be enabled by using the 'transaction.lifecycle.events.enabled=true' property, or by setting
 * the {@link #logEvents} property on a case-by-case basis.
 *
 * @author Jeff Fischer
 */
public class LifecycleAwareJpaTransactionManager extends JpaTransactionManager {

    private static final Log LOG = LogFactory.getLog(LifecycleAwareJpaTransactionManager.class);

    @Value("${transaction.lifecycle.events.enabled:false}")
    protected boolean defaultLogEvents = false;

    protected Boolean logEvents = null;

    @Autowired
    protected ApplicationEventPublisher publisher;

    @Override
    protected Object doGetTransaction() {
        Object transaction = super.doGetTransaction();
        if (isEnabled()) {
            try {
                publisher.publishEvent(new TransactionLifecycleEvent(this, TransactionLifecycle.GET_TRANSACTION, null, transaction));
            } catch (Throwable e) {
                LOG.error("Problem while publishing GET_TRANSACTION lifecycle event. Exception is logged but not bubbled.", e);
            }
        }
        return transaction;
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        super.doBegin(transaction, definition);
        if (isEnabled()) {
            try {
                publisher.publishEvent(new TransactionLifecycleEvent(this, TransactionLifecycle.BEGIN, null, transaction, definition));
            } catch (Throwable e) {
                LOG.error("Problem while publishing BEGIN lifecycle event. Exception is logged but not bubbled.", e);
            }
        }
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        try {
            super.doCommit(status);
            if (isEnabled()) {
                try {
                    publisher.publishEvent(new TransactionLifecycleEvent(this, TransactionLifecycle.COMMIT, null, status));
                } catch (Throwable e) {
                    LOG.error("Problem while publishing COMMIT lifecycle event. Exception is logged but not bubbled.", e);
                }
            }
        } catch (RuntimeException e) {
            if (isEnabled()) {
                try {
                    publisher.publishEvent(new TransactionLifecycleEvent(this, TransactionLifecycle.COMMIT, e, status));
                } catch (Throwable ex) {
                    LOG.error("Problem while publishing COMMIT lifecycle event. Exception is logged but not bubbled.", ex);
                }
            }
            throw e;
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        try {
            super.doRollback(status);
            if (isEnabled()) {
                try {
                    publisher.publishEvent(new TransactionLifecycleEvent(this, TransactionLifecycle.ROLLBACK, null, status));
                } catch (Throwable e) {
                    LOG.error("Problem while publishing ROLLBACK lifecycle event. Exception is logged but not bubbled.", e);
                }
            }
        } catch (RuntimeException e) {
            if (isEnabled()) {
                try {
                    publisher.publishEvent(new TransactionLifecycleEvent(this, TransactionLifecycle.ROLLBACK, e, status));
                } catch (Throwable ex) {
                    LOG.error("Problem while publishing ROLLBACK lifecycle event. Exception is logged but not bubbled.", ex);
                }
            }
            throw e;
        }
    }

    public Boolean getLogEvents() {
        return logEvents;
    }

    public void setLogEvents(Boolean logEvents) {
        this.logEvents = logEvents;
    }

    public boolean isEnabled() {
        return (logEvents != null && logEvents) || (logEvents == null && defaultLogEvents);
    }
}
