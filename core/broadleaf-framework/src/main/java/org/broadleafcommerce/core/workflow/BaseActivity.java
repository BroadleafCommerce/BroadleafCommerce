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
package org.broadleafcommerce.core.workflow;

import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.core.Ordered;

import java.util.Map;

public abstract class BaseActivity<T extends ProcessContext<?>> implements Activity<T> {
    
    protected ErrorHandler errorHandler;
    protected String beanName;

    protected RollbackHandler<T> rollbackHandler;
    protected String rollbackRegion;
    protected Map<String, Object> stateConfiguration;
    protected boolean automaticallyRegisterRollbackHandler = false;
    protected int order = Ordered.LOWEST_PRECEDENCE;
    
    @Override
    public boolean shouldExecute(T context) {
        return true;
    }
    
    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setBeanName(final String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public RollbackHandler<T> getRollbackHandler() {
        return rollbackHandler;
    }

    @Override
    public void setRollbackHandler(RollbackHandler<T> rollbackHandler) {
        this.rollbackHandler = rollbackHandler;
    }

    @Override
    public String getRollbackRegion() {
        return rollbackRegion;
    }

    @Override
    public void setRollbackRegion(String rollbackRegion) {
        this.rollbackRegion = rollbackRegion;
    }

    @Override
    public Map<String, Object> getStateConfiguration() {
        return stateConfiguration;
    }

    @Override
    public void setStateConfiguration(Map<String, Object> stateConfiguration) {
        this.stateConfiguration = stateConfiguration;
    }

    @Override
    public boolean getAutomaticallyRegisterRollbackHandler() {
        return automaticallyRegisterRollbackHandler;
    }

    @Override
    public void setAutomaticallyRegisterRollbackHandler(boolean automaticallyRegisterRollbackHandler) {
        this.automaticallyRegisterRollbackHandler = automaticallyRegisterRollbackHandler;
    }
    
    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
