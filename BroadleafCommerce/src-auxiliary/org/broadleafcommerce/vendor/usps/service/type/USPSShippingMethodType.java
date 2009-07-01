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

/**
 * An extendible enumeration of usps shipping method types.
 * 
 * @author jfischer
 */
public class USPSShippingMethodType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSShippingMethodType> types = new Hashtable<String, USPSShippingMethodType>();

    public static USPSShippingMethodType FIRSTCLASS  = new USPSShippingMethodType("0");
    public static USPSShippingMethodType PRIORITYMAIL = new USPSShippingMethodType("1");
    public static USPSShippingMethodType EXPRESSMAILHOLDFORPICKUP = new USPSShippingMethodType("2");
    public static USPSShippingMethodType EXPRESSMAILPOTOADDRESSEE = new USPSShippingMethodType("3");
    public static USPSShippingMethodType PARCELPOST = new USPSShippingMethodType("4");
    public static USPSShippingMethodType BOUNDPRINTEDMATTER = new USPSShippingMethodType("5");
    public static USPSShippingMethodType MEDIAMAIL = new USPSShippingMethodType("6");
    public static USPSShippingMethodType LIBRARY = new USPSShippingMethodType("7");
    public static USPSShippingMethodType FIRSTCLASSPOSTCARDSTAMPED = new USPSShippingMethodType("12");
    public static USPSShippingMethodType EXPRESSMAILFLATRATEENVELOPE = new USPSShippingMethodType("13");
    public static USPSShippingMethodType PRIORITYMAILFLATRATEENVELOPE = new USPSShippingMethodType("16");
    public static USPSShippingMethodType PRIORITYMAILFLATRATEBOX = new USPSShippingMethodType("17");
    public static USPSShippingMethodType PRIORITYMAILKEYSANDIDS = new USPSShippingMethodType("18");
    public static USPSShippingMethodType FIRSTCLASSKEYSANDIDS = new USPSShippingMethodType("19");
    public static USPSShippingMethodType PRIORITYMAILFLATRATELARGEBOX = new USPSShippingMethodType("22");
    public static USPSShippingMethodType EXPRESSMAILSUNDAYHOLIDAY = new USPSShippingMethodType("23");
    public static USPSShippingMethodType EXPRESSMAILFLATRATEENVELOPESUNDAYHOLIDAY = new USPSShippingMethodType("25");
    public static USPSShippingMethodType EXPRESSMAILFLATRATEENVELOPEHOLDFORPICKUP = new USPSShippingMethodType("27");

    public static USPSShippingMethodType getInstance(String type) {
        return types.get(type);
    }

    private String type;

    public USPSShippingMethodType() {
        //do nothing
    }

    public USPSShippingMethodType(String type) {
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
        USPSShippingMethodType other = (USPSShippingMethodType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
