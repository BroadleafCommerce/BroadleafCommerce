package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.CatalogItemListAssociation;

public interface CatalogItemListAssociationDao {

    public CatalogItemListAssociation readCatalogItemListAssociationById(Long id);
    
    public CatalogItemListAssociation maintainCatalogItemListAssociation(CatalogItemListAssociation catalogItemListAssociation);

}