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

package org.broadleafcommerce.common.cache.engine;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class HydrationDescriptor {

    private Map<String, HydrationItemDescriptor> hydratedMutators;
    private Method[] idMutators;
    private String cacheRegion;
    
    public Map<String, HydrationItemDescriptor> getHydratedMutators() {
        return hydratedMutators;
    }
    
    public Method[] getIdMutators() {
        return idMutators;
    }
    
    public String getCacheRegion() {
        return cacheRegion;
    }

    public void setHydratedMutators(Map<String, HydrationItemDescriptor> hydratedMutators) {
        this.hydratedMutators = hydratedMutators;
    }

    public void setIdMutators(Method[] idMutators) {
        this.idMutators = idMutators;
    }

    public void setCacheRegion(String cacheRegion) {
        this.cacheRegion = cacheRegion;
    }
    
}
