package org.broadleafcommerce.gwt.client.service.catalog;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.gwt.client.service.GridService;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("category.service")
public interface CategoryService extends GridService<Category>{

}
