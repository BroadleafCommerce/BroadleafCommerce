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

import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;

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
 * Created by bpolster.
 */
public class FieldMapWrapper implements Map {
    private Map wrappedMap;

    public FieldMapWrapper(Map wrappedMap) {
          this.wrappedMap = wrappedMap;
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
        Object obj = wrappedMap.get(o);
        if (obj != null) {
            if (obj instanceof StructuredContentField) {
                StructuredContentField field = (StructuredContentField) obj;
                return field.getValue();
            } else if (obj instanceof PageField) {
                PageField field = (PageField) obj;
                return field.getValue();
            }
        }
        return obj;
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
