/*
 * #%L
 * BroadleafCommerce Common Libraries
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
