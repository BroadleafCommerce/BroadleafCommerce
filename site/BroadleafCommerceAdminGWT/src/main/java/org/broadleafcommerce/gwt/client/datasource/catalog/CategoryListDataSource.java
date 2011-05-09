package org.broadleafcommerce.gwt.client.datasource.catalog;

import org.broadleafcommerce.gwt.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.client.datasource.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

public class CategoryListDataSource extends ListGridDataSource {	

	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param name
	 * @param service
	 * @param foreignFields
	 * @param removeType
	 * @param additionalNonPersistentProperties
	 */
	public CategoryListDataSource(String ceilingEntityFullyQualifiedClassname, String name, RemoveType removeType, DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, new JoinTable("allParentCategories", "category", "id", "subCategory", EntityImplementations.CATEGORY_XREF), name, service, removeType, new String[]{});
	}

}
