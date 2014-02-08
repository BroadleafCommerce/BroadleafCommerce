/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.openadmin.server.security.domain.ForgotPasswordSecurityToken;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    public ForgotPasswordSecurityToken saveToken(ForgotPasswordSecurityToken token) {
        return em.merge(token);
    }
}
