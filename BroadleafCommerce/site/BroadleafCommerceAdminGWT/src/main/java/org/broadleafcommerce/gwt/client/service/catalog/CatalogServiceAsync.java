package org.broadleafcommerce.gwt.client.service.catalog;

import java.util.Date;
import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CatalogServiceAsync {

	public void saveProduct(Product product, AsyncCallback<Product> callback);    

    public void findProductById(Long productId, AsyncCallback<Product> callback);

    public void findProductsByName(String searchName, AsyncCallback<List<Product>> callback);

    public void findActiveProductsByCategory(Category category, Date currentDate, AsyncCallback<List<Product>> callback);

    public void saveCategory(Category category, AsyncCallback<Category> callback);
    
    public void removeCategory(Category category, AsyncCallback<Category> callback);

    public void findCategoryById(Long categoryId, AsyncCallback<Category> callback);

    public void findCategoryByName(String categoryName, AsyncCallback<Category> callback);

    public void findProductsForCategory(Category category, AsyncCallback<List<Product>> callback);

    public void saveSku(Sku sku, AsyncCallback<Sku> callback);

    public void findSkusByIds(List<Long> ids, AsyncCallback<List<Sku>> callback);

    public void findSkuById(Long skuId, AsyncCallback<Sku> callback);
}
