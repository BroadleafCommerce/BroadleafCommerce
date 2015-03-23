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
}
