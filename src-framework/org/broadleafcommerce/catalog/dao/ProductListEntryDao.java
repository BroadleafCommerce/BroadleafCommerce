package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.ProductListEntry;

public interface ProductListEntryDao {

    public ProductListEntry readProductListAssociationById(Long id);

    public ProductListEntry maintainProductListAssociation(ProductListEntry productListEntry);
}