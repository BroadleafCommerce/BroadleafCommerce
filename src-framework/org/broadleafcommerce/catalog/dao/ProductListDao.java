package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.ProductList;

public interface ProductListDao {

    public ProductList readProductListById(Long productListId);

    public ProductList maintainProductList(ProductList productList);
}