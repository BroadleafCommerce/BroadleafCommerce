/*
 * Copyright 2008-20011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.field.dao;

import org.broadleafcommerce.cms.field.domain.FieldData;
import org.broadleafcommerce.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by bpolster.
 */
@Repository("blFileDao")
public class FileDaoImpl implements FileDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public FieldData readFieldDataById(Long id) {
        return (FieldData) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.cms.field.domain.FieldData"), id);
    }

    @Override
    public FieldData updateFieldData(FieldData fieldData) {
        em.clear();
        return em.merge(fieldData);
    }

    @Override
    public void delete(FieldData fieldData) {
        if (!em.contains(fieldData)) {
            fieldData = (FieldData) readFieldDataById(fieldData.getId());
        }
        em.remove(fieldData);
    }

    @Override
    public FieldData addFieldData(FieldData fieldData) {
        em.persist(fieldData);
        em.flush();
        return fieldData;
    }

}
