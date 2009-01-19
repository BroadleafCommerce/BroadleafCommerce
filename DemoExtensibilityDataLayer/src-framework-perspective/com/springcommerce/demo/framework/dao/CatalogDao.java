package com.springcommerce.demo.framework.dao;

import com.springcommerce.demo.framework.domain.AbstractCatalog;

public interface CatalogDao {

    public AbstractCatalog readCatalogById(Long catalogId);
  
    public void saveCatalog(AbstractCatalog catalog);
    
    public AbstractCatalog createNewCatalog();
    
}