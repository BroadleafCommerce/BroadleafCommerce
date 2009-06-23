/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.test.integration;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.catalog.dao.ProductDao;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.order.dao.OrderDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.dao.CustomerAddressDao;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerAddress;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.ProductDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class ProductDaoTest extends BaseTest {

    @Resource
    private ProductDao productDao;

    @Resource
    private CustomerService customerService;

    @Resource
    private CustomerAddressDao customerAddressDao;

    @Resource
    private OrderDao orderDao;

    @Test(groups={"createProduct"},dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    public void testMaintainProduct(Product product) {
        assert product.getId() == null;
        product = productDao.save(product);
        assert product.getId() != null;
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsById(Product product) {
        product = productDao.save(product);
        Product result = productDao.readProductById(product.getId());
        assert product.equals(result);
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testReadProductsByName(Product product) {
        String name = product.getName();
        product = productDao.save(product);
        List<Product> result = productDao.readProductsByName(name);
        assert result.contains(product);
    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testProductAttributes(Product product) {
        product = productDao.save(product);
        product.setWidth(new BigDecimal(25.5));
        product.setHeight(new BigDecimal(50D));
        product.setDepth(new BigDecimal(75.5D));
        product.setWeight(new BigDecimal(100.1));
        Product result = productDao.readProductById(product.getId());
        assert result.getWidth().doubleValue() == 25.5D;
        assert result.getHeight().doubleValue() == 50D;
        assert result.getDepth().doubleValue() == 75.5D;
        assert result.getWeight().doubleValue() == 100.1D;
        assert result.getDimensionString().equals("50Hx25.5Wx75.5D\"");
    }
    /*
    @Test(groups="readUpSaleValues", dataProvider="basicUpSaleValue", dataProviderClass=ProductDataProvider.class, dependsOnGroups="createUpSaleValues")
    public void testReadProductsWithUpSaleValues(Product product) {
        Product result = productDao.readProductById(product.getId());

        List<RelatedProduct> related = result.getUpSaleProducts();

        assert(related != null);
        assert(!related.isEmpty());
        assert(related.size() == 2 || related.size() == 3);

        for(RelatedProduct rp : related){
            assert(rp instanceof UpSaleProductImpl);
        }
    }

    @Test(groups="createUpSaleValues", dataProvider="basicUpSaleValue", dataProviderClass=ProductDataProvider.class, dependsOnGroups="createProducts")
    @Rollback(false)
    public void createUpSaleValues(Product product){
        System.out.println("#######product.getUpSaleProducts().size() - " + product.getUpSaleProducts().size());

        productDao.save(product);
        assert(product.getId() != null);
    }

    @Test(groups="createProducts", dataProvider="setupProducts", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    public void createProducts(Product product){
        productDao.save(product);
        assert(product.getId() != null);
    }
     */
    @Test(groups="myAssertTrue")
    public void myAssertTrue(){
        assert(true);
    }
    /*

    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class, dependsOnGroups="createCrossSaleValues")
    public void testReadProductsWithCrossSaleValues(Product product) {
        //TODO
    }

    @Test(groups={"createCrossSaleValues"}, dataProvider="basicCrossSaleValue", dataProviderClass=ProductDataProvider.class)
    @Rollback(false)
    public void createCrossSaleValues(Product product){
        //        productDao.save(product);
    }*/


    public void retrieveAddressInfoForCustomer(){
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        System.out.println("@@@@@ - " + customer.getId());

        List<CustomerAddress> cAddress = customerAddressDao.readActiveCustomerAddressesByCustomerId(customer.getId());

        System.out.println("@@@@@ - " + cAddress.size());
        System.out.println("@@@@@ - " + cAddress.get(0));
        System.out.println("@@@@@ - " + cAddress.get(0).getAddressName());
        System.out.println("@@@@@ - " + cAddress.get(0).getId());
        System.out.println("@@@@@ - " + cAddress.get(0).getAddressName());

        Address address = cAddress.get(0).getAddress();
        Order salesOrder = orderDao.readCartForCustomer(customer);

        System.out.println("@@@@@ - " + address.getId());
        System.out.println("@@@@@ - " + salesOrder.getId());

    }

    @Test(dataProvider="basicProduct", dataProviderClass=ProductDataProvider.class)
    public void testFeaturedProduct(Product product) {
        product = productDao.save(product);
        Long productId = product.getId();
        product.setFeaturedProduct(true);
        productDao.save(product);
        Product testProduct = productDao.readProductById(productId);
        assert (testProduct.getIsFeaturedProduct() == true);
    }

}
