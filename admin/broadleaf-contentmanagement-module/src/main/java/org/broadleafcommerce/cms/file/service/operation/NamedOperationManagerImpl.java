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

    public List<NamedOperationComponent> getNamedOperationComponents() {
        return namedOperationComponents;
    }

    public void setNamedOperationComponents(List<NamedOperationComponent> namedOperationComponents) {
        this.namedOperationComponents = namedOperationComponents;
    }
}
