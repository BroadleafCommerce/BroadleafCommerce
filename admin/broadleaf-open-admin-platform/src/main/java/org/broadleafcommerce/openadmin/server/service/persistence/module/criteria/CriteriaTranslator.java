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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;

import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public interface CriteriaTranslator {

    TypedQuery<Serializable> translateQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings, Integer firstResult, Integer maxResults);

    TypedQuery<Serializable> translateCountQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings);

    TypedQuery<Serializable> translateMaxQuery(DynamicEntityDao dynamicEntityDao, String ceilingEntity, List<FilterMapping> filterMappings, String maxField);
}
