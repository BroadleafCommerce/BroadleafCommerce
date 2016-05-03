/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.extensibility.jpa;

import java.io.Serializable;

public class ConditionalORMConfigDto implements Serializable {

    protected String puName;
    protected String conditionalProperty;

    public String getPuName() {
        return puName;
    }

    public void setPuName(String puName) {
        this.puName = puName;
    }

    public String getConditionalProperty() {
        return conditionalProperty;
    }

    public void setConditionalProperty(String conditionalProperty) {
        this.conditionalProperty = conditionalProperty;
    }
}
