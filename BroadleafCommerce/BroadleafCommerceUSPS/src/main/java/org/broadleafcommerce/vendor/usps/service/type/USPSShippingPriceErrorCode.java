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
 * An extendible enumeration of usps shipping method types.
 * 
 * @author jfischer
 */
public class USPSShippingPriceErrorCode implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, USPSShippingPriceErrorCode> TYPES = new HashMap<String, USPSShippingPriceErrorCode>();

    public static final USPSShippingPriceErrorCode TOOMANYCONTAINERITEMS  = new USPSShippingPriceErrorCode("bl_items", "No more than 25 packages may be included in the request.");
    public static final USPSShippingPriceErrorCode WEIGHTNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_wns", "The package weight must be specified for this request.");
    public static final USPSShippingPriceErrorCode OVERWEIGHT  = new USPSShippingPriceErrorCode("bl_ow", "Package exceeds weight limit specified by carrier.");
    public static final USPSShippingPriceErrorCode SHAPENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_sns", "The package shape must be specified for this request.");
    public static final USPSShippingPriceErrorCode SHAPENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_shapesupport", "The package shape type specified is not supported.");
    public static final USPSShippingPriceErrorCode DIMENSIONSNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_dns", "The package dimensions must be specified for this request.");
    public static final USPSShippingPriceErrorCode GIRTHNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_dns", "The package girth must be specified for this request.");
    public static final USPSShippingPriceErrorCode PACKAGEIDNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_pkid", "The package id must be specified.");
    public static final USPSShippingPriceErrorCode ZIPNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_zip", "The origin and destination zip codes must be specified.");
    public static final USPSShippingPriceErrorCode ZIPLENGTH  = new USPSShippingPriceErrorCode("bl_ziplength", "The origin and destination zip codes must be 5 digits in length.");
    public static final USPSShippingPriceErrorCode UNITTYPENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_unit", "The unit of measure type specified is not supported.");
    public static final USPSShippingPriceErrorCode UNITTYPENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_type", "The dimension and weight unit of measure types must be specified for this request.");
    public static final USPSShippingPriceErrorCode SHIPDATETOOFAR  = new USPSShippingPriceErrorCode("bl_shipdate", "The ship date may only be 0 to 3 days in advance.");
    public static final USPSShippingPriceErrorCode SERVICENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_service", "The service must be specified for this request.");
    public static final USPSShippingPriceErrorCode SERVICENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_servicesupported", "The service type specified is not compatible with this version of the USPS api.");
    public static final USPSShippingPriceErrorCode FIRSTCLASSNOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_firstclass", "The first class container type must be specified for this request.");
    public static final USPSShippingPriceErrorCode SIZENOTSPECIFIED  = new USPSShippingPriceErrorCode("bl_size", "The container size type must be specified for this request.");
    public static final USPSShippingPriceErrorCode SIZENOTSUPPORTED  = new USPSShippingPriceErrorCode("bl_sizesupported", "The container size type specified is not supported.");
    public static final USPSShippingPriceErrorCode MACHINABLESPECIFIED = new USPSShippingPriceErrorCode("bl_machinable", "The machine sortable value must be specified for this request.");

    public static USPSShippingPriceErrorCode getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String message;

    public USPSShippingPriceErrorCode() {
        //do nothing
    }

    public USPSShippingPriceErrorCode(final String type, final String message) {
        setType(type);
        setMessage(message);
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
