/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.web.expression;

import jakarta.annotation.Resource;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.common.web.expression.BroadleafVariableExpression;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.broadleafcommerce.openadmin.web.form.entity.Tab;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

/**
 * A {@link BroadleafVariableExpression} that assists with operations for Thymeleaf-layer operations on entity forms.
 *
 * @author Andre Azzolini (apazzolini)
 */
@Component("blEntityFormVariableExpression")
@ConditionalOnTemplating
public class EntityFormVariableExpression implements BroadleafVariableExpression {

    @Resource(name = "blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

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

    public String getFieldTooltip(Field field) {
        String tooltip = field.getTooltip() == null ? field.getDisplayValue() : field.getTooltip();
        if (tooltip != null) {
            return exploitProtectionService.htmlDecode(tooltip);
        }
        return "";
    }

}
