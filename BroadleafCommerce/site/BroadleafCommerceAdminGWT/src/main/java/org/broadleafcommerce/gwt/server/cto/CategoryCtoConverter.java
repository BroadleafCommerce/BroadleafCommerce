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
package org.broadleafcommerce.gwt.server.cto;

import org.broadleafcommerce.gwt.client.datasource.CategoryDataSource;
import org.broadleafcommerce.profile.domain.Customer;

import com.anasoft.os.daofusion.criteria.AssociationPath;

/**
 * CTO converter for the {@link Customer} entity.
 */
public class CategoryCtoConverter extends BaseCtoConverter {
	
    public static final String GROUP_NAME = "category";
    
    public CategoryCtoConverter() {
        // direct properties
        addStringLikeMapping(GROUP_NAME, CategoryDataSource._NAME,
                AssociationPath.ROOT, CategoryDataSource._NAME);
        addStringLikeMapping(GROUP_NAME, CategoryDataSource._URL,
                AssociationPath.ROOT, CategoryDataSource._URL);
        addStringLikeMapping(GROUP_NAME, CategoryDataSource._URL_KEY,
                AssociationPath.ROOT, CategoryDataSource._URL_KEY);
        addDateMapping(GROUP_NAME, CategoryDataSource._ACTIVE_START_DATE,
                AssociationPath.ROOT, CategoryDataSource._ACTIVE_START_DATE);
        addDateMapping(GROUP_NAME, CategoryDataSource._ACTIVE_END_DATE,
                AssociationPath.ROOT, CategoryDataSource._ACTIVE_END_DATE);
    }
    
}
