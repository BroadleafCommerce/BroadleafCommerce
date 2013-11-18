/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.service.operation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class StaticMapNamedOperationComponent implements NamedOperationComponent {

    @Override
    public List<String> setOperationValues(Map<String, String> originalParameters, Map<String, String> derivedParameters) {
        List<String> utilizedNames = new ArrayList<String>();
        expandFulfilledMap(originalParameters, derivedParameters, utilizedNames);

        return utilizedNames;
    }

    protected void expandFulfilledMap(Map<String, String> originalParameters, Map<String, String> derivedParameters, List<String> utilizedNames) {
        for (Map.Entry<String, String> entry : originalParameters.entrySet()) {
            if (namedOperations.containsKey(entry.getKey())) {
                expandFulfilledMap(namedOperations.get(entry.getKey()), derivedParameters, utilizedNames);
                if (!utilizedNames.contains(entry.getKey())) {
                    utilizedNames.add(entry.getKey());
                }
            } else {
                derivedParameters.put(entry.getKey(), entry.getValue());
            }
        }
    }

    protected LinkedHashMap<String, LinkedHashMap<String, String>> namedOperations = new LinkedHashMap<String, LinkedHashMap<String, String>>();

    public LinkedHashMap<String, LinkedHashMap<String, String>> getNamedOperations() {
        return namedOperations;
    }

    public void setNamedOperations(LinkedHashMap<String, LinkedHashMap<String, String>> namedOperations) {
        this.namedOperations = namedOperations;
    }
}
