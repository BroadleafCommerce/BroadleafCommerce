/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.spec.catalog.domain

import org.broadleafcommerce.core.catalog.domain.Product
import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.domain.ProductOption
import org.broadleafcommerce.core.catalog.domain.ProductOptionImpl
import org.broadleafcommerce.core.catalog.domain.ProductOptionXref
import org.broadleafcommerce.core.catalog.domain.ProductOptionXrefImpl

import spock.lang.Specification


class ProductSpec extends Specification {
    
    Product product;
    def setup() {
        product = new ProductImpl();
    }
    
    def "Test if Product Options are returned sorted by displayOrder"(){
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>();
        setup:
        ProductOption testPo = new ProductOptionImpl();
        testPo.setDisplayOrder(2);
        ProductOption testPo2 = new ProductOptionImpl();
        testPo2.setDisplayOrder(0);
        ProductOptionXref testPox = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo
            id = 1
            it
        }
        ProductOptionXref testPox2 = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo2
            id = 2
            it
        }
        testProductOptions = Arrays.asList(testPox,testPox2);
        product.setProductOptionXrefs(testProductOptions);
        
        when:
        List<ProductOptionXref> output = product.getProductOptionXrefs();
        
        then:
        output.get(0).getProductOption() == testPo2
        output.get(0) == testPox2
        output.get(1).getProductOption() == testPo
        output.get(1) == testPox
    }
    
    def "Test if Product Options are returned sorted by displayOrder when already in order"(){
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>();
        setup:
        ProductOption testPo = new ProductOptionImpl();
        testPo.setDisplayOrder(0);
        ProductOption testPo2 = new ProductOptionImpl();
        testPo2.setDisplayOrder(1);
        ProductOption testPo3 = new ProductOptionImpl();
        testPo3.setDisplayOrder(2);
        ProductOptionXref testPox = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo
            id = 1
            it
        }
        ProductOptionXref testPox2 = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo2
            id = 2
            it
        }
        ProductOptionXref testPox3 = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo3
            id = 3
            it
        }
        testProductOptions = Arrays.asList(testPox,testPox2,testPox3);
        product.setProductOptionXrefs(testProductOptions);
        
        when:
        List<ProductOptionXref> output = product.getProductOptionXrefs();
        
        then:
        output.get(0) == testPox
        output.get(0).getProductOption() == testPo
        output.get(1) == testPox2
        output.get(1).getProductOption() == testPo2
        output.get(2) == testPox3
        output.get(2).getProductOption() == testPo3
    }
    
    def "Test if Product Options are returned sorted by displayOrder when one has no displayOrder"(){
        ArrayList<ProductOptionXref> testProductOptions = new ArrayList<ProductOptionXref>();
        setup:
        ProductOption testPo = new ProductOptionImpl();
        ProductOption testPo2 = new ProductOptionImpl();
        testPo2.setDisplayOrder(2);
        ProductOption testPo3 = new ProductOptionImpl();
        testPo3.setDisplayOrder(1);
        ProductOptionXref testPox = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo
            id = 1
            it
        }
        ProductOptionXref testPox2 = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo2
            id = 2
            it
        }
        ProductOptionXref testPox3 = new ProductOptionXrefImpl().with{
            product = product
            productOption = testPo3
            id = 3
            it
        }
        testProductOptions = Arrays.asList(testPox,testPox2,testPox3);
        product.setProductOptionXrefs(testProductOptions);
        
        when:
        List<ProductOptionXref> output = product.getProductOptionXrefs();
        
        then:
        output.get(0) == testPox3
        output.get(0).getProductOption() == testPo3
        output.get(1) == testPox2
        output.get(1).getProductOption() == testPo2
        output.get(2) == testPox
        output.get(2).getProductOption() == testPo
    }
}
