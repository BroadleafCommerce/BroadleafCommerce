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
package org.broadleafcommerce.core.util.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.util.domain.CodeType;
import org.broadleafcommerce.core.util.domain.CodeTypeImpl;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Repository("blCodeTypeDao")
public class CodeTypeDaoImpl implements CodeTypeDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public CodeType create() {
        return ((CodeType) entityConfiguration.createEntityInstance(CodeType.class.getName()));
    }

    @SuppressWarnings("unchecked")
    public List<CodeType> readAllCodeTypes() {
        Query query = em.createNamedQuery("BC_READ_ALL_CODE_TYPES");
        return query.getResultList();
    }

    public void delete(CodeType codeType) {
        if (!em.contains(codeType)) {
            codeType = (CodeType) em.find(CodeTypeImpl.class, codeType.getId());
        }
        em.remove(codeType);
    }

    public CodeType readCodeTypeById(Long codeTypeId) {
        return (CodeType) em.find(entityConfiguration.lookupEntityClass(CodeType.class.getName()), codeTypeId);
    }

    @SuppressWarnings("unchecked")
    public List<CodeType> readCodeTypeByKey(String key) {
        Query query = em.createNamedQuery("BC_READ_CODE_TYPE_BY_KEY");
        query.setParameter("key", key);
        List<CodeType> result = query.getResultList();
        return result;
    }

    public CodeType save(CodeType codeType) {
        if(codeType.getId()==null) {
            em.persist(codeType);
        }else {
            codeType = em.merge(codeType);
        }
        return codeType;
    }

}
