package org.broadleafcommerce.catalog.dao;

import org.broadleafcommerce.catalog.domain.ItemAttribute;

public interface ItemAttributeDao {

    public ItemAttribute readItemAttributeById(Long itemAttributeId);
    
    public ItemAttribute maintainItemAttribute(ItemAttribute itemAttribute);

}