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
import java.util.Hashtable;
import java.util.Map;

import org.broadleafcommerce.util.TypeEnumeration;

/**
 * An extendible enumeration of V3 service types.
 * 
 * @author jfischer
 */
public class USPSServiceV3Type implements Serializable, TypeEnumeration {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSServiceV3Type> types = new Hashtable<String, USPSServiceV3Type>();

    public static USPSServiceV3Type ALL  = new USPSServiceV3Type("ALL");
    public static USPSServiceV3Type ONLINE = new USPSServiceV3Type("ONLINE");
    public static USPSServiceV3Type FIRSTCLASS = new USPSServiceV3Type("FIRST CLASS");
    public static USPSServiceV3Type PRIORITY = new USPSServiceV3Type("PRIORITY");
    public static USPSServiceV3Type PRIORITYCOMMERCIAL = new USPSServiceV3Type("PRIORITY COMMERCIAL");
    public static USPSServiceV3Type EXPRESS = new USPSServiceV3Type("EXPRESS");
    public static USPSServiceV3Type EXPRESS_SH = new USPSServiceV3Type("EXPRESS SH");
    public static USPSServiceV3Type EXPRESS_HFP = new USPSServiceV3Type("EXPRESS HFP");
    public static USPSServiceV3Type EXPRESSCOMMERCIAL = new USPSServiceV3Type("EXPRESS COMMERCIAL");
    public static USPSServiceV3Type EXPRESS_SH_COMMERCIAL = new USPSServiceV3Type("EXPRESS SH COMMERCIAL");
    public static USPSServiceV3Type EXPRESS_HFP_COMMERCIAL = new USPSServiceV3Type("EXPRESS HFP COMMERCIAL");
    public static USPSServiceV3Type BPM = new USPSServiceV3Type("BPM");
    public static USPSServiceV3Type PARCEL = new USPSServiceV3Type("PARCEL");
    public static USPSServiceV3Type MEDIA = new USPSServiceV3Type("MEDIA");
    public static USPSServiceV3Type LIBRARY = new USPSServiceV3Type("LIBRARY");

    public static USPSServiceV3Type getInstance(String type) {
        return types.get(type);
    }

    private String type;

    public USPSServiceV3Type() {
        //do nothing
    }

    public USPSServiceV3Type(String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        if (!types.containsKey(type)) {
            types.put(type, this);
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
        USPSServiceV3Type other = (USPSServiceV3Type) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
