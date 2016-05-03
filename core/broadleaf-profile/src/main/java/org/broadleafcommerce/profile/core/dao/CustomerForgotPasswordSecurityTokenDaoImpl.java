/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.dao;


import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityToken;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * 
 * @author bpolster
 *
 */
@Repository("blCustomerForgotPasswordSecurityTokenDao")
public class CustomerForgotPasswordSecurityTokenDaoImpl implements CustomerForgotPasswordSecurityTokenDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public CustomerForgotPasswordSecurityToken readToken(String token) {
        return (CustomerForgotPasswordSecurityToken) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityToken"), token);        
    }

    @Override
    public List<CustomerForgotPasswordSecurityToken> readUnusedTokensByCustomerId(Long customerId) {
        TypedQuery<CustomerForgotPasswordSecurityToken> query = new TypedQueryBuilder<CustomerForgotPasswordSecurityToken>(CustomerForgotPasswordSecurityToken.class, "token")
                .addRestriction("token.customerId", "=", customerId)
                .addRestriction("token.tokenUsedFlag", "=", false)
                .toQuery(em);
        return query.getResultList();
    }

    @Override
    public CustomerForgotPasswordSecurityToken saveToken(CustomerForgotPasswordSecurityToken token) {
        return em.merge(token);
    }
}
