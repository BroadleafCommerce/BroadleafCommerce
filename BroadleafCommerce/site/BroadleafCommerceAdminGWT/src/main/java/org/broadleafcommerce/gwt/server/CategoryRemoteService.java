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

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.gwt.client.datasource.ResultSet;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.client.service.catalog.CategoryService;
import org.broadleafcommerce.gwt.server.cto.CategoryCtoConverter;
import org.broadleafcommerce.gwt.server.cto.NullParentCategoryCtoConverter;
import org.broadleafcommerce.gwt.server.cto.RegularParentCategoryCtoConverter;
import org.broadleafcommerce.gwt.server.dao.CategoryDao;
import org.springframework.stereotype.Service;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectConverter;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectCountWrapper;

@Service("blCategoryRemoteService")
public class CategoryRemoteService implements CategoryService {

	private CriteriaTransferObjectConverter ctoNullConverter = new NullParentCategoryCtoConverter();
	private CriteriaTransferObjectConverter ctoRegularConverter = new RegularParentCategoryCtoConverter();
	
	@Resource(name="blAdminCategoryDao")
	protected CategoryDao categoryDao;
	
	@Resource(name = "blCatalogService")
	protected org.broadleafcommerce.catalog.service.CatalogService catalogService;
	
	public ResultSet<Category> fetch(CriteriaTransferObject cto) throws ServiceException {
		CriteriaTransferObjectConverter ctoConverter;
		if (cto.get("parentId").getFilterValues()[0] == null) {
			ctoConverter = ctoNullConverter;
		} else {
			ctoConverter = ctoRegularConverter;
		}
		PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, CategoryCtoConverter.GROUP_NAME);
        List<Category> cList = (List<Category>) categoryDao.query(queryCriteria);
        
        PersistentEntityCriteria countCriteria = 
        	ctoConverter.convert(new CriteriaTransferObjectCountWrapper(cto).wrap(), CategoryCtoConverter.GROUP_NAME);
        int totalRecords = categoryDao.count(countCriteria);
        
        return new ResultSet<Category>(cList, totalRecords);
	}

	public Category add(Category dto) throws ServiceException {
		Category temp = catalogService.findCategoryById(1L);
		return temp;
	}

	public Category update(Category dto) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(Category dto) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

}
