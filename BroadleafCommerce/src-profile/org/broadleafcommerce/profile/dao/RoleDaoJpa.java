package org.broadleafcommerce.profile.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.CustomerRole;
import org.broadleafcommerce.profile.util.EntityConfiguration;
import org.springframework.stereotype.Repository;

@Repository("roleDao")
public class RoleDaoJpa implements RoleDao {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @PersistenceContext(unitName = "blPU")
    private EntityManager em;

    @Resource
    private EntityConfiguration entityConfiguration;

    private String queryCacheableKey = "org.hibernate.cacheable";

    @SuppressWarnings("unchecked")
    public Address readAddressById(Long id) {
        return (Address) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.profile.domain.Address"), id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerRole> readCustomerRolesByCustomerId(Long customerId) {
        Query query = em.createNamedQuery("BC_READ_CUSTOMER_ROLES_BY_CUSTOMER_ID");
        query.setParameter("customerId", customerId);
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
