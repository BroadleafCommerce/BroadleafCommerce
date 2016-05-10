/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.artifact.image;

import org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.UnmarshalledParameter;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/10/11
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Operation {

    protected String name;
    protected Double factor;
    protected UnmarshalledParameter[] parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getFactor() {
        return factor;
    }

    public void setFactor(Double factor) {
        this.factor = factor;
    }

    public UnmarshalledParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(UnmarshalledParameter[] parameters) {
        this.parameters = parameters;
    }
}
