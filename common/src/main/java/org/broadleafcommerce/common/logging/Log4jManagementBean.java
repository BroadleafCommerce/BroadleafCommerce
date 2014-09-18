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
package org.broadleafcommerce.common.logging;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(objectName="org.broadleafcommerce:name=Log4JManangement", description="Logging Management", currencyTimeLimit=15)
public class Log4jManagementBean {

    @ManagedOperation(description="Activate info level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateInfo(String category) {
        LogManager.getLogger(category).setLevel(Level.INFO);
    }

    @ManagedOperation(description="Activate debug level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateDebug(String category) {
        LogManager.getLogger(category).setLevel(Level.DEBUG);
    }

    @ManagedOperation(description="Activate warn level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateWarn(String category) {
        LogManager.getLogger(category).setLevel(Level.WARN);
    }

    @ManagedOperation(description="Activate error level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateError(String category) {
        LogManager.getLogger(category).setLevel(Level.ERROR);
    }

    @ManagedOperation(description="Activate fatal level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category to set")})
    public void activateFatal(String category) {
        LogManager.getLogger(category).setLevel(Level.FATAL);
    }

    @ManagedOperation(description="Retrieve the category log level")
    @ManagedOperationParameters({@ManagedOperationParameter(name = "category", description = "the log4j category")})
    public String getLevel(String category) {
        return LogManager.getLogger(category).getLevel().toString();
    }
}
