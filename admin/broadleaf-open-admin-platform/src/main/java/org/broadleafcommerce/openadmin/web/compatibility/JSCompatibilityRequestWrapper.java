/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.compatibility;


import org.broadleafcommerce.openadmin.server.service.JSCompatibilityHelper;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.savedrequest.Enumerator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Jeff Fischer
 */
public class JSCompatibilityRequestWrapper extends FirewalledRequest {

    public JSCompatibilityRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public void reset() {
        //do nothing
    }

    @Override
    public String getContextPath() {
        return JSCompatibilityHelper.unencode(super.getContextPath());
    }

    @Override
    public String getPathTranslated() {
        return JSCompatibilityHelper.unencode(super.getPathTranslated());
    }

    @Override
    public String getQueryString() {
        return JSCompatibilityHelper.unencode(super.getQueryString());
    }

    @Override
    public String getRequestURI() {
        return JSCompatibilityHelper.unencode(super.getRequestURI());
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(JSCompatibilityHelper.unencode(super.getRequestURL().toString()));
    }

    @Override
    public String getServletPath() {
        return JSCompatibilityHelper.unencode(super.getServletPath());
    }

    @Override
    public String getParameter(String name) {
        return JSCompatibilityHelper.unencode(super.getParameter(name));
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Enumeration getParameterNames() {
        List<String> names = new ArrayList<String>();
        Enumeration enumeration = super.getParameterNames();
        while (enumeration.hasMoreElements()) {
            names.add(JSCompatibilityHelper.unencode((String) enumeration.nextElement()));
        }

        return new Enumerator<String>(names);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map getParameterMap() {
        Map params = super.getParameterMap();
        Map temp = new LinkedHashMap();
        for (Object key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof String) {
                temp.put(JSCompatibilityHelper.unencode((String) key), JSCompatibilityHelper.unencode((String) value));
            } else {
                String[] vals = (String[]) value;
                String[] tempVals = new String[vals.length];
                int j = 0;
                for (String val : vals) {
                    tempVals[j] = JSCompatibilityHelper.unencode(val);
                    j++;
                }
                temp.put(JSCompatibilityHelper.unencode((String) key), tempVals);
            }
        }
        return temp;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Map<String, String> getParameterNameConversionMap() {
        if (getAttribute("requestParameterConversionMap") == null) {
            Map<String, String> map = new HashMap<String, String>();
            Enumeration enumeration = super.getParameterNames();
            while (enumeration.hasMoreElements()) {
                String temp = (String) enumeration.nextElement();
                map.put(JSCompatibilityHelper.unencode(temp), temp);
            }
            setAttribute("requestParameterConversionMap", map);
        }

        return (Map<String, String>) getAttribute("requestParameterConversionMap");
    }
    
    @Override
    public String[] getParameterValues(String name) {
        return super.getParameterValues(getParameterNameConversionMap().get(name));
    }

}
