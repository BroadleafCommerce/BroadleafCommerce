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
package org.broadleafcommerce.gwt.client.datasource.dynamic;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.widgets.grid.ListGrid;

/**
 * 
 * @author jfischer
 *
 */
public class PresentationLayerAssociatedDataSource extends DynamicEntityDataSource {

	protected ListGrid associatedGrid;
	
	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public PresentationLayerAssociatedDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
	}

	public ListGrid getAssociatedGrid() {
		return associatedGrid;
	}

	public void setAssociatedGrid(ListGrid associatedGrid) {
		this.associatedGrid = associatedGrid;
	}

	public void loadAssociatedGridBasedOnRelationship(String relationshipValue, DSCallback dsCallback) {
		Criteria criteria = createRelationshipCriteria(relationshipValue);
		if (dsCallback != null) {
			getAssociatedGrid().fetchData(criteria, dsCallback);
		} else {
			getAssociatedGrid().fetchData(criteria);
		}
	}
}
