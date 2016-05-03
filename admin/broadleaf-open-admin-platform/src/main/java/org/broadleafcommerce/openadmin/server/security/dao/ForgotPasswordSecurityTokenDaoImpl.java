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
package org.broadleafcommerce.openadmin.server.security.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.openadmin.server.security.domain.ForgotPasswordSecurityToken;
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
@Repository("blForgotPasswordSecurityTokenDao")
public class ForgotPasswordSecurityTokenDaoImpl implements ForgotPasswordSecurityTokenDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public ForgotPasswordSecurityToken readToken(String token) {
        return (ForgotPasswordSecurityToken) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.openadmin.server.security.domain.ForgotPasswordSecurityToken"), token);        
    }

    @Override
    public List<ForgotPasswordSecurityToken> readUnusedTokensByAdminUserId(Long adminUserId) {
        TypedQuery<ForgotPasswordSecurityToken> query = new TypedQueryBuilder<ForgotPasswordSecurityToken>(ForgotPasswordSecurityToken.class, "token")
                .addRestriction("token.adminUserId", "=", adminUserId)
                .addRestriction("token.tokenUsedFlag", "=", false)
                .toQuery(em);
        return query.getResultList();
    }

    @Override
    public ForgotPasswordSecurityToken saveToken(ForgotPasswordSecurityToken token) {
        return em.merge(token);
    }
}
