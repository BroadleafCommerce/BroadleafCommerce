/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.common.extensibility.context.merge;

import org.springframework.context.ApplicationContext;


public interface MergeBeanStatusProvider {
    
    /**
     * Typically used by the {@link AbstractMergeBeanPostProcessor} class to determine whether or not certain
     * lists should be processed or if they can be safely ignored.
     * 
     * @param bean
     * @param beanName
     * @param appCtx
     * @return whether or not processing should be triggered
     */
    public boolean isProcessingEnabled(Object bean, String beanName, ApplicationContext appCtx);

}
