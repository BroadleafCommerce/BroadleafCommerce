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
package org.broadleafcommerce.test.dataprovider;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.catalog.domain.CrossSaleProductImpl;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.broadleafcommerce.catalog.domain.RelatedProduct;
import org.broadleafcommerce.catalog.domain.UpSaleProductImpl;
import org.testng.annotations.DataProvider;

public class ProductDataProvider {

    @DataProvider(name="basicProduct")
    public static Object[][] provideBasicProduct() {
        Product ci = new ProductImpl();
        ci.setName("setOfAggieDominoes");
        ci.setDescription("a fine set of bones for 42");

        return new Object[][]{{ci}};
    }

    @DataProvider(name="setupProducts")
    public static Object[][] createProducts() {
        Product p1 = getProduct(null);
        Product p2 = getProduct(null);
        Product p3 = getProduct(null);
        Product p4 = getProduct(null);
        Product p5 = getProduct(null);
        Product p6 = getProduct(null);
        Product p7 = getProduct(null);

        Object[][] objs = new Object[7][1];
        objs[0] = new Object[]{p1};
        objs[1] = new Object[]{p2};
        objs[2] = new Object[]{p3};
        objs[3] = new Object[]{p4};
        objs[4] = new Object[]{p5};
        objs[5] = new Object[]{p6};
        objs[6] = new Object[]{p7};

        return objs;
    }

    @DataProvider(name="basicCrossSaleValue")
    public static Object[][] provideBasicCrossSale() {
        Product p1 = getProduct(1L);
        Product p2 = getProduct(2L);

        Product rel1 = getProduct(3L);
        Product rel2 = getProduct(4L);
        Product rel3 = getProduct(5L);
        Product rel4 = getProduct(6L);
        Product rel5 = getProduct(7L);

        //p1 associated RelatedProducts
        List<RelatedProduct> p1UpSales = new ArrayList<RelatedProduct>();
        getRelatedCrossProduct(p1, rel1, p1UpSales);
        getRelatedCrossProduct(p1, rel2, p1UpSales);
        getRelatedCrossProduct(p1, rel3, p1UpSales);
        p1.setCrossSaleProducts(p1UpSales);

        //p2 associated RelatedProducts
        List<RelatedProduct> p2UpSales = new ArrayList<RelatedProduct>();
        getRelatedCrossProduct(p2, rel4, p2UpSales);
        getRelatedCrossProduct(p2, rel5, p2UpSales);
        p2.setCrossSaleProducts(p2UpSales);

        Object[][] objs = new Object[2][1];
        objs[0] = new Object[]{p1};
        objs[1] = new Object[]{p2};

        return objs;
    }

    @DataProvider(name="basicUpSaleValue")
    public static Object[][] provideBasicUpSale() {
        Product p1 = getProduct(1L);
        Product p2 = getProduct(2L);

        Product rel1 = getProduct(3L);
        Product rel2 = getProduct(4L);
        Product rel3 = getProduct(5L);
        Product rel4 = getProduct(6L);
        Product rel5 = getProduct(7L);

        //p1 associated RelatedProducts
        List<RelatedProduct> p1UpSales = new ArrayList<RelatedProduct>();
        getRelatedUpSaleProduct(p1, rel1, p1UpSales);
        getRelatedUpSaleProduct(p1, rel2, p1UpSales);
        getRelatedUpSaleProduct(p1, rel3, p1UpSales);
        p1.setUpSaleProducts(p1UpSales);

        //p2 associated RelatedProducts
        List<RelatedProduct> p2UpSales = new ArrayList<RelatedProduct>();
        getRelatedUpSaleProduct(p2, rel4, p2UpSales);
        getRelatedUpSaleProduct(p2, rel5, p2UpSales);
        p2.setUpSaleProducts(p2UpSales);

        Object[][] objs = new Object[2][1];
        objs[0] = new Object[]{p1};
        objs[1] = new Object[]{p2};

        return objs;
    }

    private static RelatedProduct getRelatedUpSaleProduct(Product prod, Product prodToRelate, List<RelatedProduct> upSales){
        RelatedProduct rp1 = new UpSaleProductImpl();
        rp1.setProduct(prod);
        rp1.setPromotionMessage("brand new coffee");
        rp1.setRelatedProduct(prodToRelate);

        upSales.add(rp1);
        return rp1;
    }

    private static RelatedProduct getRelatedCrossProduct(Product prod, Product prodToRelate, List<RelatedProduct> upSales){
        RelatedProduct rp1 = new CrossSaleProductImpl();
        rp1.setProduct(prod);
        rp1.setPromotionMessage("brand new coffee");
        rp1.setRelatedProduct(prodToRelate);

        upSales.add(rp1);
        return rp1;
    }

    private static Product getProduct(Long id){
        if(id == null){
            Product product = new ProductImpl();
            product.setName("productNameTest");
            return product;
        }
        Product p = new ProductImpl();
        p.setId(id);
        p.setName(id.toString());
        return p;
    }
}
