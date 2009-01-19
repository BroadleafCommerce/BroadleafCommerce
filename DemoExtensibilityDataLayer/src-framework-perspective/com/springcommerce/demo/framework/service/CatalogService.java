package com.springcommerce.demo.framework.service;

import com.springcommerce.demo.framework.domain.AbstractCatalog;

public interface CatalogService {

	public AbstractCatalog readCatalogById(Long catalogId);
	
	public void updateCatalog(Long catalogId);
	
	public AbstractCatalog createNewCatalog();
}
