package org.broadleafcommerce.gwt.server.cto;

import org.broadleafcommerce.gwt.client.datasource.CategoryDataSource;

import com.anasoft.os.daofusion.criteria.AssociationPath;

public class NullParentCategoryCtoConverter extends CategoryCtoConverter {
	
	public NullParentCategoryCtoConverter() {
		super();
        addNullMapping(GROUP_NAME, CategoryDataSource._PARENT_ID,
        		AssociationPath.ROOT, "defaultParentCategory");
    }

}
