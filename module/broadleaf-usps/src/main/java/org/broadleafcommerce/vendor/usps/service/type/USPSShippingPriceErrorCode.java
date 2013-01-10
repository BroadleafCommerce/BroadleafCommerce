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

import org.broadleafcommerce.common.BroadleafEnumerationType;

/**
 * An extendible enumeration of usps shipping method types.
 * 
 * @author jfischer
 */
public class USPSShippingPriceErrorCode implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSShippingPriceErrorCode> TYPES = new HashMap<String, USPSShippingPriceErrorCode>();

    public static final USPSShippingPriceErrorCode TOOMANYCONTAINERITEMS  = new USPSShippingPriceErrorCode("bl_items", "Too Many Container Items", "No more than 25 packages may be included in the request.");
    public static final USPSShippingPriceErrorCode WEIGHTNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_wns", "Weight Not Specified", "The package weight must be specified for this request.");
    public static final USPSShippingPriceErrorCode OVERWEIGHT  = new USPSShippingPriceErrorCode("bl_ow", "Over Weight", "Package exceeds weight limit specified by carrier.");
    public static final USPSShippingPriceErrorCode SHAPENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_sns", "Shape Not Specified", "The package shape must be specified for this request.");
    public static final USPSShippingPriceErrorCode SHAPENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_shapesupport", "Shape Not Supported", "The package shape type specified is not supported.");
    public static final USPSShippingPriceErrorCode DIMENSIONSNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_dns", "Dimensions Not Specified", "The package dimensions must be specified for this request.");
    public static final USPSShippingPriceErrorCode GIRTHNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_dns", "Girth Not Specified", "The package girth must be specified for this request.");
    public static final USPSShippingPriceErrorCode PACKAGEIDNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_pkid", "Package ID Not Specified", "The package id must be specified.");
    public static final USPSShippingPriceErrorCode ZIPNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_zip", "Zip Not Specified", "The origin and destination zip codes must be specified.");
    public static final USPSShippingPriceErrorCode ZIPLENGTH  = new USPSShippingPriceErrorCode("bl_ziplength", "Zip Length", "The origin and destination zip codes must be 5 digits in length.");
    public static final USPSShippingPriceErrorCode UNITTYPENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_unit", "Unit Type Not Supported", "The unit of measure type specified is not supported.");
    public static final USPSShippingPriceErrorCode UNITTYPENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_type", "Unit Type Not Specified", "The dimension and weight unit of measure types must be specified for this request.");
    public static final USPSShippingPriceErrorCode SHIPDATETOOFAR  = new USPSShippingPriceErrorCode("bl_shipdate", "Ship Date Too Far In Advance", "The ship date may only be 0 to 3 days in advance.");
    public static final USPSShippingPriceErrorCode SERVICENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_service", "Service Not Specified", "The service must be specified for this request.");
    public static final USPSShippingPriceErrorCode SERVICENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_servicesupported", "Service Not Supported", "The service type specified is not compatible with this version of the USPS api.");
    public static final USPSShippingPriceErrorCode FIRSTCLASSNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_firstclass", "First Class Not Specified", "The first class container type must be specified for this request.");
    public static final USPSShippingPriceErrorCode SIZENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_size", "Size Not Specified", "The container size type must be specified for this request.");
    public static final USPSShippingPriceErrorCode SIZENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_sizesupported", "Size Not Supported", "The container size type specified is not supported.");
    public static final USPSShippingPriceErrorCode MACHINABLESPECIFIED = new USPSShippingPriceErrorCode("bl_machinable", "Machine Sortable Not Specified", "The machine sortable value must be specified for this request.");

    public static USPSShippingPriceErrorCode getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String message;
    private String friendlyType;

    public USPSShippingPriceErrorCode() {
        //do nothing
    }

    public USPSShippingPriceErrorCode(final String type, final String friendlyType, final String message) {
        this.friendlyType = friendlyType;
        setType(type);
        setMessage(message);
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

    public String getMessage() {
        return message;
    }

    private void setMessage(final String message) {
        this.message = message;
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
        USPSShippingPriceErrorCode other = (USPSShippingPriceErrorCode) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
