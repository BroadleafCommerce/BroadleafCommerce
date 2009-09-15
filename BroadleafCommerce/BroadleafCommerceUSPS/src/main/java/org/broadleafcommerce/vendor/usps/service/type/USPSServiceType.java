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
package org.broadleafcommerce.vendor.usps.service.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * An extendible enumeration of service types.
 * 
 * @author jfischer
 */
public class USPSServiceType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSServiceType> TYPES = new HashMap<String, USPSServiceType>();

    public static final USPSServiceType ALL  = new USPSServiceType("ALL");
    public static final USPSServiceType ONLINE = new USPSServiceType("ONLINE");
    public static final USPSServiceType FIRSTCLASS = new USPSServiceType("FIRST CLASS");
    public static final USPSServiceType PRIORITY = new USPSServiceType("PRIORITY");
    public static final USPSServiceType PRIORITYCOMMERCIAL = new USPSServiceType("PRIORITY COMMERCIAL");
    public static final USPSServiceType EXPRESS = new USPSServiceType("EXPRESS");
    public static final USPSServiceType EXPRESS_SH = new USPSServiceType("EXPRESS SH");
    public static final USPSServiceType EXPRESS_HFP = new USPSServiceType("EXPRESS HFP");
    public static final USPSServiceType EXPRESSCOMMERCIAL = new USPSServiceType("EXPRESS COMMERCIAL");
    public static final USPSServiceType EXPRESS_SH_COMMERCIAL = new USPSServiceType("EXPRESS SH COMMERCIAL");
    public static final USPSServiceType EXPRESS_HFP_COMMERCIAL = new USPSServiceType("EXPRESS HFP COMMERCIAL");
    public static final USPSServiceType BPM = new USPSServiceType("BPM");
    public static final USPSServiceType PARCEL = new USPSServiceType("PARCEL");
    public static final USPSServiceType MEDIA = new USPSServiceType("MEDIA");
    public static final USPSServiceType LIBRARY = new USPSServiceType("LIBRARY");

    public static USPSServiceType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;

    public USPSServiceType() {
        //do nothing
    }

    public USPSServiceType(final String type) {
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
        USPSServiceType other = (USPSServiceType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
