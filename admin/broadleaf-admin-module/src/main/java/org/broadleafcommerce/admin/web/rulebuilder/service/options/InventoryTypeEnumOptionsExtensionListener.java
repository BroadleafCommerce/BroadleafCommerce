/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.admin.web.rulebuilder.service.options;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.broadleafcommerce.common.BroadleafEnumerationType;
import org.broadleafcommerce.common.time.HourOfDayType;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.openadmin.web.rulebuilder.enums.AbstractRuleBuilderEnumOptionsExtensionListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Rule Builder enum options provider for {@link HourOfDayType}
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blInventoryTypeOptionsExtensionListener")
public class InventoryTypeEnumOptionsExtensionListener extends AbstractRuleBuilderEnumOptionsExtensionListener {

    /**
     * Overridden to remove deprecated options
     */
    @Override
    protected Map<String, ? extends BroadleafEnumerationType> getTypes(Class<? extends BroadleafEnumerationType> clazz) {
        
        try {
            Map<String, ? extends BroadleafEnumerationType> options =
                    (Map<String, ? extends BroadleafEnumerationType>) FieldUtils.readStaticField(clazz, "TYPES", true);
            options.remove("NONE");
            options.remove("BASIC");
            return options;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected Map<String, Class<? extends BroadleafEnumerationType>> getValuesToGenerate() {
        Map<String, Class<? extends BroadleafEnumerationType>> map = 
                new HashMap<String, Class<? extends BroadleafEnumerationType>>();
        
        map.put("blcOptions_InventoryType", InventoryType.class);
        
        return map;
    }

}
