/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
