package com.springcommerce.demo.framework.dao;

import com.springcommerce.demo.framework.domain.Catalog;

public interface CatalogDao {

    public Catalog readCatalogById(Long catalogId);
  
    public void saveCatalog(Catalog catalog);
    
}