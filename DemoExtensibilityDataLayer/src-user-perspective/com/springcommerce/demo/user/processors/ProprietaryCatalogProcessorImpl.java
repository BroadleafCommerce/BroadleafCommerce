package com.springcommerce.demo.user.processors;

import com.springcommerce.demo.framework.domain.AbstractCatalog;
import com.springcommerce.demo.framework.processors.CatalogProcessor;
import com.springcommerce.demo.user.domain.ProprietaryCatalog;

public class ProprietaryCatalogProcessorImpl implements CatalogProcessor {

	/* (non-Javadoc)
	 * @see com.springcommerce.demo.framework.processors.CatalogProcessor#alterCatalog(com.springcommerce.demo.framework.domain.Catalog)
	 */
	public void alterCatalog(AbstractCatalog catalog) {
		ProprietaryCatalog myCatalog = (ProprietaryCatalog) catalog;
		if (myCatalog.getColor().equals("green") && myCatalog.getPopularity().equals("high")) {
			myCatalog.setStyle("stylish");
		}
	}

}
