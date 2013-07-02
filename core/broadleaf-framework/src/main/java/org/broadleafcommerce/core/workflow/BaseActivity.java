/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.workflow;

import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.core.Ordered;

import java.util.Map;

public abstract class BaseActivity<T extends ProcessContext<? extends Object>> implements Activity<T> {
    
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
