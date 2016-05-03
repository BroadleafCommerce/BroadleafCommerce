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
package org.broadleafcommerce.common.enumeration.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.springframework.stereotype.Repository;


@Repository("blDataDrivenEnumerationDao")
public class DataDrivenEnumerationDaoImpl implements DataDrivenEnumerationDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;
    
    @Override
    public DataDrivenEnumeration readEnumByKey(String enumKey) {
        TypedQuery<DataDrivenEnumeration> query = new TypedQueryBuilder<DataDrivenEnumeration>(DataDrivenEnumeration.class, "dde")
            .addRestriction("dde.key", "=", enumKey)
            .toQuery(em);
        return query.getSingleResult();
    }
    
    @Override
    public DataDrivenEnumerationValue readEnumValueByKey(String enumKey, String enumValueKey) {
        TypedQuery<DataDrivenEnumerationValue> query = 
                new TypedQueryBuilder<DataDrivenEnumerationValue>(DataDrivenEnumerationValue.class, "ddev")
            .addRestriction("ddev.type.key", "=", enumKey)
            .addRestriction("ddev.key", "=", enumValueKey)
            .toQuery(em);
        return query.getSingleResult();
    }

}
