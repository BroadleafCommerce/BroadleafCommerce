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

import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;

/**
 * An extendible enumeration of container size types.
 * 
 * @author jfischer
 */
public class USPSContainerSizeType extends ContainerSizeType {

    private static final long serialVersionUID = 1L;

    public static final USPSContainerSizeType REGULAR  = new USPSContainerSizeType("REGULAR", "Regular");
    public static final USPSContainerSizeType LARGE = new USPSContainerSizeType("LARGE", "Large");
    public static final USPSContainerSizeType OVERSIZE = new USPSContainerSizeType("OVERSIZE", "Oversize");

    public USPSContainerSizeType() {
        //do nothing
    }

    public USPSContainerSizeType(final String type, final String friendlyType) {
        super(type, friendlyType);
    }

}
