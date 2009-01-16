package com.springcommerce.demo.framework.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.springcommerce.demo.framework.domain.Catalog;


@Repository("CatalogDao")
public class CatalogDaoJpa implements CatalogDao {

    @PersistenceContext
    private EntityManager em;

    public Catalog readCatalogById(Long catalogId) {
        return em.find(Catalog.class, catalogId);
    }
    
    public void saveCatalog(Catalog catalog) {
    	em.merge(catalog);
    }

}
