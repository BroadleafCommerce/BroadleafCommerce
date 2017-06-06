package org.broadleafcommerce.common.persistence.transaction;

import org.springframework.context.ApplicationEvent;

/**
 * Spring event published at key transaction lifecycle points.
 *
 * @see LifecycleAwareJpaTransactionManager
 * @see TransactionLifecycleMonitor
 * @author Jeff Fischer
 */
public class TransactionLifecycleEvent extends ApplicationEvent {

    protected TransactionLifecycle lifecycle;
    protected Object[] params;
    protected Throwable e;

    public TransactionLifecycleEvent(Object source, TransactionLifecycle lifecycle, Throwable e, Object... params) {
        super(source);
        this.lifecycle = lifecycle;
        this.e = e;
        this.params = params;
    }

    public Object[] getParams() {
        return params;
    }

    public TransactionLifecycle getLifecycle() {
        return lifecycle;
    }

    public Throwable getException() {
        return e;
    }
}
