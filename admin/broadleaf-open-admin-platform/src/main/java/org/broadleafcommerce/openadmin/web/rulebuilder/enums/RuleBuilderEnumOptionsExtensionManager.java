/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.web.rulebuilder.enums;

import java.util.ArrayList;
import java.util.List;


/**
 * Extension Manager used to aggregate option values for all registered {@link RuleBuilderEnumOptionsExtensionListener}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class RuleBuilderEnumOptionsExtensionManager implements RuleBuilderEnumOptionsExtensionListener {
    
    protected List<RuleBuilderEnumOptionsExtensionListener> listeners = new ArrayList<RuleBuilderEnumOptionsExtensionListener>();

    @Override
    public String getOptionValues() {
        StringBuilder sb = new StringBuilder();
        for (RuleBuilderEnumOptionsExtensionListener listener : listeners) {
            sb.append(listener.getOptionValues()).append("\r\n");
        }
        return sb.toString();
    }
    
    public List<RuleBuilderEnumOptionsExtensionListener> getListeners() {
        return listeners;
    }
    
    public void setListeners(List<RuleBuilderEnumOptionsExtensionListener> listeners) {
        this.listeners = listeners;
    }

}