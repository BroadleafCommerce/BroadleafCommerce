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
package org.broadleafcommerce.openadmin.web.expression;

import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;

/**
 * A {@link BroadleafVariableExpression} that assists with operations for Thymeleaf-layer operations on entity forms.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class EntityFormVariableExpression implements BroadleafVariableExpression {
    
    @Override
    public String getName() {
        return "ef";
    }
    
    public boolean isTabActive(EntityForm ef, Tab tab) {
        boolean foundVisibleTab = false;

        for (Tab t : ef.getTabs()) {
            if (tab == t && !foundVisibleTab) {
                return true;
            } else if (tab != t && t.getIsVisible()) {
                foundVisibleTab = true;
            }
        }

        return false;
    }

}
