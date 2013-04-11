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

package org.broadleafcommerce.openadmin.client;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class MessageManager {

    Map<String, String> localizedStrings = new HashMap<String, String>();

    public void addConstants(i18nConstants constants) {
        constants.retrievei18nProperties(new i18nPropertiesClient() {
            @Override
            public void onSuccess(Map<String, String> localizedProperties) {
                localizedStrings.putAll(localizedProperties);
            }

            @Override
            public void onUnavailable(Throwable error) {
                throw new RuntimeException("Unable to process i18n constants", error);
            }
        });
    }

    public String getString(String i18nKey) {
        return localizedStrings.get(i18nKey);
    }

    public String replaceKeys(String templateProperty, String[] keyNames, String[] values) {
        for (int j=0;j<keyNames.length;j++) {
            templateProperty = templateProperty.replaceAll("\\$\\{"+keyNames[j]+"}", values[j]);
        }

        return templateProperty;
    }
}
