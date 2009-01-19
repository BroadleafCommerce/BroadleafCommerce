package com.springcommerce.demo.framework.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springcommerce.demo.framework.dao.CatalogDao;
import com.springcommerce.demo.framework.domain.AbstractCatalog;
import com.springcommerce.demo.framework.processors.CatalogProcessor;

@Service("catalogService")
public class CatalogServiceImpl implements CatalogService {

	@Resource
    private CatalogDao catalogDao;
	@Resource(name="catalogProcessor")
    private CatalogProcessor processor;
	
	public AbstractCatalog readCatalogById(Long catalogId) {
		return catalogDao.readCatalogById(catalogId);
	}

	@Transactional
	public void updateCatalog(Long catalogId) {
		AbstractCatalog catalog = readCatalogById(catalogId);
		processor.alterCatalog(catalog);
		catalogDao.saveCatalog(catalog);
	}
	
	@Transactional
	public AbstractCatalog createNewCatalog() {
		return catalogDao.createNewCatalog();
	}
}
