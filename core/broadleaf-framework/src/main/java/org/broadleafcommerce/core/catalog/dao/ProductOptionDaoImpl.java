package org.broadleafcommerce.core.catalog.dao;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.springframework.stereotype.Repository;

@Repository("blProductOptionDao")
public class ProductOptionDaoImpl implements ProductOptionDao {
    
    @PersistenceContext(unitName="blPU")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    public ProductOption readProductOptionById(Long id) {
        return (ProductOption) em.find(entityConfiguration.lookupEntityClass("org.broadleafcommerce.core.catalog.domain.ProductOption"), id);
    }

}
