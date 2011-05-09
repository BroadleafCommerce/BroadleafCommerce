package org.broadleafcommerce.gwt.client.service.catalog;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CatalogServiceAsync {

	public void saveProduct(Product product, AsyncCallback<Product> callback);    

    public void saveCategory(Category category, AsyncCallback<Category> callback);
    
    public void createCategory(AsyncCallback<Category> callback);  
    
    public void removeCategory(Category category, AsyncCallback<Category> callback);

    public void saveSku(Sku sku, AsyncCallback<Sku> callback);
    
}
