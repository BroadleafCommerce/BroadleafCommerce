/*
 * #%L
 * BroadleafCommerce Workflow
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
package org.broadleafcommerce.common.util;

/**
 * @author Jeff Fischer
 */
public abstract class StreamCapableTransactionalOperationAdapter implements StreamCapableTransactionalOperation {

    protected Object[] pagedItems;

    @Override
    public void pagedExecute(Object[] param) throws Throwable {
        //do nothing
    }

    @Override
    public void executeAfterCommit(Object[] param) {
        //do nothing
    }

    @Override
    public void execute() throws Throwable {
        //do nothing
    }

    @Override
    public Object[] retrievePage(int startPos, int pageSize) {
        return null;
    }

    @Override
    public Long retrieveTotalCount() {
        return null;
    }

    public Object[] getPagedItems() {
        return pagedItems;
    }

    public void setPagedItems(Object[] pagedItems) {
        this.pagedItems = pagedItems;
    }

    @Override
    public boolean shouldRetryOnTransactionLockAcquisitionFailure() {
        return false;
    }

    @Override
    public int retryMaxCountOverrideForLockAcquisitionFailure() {
        return -1;
    }
}
