/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.rulebuilder.statement;

import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;

/**
 * @author jfischer
 * @author Elbert Bautista (elbertbautista)
 */
public class Expression {
    protected String field;
    protected BLCOperator operator;
    protected String value;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field.trim();
    }

    public BLCOperator getOperator() {
        return operator;
    }

    public void setOperator(BLCOperator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.trim();
    }
}
