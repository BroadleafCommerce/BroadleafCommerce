package org.broadleafcommerce.admin.catalog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.media.domain.Media;
import org.broadleafcommerce.media.domain.MediaImpl;
import org.springframework.stereotype.Service;

import flex.messaging.io.amf.ASObject;

@Service("blAdminCatalogService")
public class AdminCatalogService {
    
    @Resource(name = "blCatalogService")
    CatalogService catalogService;
    
    public Product findProductById(Long productId) {
        return catalogService.findProductById(productId);
    }

    public List<Product> findProductsByName(String searchName) {
        return catalogService.findProductsByName(searchName);
    }

    public List<Product> findActiveProductsByCategory(Category category) {
        return catalogService.findActiveProductsByCategory(category);
    }

    public Product saveProduct(Product product) {
        ASObject asObjectProductImages = (ASObject)product.getProductImages();
        Map<String, String> productImages = new HashMap<String, String>();        
        for(Object key : asObjectProductImages.keySet()) {
            if(String.class.equals(key.getClass())) {                
                Object test = asObjectProductImages.get(key);
                if(String.class.equals(test.getClass())) {
                    productImages.put((String)key, (String)asObjectProductImages.get(key));                
                }
            }
        }
        product.setProductImages(productImages);
        return catalogService.saveProduct(product);
    }
    
    public Category findCategoryById(Long categoryId) {
        return catalogService.findCategoryById(categoryId);
    }

    public Category findCategoryByName(String categoryName) {
        return catalogService.findCategoryByName(categoryName);
    }

    public Category saveCategory(Category category) {
        if(category.getCategoryImages() != null && category.getCategoryImages() instanceof ASObject) {
            ASObject asObjectCategoryImages = (ASObject)category.getCategoryImages();
            Map<String, String> categoryImages = new HashMap<String, String>();        
            for(Object key : asObjectCategoryImages.keySet()) {
                if(String.class.equals(key.getClass())) {                
                    Object test = asObjectCategoryImages.get(key);
                    if(String.class.equals(test.getClass())) {
                        categoryImages.put((String)key, (String)asObjectCategoryImages.get(key));                
                    }
                }
            }
            category.setCategoryImages(categoryImages);
        }

        if(category.getCategoryMedia() != null && category.getCategoryMedia() instanceof ASObject) {            
            ASObject asObjectCategoryMedia = (ASObject) category.getCategoryMedia();
            Map<String, Media> categoryMedia = new HashMap<String, Media>();
            for(Object key:asObjectCategoryMedia.keySet()) {
                if(String.class.equals(key.getClass())) {
                    Object test = asObjectCategoryMedia.get(key);
                    if(test instanceof MediaImpl) {
                    	String keyString = (String)key;
                        categoryMedia.put(keyString, (MediaImpl)asObjectCategoryMedia.get(key));
                    }
                }
            }
//            Category testCat = catalogService.findCategoryById(category.getId());
//            testCat.setCategoryMedia(categoryMedia);
//            return catalogService.saveCategory(testCat);
            
            category.setCategoryMedia(categoryMedia);
        }
//        else {
//            Map<String, Media> newMap = new HashMap<String, Media>();
//            category.setCategoryMedia(newMap);
//            Category testCat = catalogService.findCategoryById(category.getId());
//            testCat.setCategoryMedia(newMap);
//            return catalogService.saveCategory(testCat);
//        }

        
        return catalogService.saveCategory(category);
    }

    public List<Category> findAllCategories() {
        return catalogService.findAllCategories();
    }

    public List<Product> findAllProducts() {
        return catalogService.findAllProducts();
    }

    public List<Sku> findAllSkus() {
        return catalogService.findAllSkus();
    }

    public Sku findSkuById(Long skuId) {
        return catalogService.findSkuById(skuId);
    }

    public Sku saveSku(Sku sku) {
        return catalogService.saveSku(sku);
    }

    public List<Sku> findSkusByIds(List<Long> ids) {
        return catalogService.findSkusByIds(ids);
    }

    
}
