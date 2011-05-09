package org.broadleafcommerce.gwt.admin.client.datasource.catalog.category.module;

import org.broadleafcommerce.gwt.client.datasource.dynamic.module.JoinStructureModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Record;

public class CategoryTreeJoinStructureModule extends JoinStructureModule {

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param persistencePerspective
	 * @param service
	 */
	public CategoryTreeJoinStructureModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
	}

	@Override
	public Record buildRecord(Entity entity, Boolean updateId) {
		return super.buildRecord(entity, true);
	}

}
