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

import org.broadleafcommerce.order.service.type.USPSServiceMethod;
import org.broadleafcommerce.common.BroadleafEnumerationType;

/**
 * An extendible enumeration of service types.
 * 
 * @author jfischer
 */
public class USPSServiceType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSServiceType> TYPES = new HashMap<String, USPSServiceType>();

    public static final USPSServiceType ALL  = new USPSServiceType("ALL", "All");
    public static final USPSServiceType ONLINE = new USPSServiceType("ONLINE", "Online");
    public static final USPSServiceType FIRSTCLASS = new USPSServiceType("FIRST CLASS", "First Class");
    public static final USPSServiceType PRIORITY = new USPSServiceType("PRIORITY", "Priority");
    public static final USPSServiceType PRIORITYCOMMERCIAL = new USPSServiceType("PRIORITY COMMERCIAL", "Priority Commercial");
    public static final USPSServiceType EXPRESS = new USPSServiceType("EXPRESS", "Express");
    public static final USPSServiceType EXPRESS_SH = new USPSServiceType("EXPRESS SH", "Express SH");
    public static final USPSServiceType EXPRESS_HFP = new USPSServiceType("EXPRESS HFP", "Express HFP");
    public static final USPSServiceType EXPRESSCOMMERCIAL = new USPSServiceType("EXPRESS COMMERCIAL", "Express Commercial");
    public static final USPSServiceType EXPRESS_SH_COMMERCIAL = new USPSServiceType("EXPRESS SH COMMERCIAL", "Express SH Commercial");
    public static final USPSServiceType EXPRESS_HFP_COMMERCIAL = new USPSServiceType("EXPRESS HFP COMMERCIAL", "Express HFP Commercial");
    public static final USPSServiceType BPM = new USPSServiceType("BPM", "BPM");
    public static final USPSServiceType PARCEL = new USPSServiceType("PARCEL", "Parcel");
    public static final USPSServiceType MEDIA = new USPSServiceType("MEDIA", "Media");
    public static final USPSServiceType LIBRARY = new USPSServiceType("LIBRARY", "Library");

    public static USPSServiceType getInstance(final String type) {
        return TYPES.get(type);
    }
    
    public static USPSServiceType getInstanceByServiceMethod(USPSServiceMethod method) {
        if(
                method.equals(USPSServiceMethod.FIRSTCLASS) ||
                method.equals(USPSServiceMethod.FIRSTCLASSKEYSANDIDS) ||
                method.equals(USPSServiceMethod.FIRSTCLASSPOSTCARDSTAMPED)
        ) {
            return FIRSTCLASS;
        } else if(
                method.equals(USPSServiceMethod.PRIORITYMAIL) ||
                method.equals(USPSServiceMethod.PRIORITYMAILFLATRATEBOX) ||
                method.equals(USPSServiceMethod.PRIORITYMAILFLATRATEENVELOPE) ||
                method.equals(USPSServiceMethod.PRIORITYMAILFLATRATELARGEBOX) ||
                method.equals(USPSServiceMethod.PRIORITYMAILKEYSANDIDS)
        ) {
            return PRIORITY;
        } else if(
                method.equals(USPSServiceMethod.EXPRESSMAILFLATRATEENVELOPE) ||
                method.equals(USPSServiceMethod.EXPRESSMAILPOTOADDRESSEE)
        ) {
            return EXPRESS;
        } else if(
                method.equals(USPSServiceMethod.EXPRESSMAILFLATRATEENVELOPEHOLDFORPICKUP) ||
                method.equals(USPSServiceMethod.EXPRESSMAILHOLDFORPICKUP)
        ) {
            return EXPRESS_HFP;
        } else if(
                method.equals(USPSServiceMethod.EXPRESSMAILFLATRATEENVELOPESUNDAYHOLIDAY) ||
                method.equals(USPSServiceMethod.EXPRESSMAILSUNDAYHOLIDAY)
        ) {
            return EXPRESS_SH;
        } else if(
                method.equals(USPSServiceMethod.BOUNDPRINTEDMATTER)
        ) {
            return BPM;
        } else if(
                method.equals(USPSServiceMethod.PARCELPOST)
        ) {
            return PARCEL;
        } else if(
                method.equals(USPSServiceMethod.MEDIAMAIL)
        ) {
            return MEDIA;
        } else if(
                method.equals(USPSServiceMethod.LIBRARY)
        ) {
            return LIBRARY;
        }
        
        return null;
    }

    private String type;
    private String friendlyType;

    public USPSServiceType() {
        //do nothing
    }

    public USPSServiceType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
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
