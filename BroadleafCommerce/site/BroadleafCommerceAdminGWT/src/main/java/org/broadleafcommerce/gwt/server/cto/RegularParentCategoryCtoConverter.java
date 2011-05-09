package org.broadleafcommerce.gwt.server.cto;

import org.broadleafcommerce.gwt.client.datasource.CategoryDataSource;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;

public class RegularParentCategoryCtoConverter extends CategoryCtoConverter {

	public RegularParentCategoryCtoConverter() {
		super();
		AssociationPath parentCategory = new AssociationPath(
                new AssociationPathElement("defaultParentCategory"));
		addLongMapping(GROUP_NAME, CategoryDataSource._PARENT_ID,
        		parentCategory, "id");
    }
}
