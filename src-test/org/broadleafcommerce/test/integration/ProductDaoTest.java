package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.ProductDaoJpa;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.test.dataprovider.ProductDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class ProductDaoTest extends BaseTest {
    
    @Resource
    private ProductDaoJpa productDao;

    @Test(groups={"createProduct"},dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    public void testMaintainProduct(Product product) {
        assert product.getId() == null;
        product = productDao.maintainProduct(product);
        assert product.getId() != null;
    }
    
    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsById(Product product) {
        product = productDao.maintainProduct(product);
        Product result = productDao.readProductById(product.getId());
        assert product.equals(result);
    }
    
    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsByName(Product product) {
        String name = product.getName();
        product = productDao.maintainProduct(product);
        List<Product> result = productDao.readProductsByName(name);
        assert result.contains(product);
    }
    
}
