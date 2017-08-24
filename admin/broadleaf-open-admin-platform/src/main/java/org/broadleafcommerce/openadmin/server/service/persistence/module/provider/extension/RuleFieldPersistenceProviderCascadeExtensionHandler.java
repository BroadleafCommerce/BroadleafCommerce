/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.provider.extension;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.rule.QuantityBasedRule;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataDTO;

/**
 * Special case - Handle propagated state for rules. This only applies in the presence of the enterprise module.
 *
 * @author Jeff Fischer
 */
public interface RuleFieldPersistenceProviderCascadeExtensionHandler extends ExtensionHandler {

    /**
     * Setup proper prod record enterprise state for a propagated rule addition.
     *
     * @param rule
     * @param dataDTO
     * @param resultHolder
     * @return
     */
    ExtensionResultStatusType postCascadeAdd(Object rule, DataDTO dataDTO, ExtensionResultHolder resultHolder);

}
