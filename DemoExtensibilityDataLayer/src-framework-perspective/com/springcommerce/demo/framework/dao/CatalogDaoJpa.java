package com.springcommerce.demo.framework.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.springcommerce.demo.framework.domain.AbstractCatalog;
import com.springcommerce.demo.framework.domain.Catalog;

public class CatalogDaoJpa implements CatalogDao {

    @PersistenceContext
    protected EntityManager em;

	public AbstractCatalog readCatalogById(Long catalogId) {
        return em.find(Catalog.class, catalogId);
    }
    
    public final void saveCatalog(AbstractCatalog catalog) {
    	em.merge(catalog);
    }
    
    public AbstractCatalog createNewCatalog() {
    	Catalog catalog = new Catalog();
    	catalog.setColor("green");
    	catalog.setItemNumber(123);
    	catalog.setStyle("normal");
    	
    	em.persist(catalog);
    	
    	return catalog;
    }

}
