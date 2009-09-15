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

import org.broadleafcommerce.util.StringUtil;

/**
 * An extendible enumeration of usps shipping method types.
 * 
 * @author jfischer
 */
public class USPSServiceResponseType implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSServiceResponseType> TYPES = new HashMap<String, USPSServiceResponseType>();

    public static final USPSServiceResponseType FIRSTCLASS  = new USPSServiceResponseType("0", "First Class");
    public static final USPSServiceResponseType PRIORITYMAIL = new USPSServiceResponseType("1", "Priority Mail");
    public static final USPSServiceResponseType EXPRESSMAILHOLDFORPICKUP = new USPSServiceResponseType("2", "Express Mail Hold for Pickup");
    public static final USPSServiceResponseType EXPRESSMAILPOTOADDRESSEE = new USPSServiceResponseType("3", "Express Mail PO to Addressee");
    public static final USPSServiceResponseType PARCELPOST = new USPSServiceResponseType("4", "Parcel Post");
    public static final USPSServiceResponseType BOUNDPRINTEDMATTER = new USPSServiceResponseType("5", "Bound Printed Matter");
    public static final USPSServiceResponseType MEDIAMAIL = new USPSServiceResponseType("6", "Media Mail");
    public static final USPSServiceResponseType LIBRARY = new USPSServiceResponseType("7", "Library Mail");
    public static final USPSServiceResponseType FIRSTCLASSPOSTCARDSTAMPED = new USPSServiceResponseType("12", "First Class Postcard Stamped");
    public static final USPSServiceResponseType EXPRESSMAILFLATRATEENVELOPE = new USPSServiceResponseType("13", "Express Mail Flat Rate Envelope");
    public static final USPSServiceResponseType PRIORITYMAILFLATRATEENVELOPE = new USPSServiceResponseType("16", "Priority Mail Flat Rate Envelope");
    public static final USPSServiceResponseType PRIORITYMAILFLATRATEBOX = new USPSServiceResponseType("17", "Priority Mail Flat Rate Box");
    public static final USPSServiceResponseType PRIORITYMAILKEYSANDIDS = new USPSServiceResponseType("18", "Priority Mail Keys and IDs");
    public static final USPSServiceResponseType FIRSTCLASSKEYSANDIDS = new USPSServiceResponseType("19", "First Class Keys and IDs");
    public static final USPSServiceResponseType PRIORITYMAILFLATRATELARGEBOX = new USPSServiceResponseType("22", "Priority Mail Flat Rate Large Box");
    public static final USPSServiceResponseType EXPRESSMAILSUNDAYHOLIDAY = new USPSServiceResponseType("23", "Express Mail Sunday/Holiday");
    public static final USPSServiceResponseType EXPRESSMAILFLATRATEENVELOPESUNDAYHOLIDAY = new USPSServiceResponseType("25", "Express Mail Flat Rate Envelope Sunday/Holiday");
    public static final USPSServiceResponseType EXPRESSMAILFLATRATEENVELOPEHOLDFORPICKUP = new USPSServiceResponseType("27", "Express Mail Flat Rate Envelope Hold For Pickup");

    public static USPSServiceResponseType getInstance(final String type) {
        return TYPES.get(type);
    }

    public static USPSServiceResponseType getInstanceByDescription(final String description) {
        //remove any dimension callouts
        String lDescription = description.replaceAll("\\(.*?\\)", "");
        USPSServiceResponseType closestMatch = null;
        Double closestChecksumDeviation = null;
        for (USPSServiceResponseType type : TYPES.values()) {
            double deviation = StringUtil.determineSimilarity(lDescription, type.getDescription());
            if (
                    (closestChecksumDeviation == null && deviation <= 5000000.0) ||
                    (closestChecksumDeviation != null && deviation < closestChecksumDeviation)
            ){
                closestChecksumDeviation = deviation;
                closestMatch = type;
            }
        }
        return closestMatch;
    }

    private String type;
    private String description;

    public USPSServiceResponseType() {
        //do nothing
    }

    public USPSServiceResponseType(final String type, final String description) {
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
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
        USPSServiceResponseType other = (USPSServiceResponseType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
