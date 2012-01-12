/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.cms.web.utility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldValueHolder;
import org.broadleafcommerce.cms.file.service.StaticAssetService;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * FieldMapWrapper is used in combination with PageField and StructuredContentField classes.
 * It is used by the DisplayContentTag to allow for simpler use of these
 * entities within JSP pages.
 *
 * For example, instead of ${page.pageFields.body.value}, this class allows the JSP
 * syntax to be ${page.body} supporting more readable JSP code.
 *
 * Values may contain references to images maintained within the CMS.   This class
 * also rewrites those images if the system has properties set for ${asset.server.url.prefix}
 *
 * Created by bpolster.
 */
public class FieldMapWrapper implements Map {
    private static final Log LOG = LogFactory.getLog(FieldMapWrapper.class);

    private Map wrappedMap;
    private String cmsPrefix;
    private String envPrefix;
    private boolean isProductionSandbox;

    public FieldMapWrapper(Map wrappedMap, StaticAssetService staticAssetService, boolean secure, boolean isProductionSandbox) {
        this.wrappedMap = wrappedMap;
        this.isProductionSandbox = isProductionSandbox;

        cmsPrefix = staticAssetService.getStaticAssetUrlPrefix();
        if (secure) {
            envPrefix = staticAssetService.getStaticAssetEnvironmentSecureUrlPrefix();
        } else {
            envPrefix = staticAssetService.getStaticAssetEnvironmentUrlPrefix();
        }
    }

    @Override
    public int size() {
        return wrappedMap.size();
    }

    @Override
    public boolean isEmpty() {
        return wrappedMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return wrappedMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return wrappedMap.containsValue(o);
    }

    @Override
    public Object get(Object o) {
        Object originalObj = wrappedMap.get(o);
        if (originalObj != null) {
            if (originalObj instanceof FieldValueHolder) {
                FieldValueHolder field = (FieldValueHolder) originalObj;
                if (field.getProcessedValue() == null) {                    
                    if (envPrefix != null && field.getValue() != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Processing page or structured content field.   Replacing " + cmsPrefix + " with " + envPrefix + " and nulling out original value.");
                        }
                        // Set the processed value and clear out the old value (to save memory).   Note that this
                        // assumes that fields are read-only in the context of the application calling this method
                        // which is true for OutOfBox BLC design where Page and StructuredContent fields are only
                        // modifiable via the BLC admin.
                        field.setProcessedValue(field.getValue().replace(cmsPrefix, envPrefix), true);
                    } else {
                        // The processed value is the same, so no need to clear out the
                        // old value.
                        field.setProcessedValue(field.getValue(), false);
                    }
                }
                return field.getProcessedValue();
            }
        }
        // no matching map value found for this item.
        return null;
    }

    @Override
    public Object put(Object o, Object o1) {
        return wrappedMap.put(o,o1);
    }

    @Override
    public Object remove(Object o) {
        return wrappedMap.remove(o);
    }

    @Override
    public void putAll(Map map) {
        wrappedMap.putAll(map);
    }

    @Override
    public void clear() {
        wrappedMap.clear();
    }

    @Override
    public Set keySet() {
        return wrappedMap.keySet();
    }

    @Override
    public Collection values() {
        return wrappedMap.values();
    }

    @Override
    public Set entrySet() {
        return wrappedMap.entrySet();
    }
}
