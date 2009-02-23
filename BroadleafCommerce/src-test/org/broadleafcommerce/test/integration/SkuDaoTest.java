package org.broadleafcommerce.test.integration;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.ProductDaoJpa;
import org.broadleafcommerce.catalog.dao.SkuDaoJpa;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.test.dataprovider.SkuDaoDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class SkuDaoTest extends BaseTest {

    private Long skuId;

    @Resource
    private SkuDaoJpa skuDao;

    @Resource
    private ProductDaoJpa productDao;

    @Test(groups = { "createSku" }, dataProvider = "basicSku", dataProviderClass = SkuDaoDataProvider.class, dependsOnGroups = { "readCustomer1", "createOrder", "createProduct" })
    @Rollback(false)
    public void createSku(Sku sku) {
        Product product = (productDao.readProductsByName("setOfAggieDominoes")).get(0);
        // sku.setProduct(product);
        sku.setSalePrice(BigDecimal.valueOf(10.0));
        assert sku.getId() == null;
        sku = skuDao.maintainSku(sku);
        assert sku.getId() != null;
        skuId = sku.getId();
    }

    @Test(groups = { "readFirstSku" }, dependsOnGroups = { "createSku" })
    public void readFirstSku() {
        Sku si = skuDao.readFirstSku();
        assert si != null;
        assert si.getId() != null;
    }

    @Test(groups = { "readSkuById" }, dependsOnGroups = { "createSku" })
    public void readSkuById() {
        Sku item = skuDao.readSkuById(skuId);
        assert item != null;
        assert item.getId() == skuId;
    }

}
