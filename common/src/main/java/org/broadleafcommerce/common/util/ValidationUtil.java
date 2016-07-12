/*
 * #%L
 * BroadleafCommerce Common Libraries
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
            sb.append(";\n");
        }

        for (String globalError : globalErrors) {
            sb.append(processMessage(globalError));
            sb.append(";\n");
        }

        return "The entity has failed validation -\n" + sb.toString();
    }

    public static String processMessage(String globalError) {
        String friendlyName = BLCMessageUtils.getMessage(globalError);
        if (friendlyName != null) {
            return friendlyName;
        }
        return globalError;
    }
}
