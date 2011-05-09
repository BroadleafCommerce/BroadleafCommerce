package org.broadleafcommerce.gwt.client.datasource.dynamic;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.widgets.grid.ListGrid;

public class PresentationLayerAssociatedDataSource extends DynamicEntityDataSource {

	protected ListGrid associatedGrid;
	
	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public PresentationLayerAssociatedDataSource(String ceilingEntityFullyQualifiedClassname, String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(ceilingEntityFullyQualifiedClassname, name, persistencePerspective, service, modules);
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
