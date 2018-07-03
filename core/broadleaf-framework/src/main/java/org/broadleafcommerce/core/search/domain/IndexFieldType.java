/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.core.search.domain.solr.FieldType;

import java.io.Serializable;

/**
 * This interface is used for representing a {@link FieldType} for an {@link IndexField}
 *
 * @author Nick Crum (ncrum)
 */
public interface IndexFieldType extends Serializable, MultiTenantCloneable<IndexFieldType>  {

    Long getId();

    void setId(Long id);

    FieldType getFieldType();

    void setFieldType(FieldType fieldType);
    
    IndexField getIndexField();

    void setIndexField(IndexField indexField);

}
