package org.broadleafcommerce.admin.web.rulebuilder.service.options;

import org.broadleafcommerce.common.BroadleafEnumerationType;
import org.broadleafcommerce.common.time.HourOfDayType;
import org.broadleafcommerce.core.inventory.service.type.InventoryType;
import org.broadleafcommerce.openadmin.web.rulebuilder.enums.AbstractRuleBuilderEnumOptionsExtensionListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Rule Builder enum options provider for {@link HourOfDayType}
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class InventoryTypeEnumOptionsExtensionListener extends AbstractRuleBuilderEnumOptionsExtensionListener {

    @Override
    protected Map<String, Class<? extends BroadleafEnumerationType>> getValuesToGenerate() {
        Map<String, Class<? extends BroadleafEnumerationType>> map = 
                new HashMap<String, Class<? extends BroadleafEnumerationType>>();
        
        map.put("blcOptions_InventoryType", InventoryType.class);
        
        return map;
    }

}
