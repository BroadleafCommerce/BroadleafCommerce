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
package org.broadleafcommerce.core.spec.catalog.domain.service;

import org.broadleafcommerce.core.catalog.domain.CategoryImpl
import org.broadleafcommerce.core.catalog.domain.ProductImpl
import org.broadleafcommerce.core.catalog.service.CatalogURLServiceImpl

import spock.lang.Specification

class CatalogURLServiceSpec extends Specification {

    CatalogURLServiceImpl catalogService = new CatalogURLServiceImpl();

    def "Test relative category url "() {
        setup:
        catalogService.appendIdToRelativeURI=true
        catalogService.useUrlKey=false
        catalogService.categoryIdParam="categoryId"

        when:
        CategoryImpl subCategory = new CategoryImpl().with() {
            url = "/test/subcategory"
            id = 10
            it
        }
        String relativeUrl = catalogService.buildRelativeCategoryURL("/category",  subCategory);

        then:
        relativeUrl == "/category/subcategory?categoryId=10";
    }

    def "Test relative product url "() {
        setup:
        catalogService.appendIdToRelativeURI=true
        catalogService.useUrlKey=false
        catalogService.productIdParam="productId"

        when:
        ProductImpl testProduct = new ProductImpl().with() {
            url = "/test/product-url"
            id = 8
            it
        }
        String relativeUrl = catalogService.buildRelativeProductURL("/category",  testProduct);

        then:
        relativeUrl == "/category/product-url?productId=8";
    }

    def "Test relative URLs without appending id"() {
        setup:
        catalogService.appendIdToRelativeURI=false;

        expect:
        catalogService.buildRelativeUrlWithParam(url, "test", "categoryId", "1") == result;

        where:
        url << [
            "",
            "/",
            "/parentdir",
            "/parentdir?x=5",
            "/parentdir/subdir",
            "/parentdir/subdir?x=5"
        ]

        result << [
            "/test",
            "/test",
            "/parentdir/test",
            "/parentdir/test?x=5",
            "/parentdir/subdir/test",
            "/parentdir/subdir/test?x=5"
        ]
    }

    def "Test relative URLs with appending id"() {
        setup:
        catalogService.appendIdToRelativeURI=true;

        expect:
        catalogService.buildRelativeUrlWithParam(url, "test", "categoryId", "1") == result;

        where:
        url << [
            "",
            "/",
            "/parentdir",
            "/parentdir?x=5",
            "/parentdir/subdir",
            "/parentdir/subdir?x=5"
        ]

        result << [
            "/test?categoryId=1",
            "/test?categoryId=1",
            "/parentdir/test?categoryId=1",
            "/parentdir/test?x=5&categoryId=1",
            "/parentdir/subdir/test?categoryId=1",
            "/parentdir/subdir/test?x=5&categoryId=1"
        ]
    }

    def "Test computing the last fragment for a provided URL"() {

        expect:
        catalogService.getLastFragment(value) == fragment

        where:
        value << [
            "",
            "/",
            "/test",
            "/test?x=5",
            "/test#loc",
            "/test/subdir",
            "/test/subdir?x=5",
            "/test/subdir/subdir2/subdir3",
        ]

        fragment << [
            "",
            "",
            "test",
            "test",
            "test",
            "subdir",
            "subdir",
            "subdir3"
        ]
    }

    def "Test that fragment is category key when property is true "() {

        setup:
        catalogService.useUrlKey=true;
        CategoryImpl category = new CategoryImpl().with() {
            urlKey = "urlkey"
            url = "/test/categoryurl"
            it
        }

        expect:
        catalogService.getCategoryUrlFragment(category) == "urlkey";
    }

    def "Test that fragment is from category url when property is false "() {

        setup:
        catalogService.useUrlKey=false;
        CategoryImpl category = new CategoryImpl().with() {
            urlKey = "urlkey"
            url = "/test/categoryurl"
            it
        }

        expect:
        catalogService.getCategoryUrlFragment(category) == "categoryurl";
    }

    def "Test that fragment is product key when property is true "() {

        setup:
        catalogService.useUrlKey=true;
        ProductImpl product = new ProductImpl().with() {
            urlKey = "urlkey"
            url = "/test/producturl"
            it
        }

        expect:
        catalogService.getProductUrlFragment(product) == "urlkey";
    }

    def "Test that fragment is from product url when property is false "() {

        setup:
        catalogService.useUrlKey=false;
        ProductImpl product = new ProductImpl().with() {
            urlKey = "urlkey"
            url = "/test/producturl"
            it
        }

        expect:
        catalogService.getProductUrlFragment(product) == "producturl";
    }
}
