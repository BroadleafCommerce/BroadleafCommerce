package com.springcommerce.demo.framework.service;

import com.springcommerce.demo.framework.domain.Catalog;

public interface CatalogService {

	public Catalog readCatalogById(Long catalogId);
	
	public void updateCatalog(Long catalogId);
	
}
