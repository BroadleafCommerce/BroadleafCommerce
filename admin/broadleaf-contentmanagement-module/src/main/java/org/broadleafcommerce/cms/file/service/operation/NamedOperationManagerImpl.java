/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.service.operation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class NamedOperationManagerImpl implements NamedOperationManager {

    protected List<NamedOperationComponent> namedOperationComponents = new ArrayList<NamedOperationComponent>();

    @Override
    public Map<String, String> manageNamedParameters(Map<String, String> parameterMap) {
        List<String> utilizedNames = new ArrayList<String>();
        Map<String, String> derivedMap = new LinkedHashMap<String, String>();
        for (NamedOperationComponent namedOperationComponent : namedOperationComponents) {
            utilizedNames.addAll(namedOperationComponent.setOperationValues(parameterMap, derivedMap));
        }
        for (String utilizedName : utilizedNames) {
            parameterMap.remove(utilizedName);
        }
        derivedMap.putAll(parameterMap);

        return derivedMap;
    }

    @Override
    public List<NamedOperationComponent> getNamedOperationComponents() {
        return namedOperationComponents;
    }

    public void setNamedOperationComponents(List<NamedOperationComponent> namedOperationComponents) {
        this.namedOperationComponents = namedOperationComponents;
    }
}
