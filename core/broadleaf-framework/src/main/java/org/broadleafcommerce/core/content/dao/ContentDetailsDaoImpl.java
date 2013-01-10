/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.core.content.dao;

import org.broadleafcommerce.common.util.dao.BatchRetrieveDao;
import org.broadleafcommerce.core.content.domain.ContentDetails;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author btaylor
 *
 */
@Repository("blContentDetailsDao")
public class ContentDetailsDaoImpl extends BatchRetrieveDao implements ContentDetailsDao {

    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.dao.ContentDetailsDao#delete(org.broadleafcommerce.core.content.domain.ContentDetails)
     */
    public void delete(ContentDetails contentDetails) {
        if (!em.contains(contentDetails)){
            contentDetails = readContentDetailsById(contentDetails.getId());
        }
        em.remove(contentDetails);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.dao.ContentDetailsDao#readContentDetailsById(java.lang.Long)
     */
    public ContentDetails readContentDetailsById(Integer id) {
        return (ContentDetails) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.core.content.domain.ContentDetails"), id);
    }

    public List<ContentDetails> readContentDetailsByOrderedIds(List<Integer> ids){
        Query query = em.createNamedQuery("BC_READ_CONTENT_DETAILS_BY_IDS");
        List<ContentDetails> cds = batchExecuteReadQuery(query, ids, "contentIds");
        List<ContentDetails> orderedCds = new ArrayList<ContentDetails>();
        for (Integer id : ids){
            for (ContentDetails cd : cds){
                if(id.intValue() ==  cd.getId().intValue()){
                    orderedCds.add(cd);
                }
            }
        }
        return orderedCds;

    }


    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.content.dao.ContentDetailsDao#save(org.broadleafcommerce.core.content.domain.ContentDetails)
     */
    public ContentDetails save(ContentDetails contentDetails) {
        return em.merge(contentDetails);
    }

}
