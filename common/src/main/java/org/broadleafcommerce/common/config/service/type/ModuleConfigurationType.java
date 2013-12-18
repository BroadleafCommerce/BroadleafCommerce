/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.config.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


public class ModuleConfigurationType implements BroadleafEnumerationType, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Map<String, ModuleConfigurationType> TYPES = new LinkedHashMap<String, ModuleConfigurationType>();

    public static final ModuleConfigurationType FULFILLMENT_PRICING = new ModuleConfigurationType("FULFILLMENT_PRICING", "Fulfillment Pricing Module");
    public static final ModuleConfigurationType TAX_CALCULATION = new ModuleConfigurationType("TAX_CALCULATION", "Tax Calculation Module");
    public static final ModuleConfigurationType ADDRESS_VERIFICATION = new ModuleConfigurationType("ADDRESS_VERIFICATION", "Address Verification Module");
    public static final ModuleConfigurationType PAYMENT_PROCESSOR = new ModuleConfigurationType("PAYMENT_PROCESSOR", "Payment Processor Module");
    public static final ModuleConfigurationType CDN_PROVIDER = new ModuleConfigurationType("CDN_PROVIDER", "Content Delivery Network Module");

    public static ModuleConfigurationType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public ModuleConfigurationType() {
        //do nothing
    }

    public ModuleConfigurationType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
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
        ModuleConfigurationType other = (ModuleConfigurationType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
