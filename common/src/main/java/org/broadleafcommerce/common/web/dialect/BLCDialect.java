/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class BLCDialect extends AbstractDialect {
    
    private Set<IProcessor> processors = new HashSet<IProcessor>();

    @Override
    public String getPrefix() {
        return "blc";
    }

    @Override
    public boolean isLenient() {
        return true;
    }
    
    @Override 
    public Set<IProcessor> getProcessors() {        
        return processors; 
    } 
    
    public void setProcessors(Set<IProcessor> processors) {
        this.processors = processors;
    }

}
