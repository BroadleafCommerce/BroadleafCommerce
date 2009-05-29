package org.broadleafcommerce.marketing.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.marketing.domain.TargetContent;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("targetContentDao")
public class TargetContentDaoJpa implements TargetContentDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource
    protected EntityConfiguration entityConfiguration;

    private String queryCacheableKey = "org.hibernate.cacheable";

    @Override
    public void delete(Long targetContentId) {
        TargetContent tc = readTargetContentById(targetContentId);
        em.remove(tc);
    }

    @Override
    public TargetContent save(TargetContent targetContent) {
        if(targetContent.getId() == null) {
            em.persist(targetContent);
        }else {
            targetContent = em.merge(targetContent);
        }
        return targetContent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentByNameType(String name, String type) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_NAME_TYPE");
        query.setParameter("name", name);
        query.setParameter("type", type);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TargetContent> readCurrentTargetContentsByPriority(int priority) {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS_BY_PRIORITY");
        query.setParameter("priority", priority);
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TargetContent readTargetContentById(Long targetContentId) {
        return (TargetContent) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.marketing.domain.TargetContent"), targetContentId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<TargetContent> readTargetContents() {
        Query query = em.createNamedQuery("BC_READ_TARGET_CONTENTS");
        query.setHint(getQueryCacheableKey(), true);
        return query.getResultList();
    }

    public String getQueryCacheableKey() {
        return queryCacheableKey;
    }

    public void setQueryCacheableKey(String queryCacheableKey) {
        this.queryCacheableKey = queryCacheableKey;
    }
}
