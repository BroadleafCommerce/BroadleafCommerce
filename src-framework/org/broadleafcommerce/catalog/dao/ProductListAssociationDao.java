package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.ProductListAssociation;

public interface ProductListAssociationDao {

    public ProductListAssociation readProductListAssociationById(Long id);
    
    public ProductListAssociation maintainProductListAssociation(ProductListAssociation productListAssociation);

}