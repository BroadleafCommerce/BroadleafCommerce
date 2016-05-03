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
package org.broadleafcommerce.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericResponse {
    
    private List<String> errorCodes = new ArrayList<String>();
    private Map<String, List<String>> errorCodeMap = new HashMap<String, List<String>>();

    /**
     * Returns true if
     * @return
     */
    public boolean getHasErrors() {
        return errorCodes.size() > 0;
    }

    public List<String> getErrorCodesList() {
        return errorCodes;
    }

    public void addErrorCode(String errorCode) {
        errorCodes.add(errorCode);
        errorCodeMap.put(errorCode, new ArrayList<String>());
    }

    public void addErrorCode(String errorCode, List<String> arguments) {
        errorCodes.add(errorCode);
        errorCodeMap.put(errorCode, arguments);
    }

    public Object[] getErrorCodeArguments(String errorCode) {
        List<String> errorCodes = errorCodeMap.get(errorCode);
        if (errorCodes == null) {
            return new Object[0];
        } else {
            return errorCodes.toArray(new String[0]);
        }
    }
}
