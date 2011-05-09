/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.gwt.server;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.gwt.client.service.catalog.CatalogService;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

@Service("blCatalogRemoteService")
public class CatalogRemoteService implements CatalogService {

	@Resource(name = "blCatalogService")
	protected org.broadleafcommerce.catalog.service.CatalogService catalogService;

	public Product saveProduct(Product product) throws ApplicationSecurityException {
		return catalogService.saveProduct(product);
	}

	public Category saveCategory(Category category) throws ApplicationSecurityException {
		return catalogService.saveCategory(category);
	}

	public void removeCategory(Category category) throws ApplicationSecurityException {
		catalogService.removeCategory(category);
	}

	public Sku saveSku(Sku sku) throws ApplicationSecurityException {
		return catalogService.saveSku(sku);
	}

	public Category createCategory() throws ApplicationSecurityException {
		Category category = catalogService.createCategory();
		category.setUrlKey("/");
		return category;
	}

}
