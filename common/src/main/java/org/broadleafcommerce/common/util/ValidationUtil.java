/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Chad Harchar (charchar)
 */
public class ValidationUtil {

    public static String buildErrorMessage(Map<String, List<String>> propertyErrors, List<String> globalErrors) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : propertyErrors.entrySet()) {
            Iterator<String> itr = entry.getValue().iterator();
            while (itr.hasNext()) {
                sb.append(entry.getKey());
                sb.append(" : ");

                String errorMessage = itr.next();
                sb.append(processMessage(errorMessage));

                if (itr.hasNext()) {
                    sb.append(" / ");
                }
            }
            sb.append("; ");
        }

        for (String globalError : globalErrors) {
            sb.append(processMessage(globalError));
            sb.append("; ");
        }

        return "The entity has failed validation - " + sb.toString();
    }

    public static String processMessage(String globalError) {
        String friendlyName = BLCMessageUtils.getMessage(globalError);
        if (friendlyName != null) {
            return friendlyName;
        }
        return globalError;
    }
}
