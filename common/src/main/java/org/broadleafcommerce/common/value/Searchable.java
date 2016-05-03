/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.value;

import java.io.Serializable;

/**
 * Describes a class that contains searchable information. Can be used by the framework search engine to create
 * search indexes and indicate that information in this class should be searched for search terms during actual
 * searches.
 *
 * @author Jeff Fischer
 */
public interface Searchable<T extends Serializable> extends ValueAssignable<T> {

    /**
     * Whether or not this class contains searchable information
     *
     * @return Whether or not this class contains searchable information
     */
    Boolean getSearchable();

    /**
     * Whether or not this class contains searchable information
     *
     * @param searchable Whether or not this class contains searchable information
     */
    void setSearchable(Boolean searchable);

}
