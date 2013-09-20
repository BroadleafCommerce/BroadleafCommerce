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

package org.broadleafcommerce.common.web.expression;

import org.broadleafcommerce.common.config.RuntimeEnvironmentPropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This Thymeleaf variable expression class provides access to runtime configuration properties that are configured
 * in development.properties, development-shared.properties, etc, for the current environment.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class PropertiesVariableExpression implements BroadleafVariableExpression {
    
    @Autowired
    protected RuntimeEnvironmentPropertiesManager propMgr;
    
    @Override
    public String getName() {
        return "props";
    }
    
    public String get(String propertyName) {
        return propMgr.getProperty(propertyName);
    }

    public int getAsInt(String propertyName) {
        return Integer.parseInt(propMgr.getProperty(propertyName));
    }

}
