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

package org.broadleafcommerce.order.service.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jfischer
 *
 */
public class USPSServiceMethod implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSServiceMethod> TYPES = new HashMap<String, USPSServiceMethod>();
    
    public static final USPSServiceMethod FIRSTCLASS  = new USPSServiceMethod("FIRSTCLASS");
    public static final USPSServiceMethod PRIORITYMAIL = new USPSServiceMethod("PRIORITYMAIL");
    public static final USPSServiceMethod EXPRESSMAILHOLDFORPICKUP = new USPSServiceMethod("EXPRESSMAILHOLDFORPICKUP");
    public static final USPSServiceMethod EXPRESSMAILPOTOADDRESSEE = new USPSServiceMethod("EXPRESSMAILPOTOADDRESSEE");
    public static final USPSServiceMethod PARCELPOST = new USPSServiceMethod("PARCELPOST");
    public static final USPSServiceMethod BOUNDPRINTEDMATTER = new USPSServiceMethod("BOUNDPRINTEDMATTER");
    public static final USPSServiceMethod MEDIAMAIL = new USPSServiceMethod("MEDIAMAIL");
    public static final USPSServiceMethod LIBRARY = new USPSServiceMethod("LIBRARY");
    public static final USPSServiceMethod FIRSTCLASSPOSTCARDSTAMPED = new USPSServiceMethod("FIRSTCLASSPOSTCARDSTAMPED");
    public static final USPSServiceMethod EXPRESSMAILFLATRATEENVELOPE = new USPSServiceMethod("EXPRESSMAILFLATRATEENVELOPE");
    public static final USPSServiceMethod PRIORITYMAILFLATRATEENVELOPE = new USPSServiceMethod("PRIORITYMAILFLATRATEENVELOPE");
    public static final USPSServiceMethod PRIORITYMAILFLATRATEBOX = new USPSServiceMethod("PRIORITYMAILFLATRATEBOX");
    public static final USPSServiceMethod PRIORITYMAILKEYSANDIDS = new USPSServiceMethod("PRIORITYMAILKEYSANDIDS");
    public static final USPSServiceMethod FIRSTCLASSKEYSANDIDS = new USPSServiceMethod("FIRSTCLASSKEYSANDIDS");
    public static final USPSServiceMethod PRIORITYMAILFLATRATELARGEBOX = new USPSServiceMethod("PRIORITYMAILFLATRATELARGEBOX");
    public static final USPSServiceMethod EXPRESSMAILSUNDAYHOLIDAY = new USPSServiceMethod("EXPRESSMAILSUNDAYHOLIDAY");
    public static final USPSServiceMethod EXPRESSMAILFLATRATEENVELOPESUNDAYHOLIDAY = new USPSServiceMethod("EXPRESSMAILFLATRATEENVELOPESUNDAYHOLIDAY");
    public static final USPSServiceMethod EXPRESSMAILFLATRATEENVELOPEHOLDFORPICKUP = new USPSServiceMethod("EXPRESSMAILFLATRATEENVELOPEHOLDFORPICKUP");
    
    public static USPSServiceMethod getInstance(final String type) {
        return TYPES.get(type);
    }
    
    private String type;

    public USPSServiceMethod() {
        //do nothing
    }

    public USPSServiceMethod(final String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        USPSServiceMethod other = (USPSServiceMethod) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
