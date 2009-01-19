package com.springcommerce.demo.user.dao;

import com.springcommerce.demo.framework.dao.CatalogDaoJpa;
import com.springcommerce.demo.framework.domain.AbstractCatalog;
import com.springcommerce.demo.user.domain.ProprietaryCatalog;

public class MyCatalogDaoJpa extends CatalogDaoJpa {

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.dao.CatalogDaoJpa#createNewCatalog()
	 */
	@Override
	public AbstractCatalog createNewCatalog() {
		ProprietaryCatalog catalog = new ProprietaryCatalog();
		catalog.setColor("green");
		catalog.setItemNumber(123);
		catalog.setStyle("normal");
		catalog.setPopularity("high");
		
		em.persist(catalog);
		
		return catalog;
	}

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.dao.CatalogDaoJpa#readCatalogById(java.lang.Long)
	 */
	@Override
	public AbstractCatalog readCatalogById(Long catalogId) {
		return em.find(ProprietaryCatalog.class, catalogId);
	}
	
}
