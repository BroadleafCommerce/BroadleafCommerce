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

package org.broadleafcommerce.common.jmx;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class AspectUtil {

    public static Object exposeRootBean(Object managedBean) {
        try {
            if (AopUtils.isAopProxy(managedBean) && managedBean instanceof Advised) {
                Advised advised = (Advised) managedBean;
                managedBean = advised.getTargetSource().getTarget();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return managedBean;
    }

}
