/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.web.form.component;

import org.broadleafcommerce.openadmin.client.dto.Entity;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class RuleBuilder {

    protected String className;
    protected String[] ruleVars;
    protected String[] configKeys;
    protected Entity[] entities;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String[] getRuleVars() {
        return ruleVars;
    }

    public void setRuleVars(String[] ruleVars) {
        this.ruleVars = ruleVars;
    }

    public String[] getConfigKeys() {
        return configKeys;
    }

    public void setConfigKeys(String[] configKeys) {
        this.configKeys = configKeys;
    }

    public Entity[] getEntities() {
        return entities;
    }

    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }
}
