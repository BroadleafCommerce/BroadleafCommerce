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
package org.broadleafcommerce.gwt.client.service.catalog;

import java.util.Date;
import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.gwt.security.SecurityUserRoles;
import org.springframework.security.access.annotation.Secured;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.gwtincubator.security.exception.ApplicationSecurityException;

@RemoteServiceRelativePath("catalog.service")
public interface CatalogService extends RemoteService {

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Product saveProduct(Product product) throws ApplicationSecurityException;    

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Product findProductById(Long productId) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public List<Product> findProductsByName(String searchName) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public List<Product> findActiveProductsByCategory(Category category, Date currentDate) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Category saveCategory(Category category) throws ApplicationSecurityException;
    
	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public void removeCategory(Category category) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Category findCategoryById(Long categoryId) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Category findCategoryByName(String categoryName) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public List<Product> findProductsForCategory(Category category) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Sku saveSku(Sku sku) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public List<Sku> findSkusByIds(List<Long> ids) throws ApplicationSecurityException;

	//@Secured({ SecurityUserRoles.MERCHANDISER })
    public Sku findSkuById(Long skuId) throws ApplicationSecurityException;

}
