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
package org.broadleafcommerce.common.web.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class BLCAdminDialect extends AbstractDialect {

    private Set<IProcessor> processors = new HashSet<IProcessor>();

    @Override
    public String getPrefix() {
        return "blc_admin";
    }

    @Override
    public boolean isLenient() {
        return true;
    }

    @Override
    public Set<IProcessor> getProcessors() {
        return processors;
    }

    public void setProcessors(Set<IProcessor> processors) {
        this.processors = processors;
    }

}
