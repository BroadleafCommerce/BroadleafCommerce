/*
 * #%L
 * BroadleafCommerce Export Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package com.broadleafcommerce.export.domain;

import java.util.Date;

public interface ExportInfo {

    Long getId();
    
    void setId(Long id);

    String getName();
    
    void setName(String name);
    
    String getEntityType();
    
    void setEntityType(String entityType);
    
    Date getDateCreated();
    
    void setDateCreated(Date dateCreated);

    Long getSize();
    
    void setSize(Long size);

    String getResourcePath();
    
    void setResourcePath(String resourcePath);
    
    String getFriendlyResourcePath();
    
    void setFriendlyResourcePath(String friendlyResourcePath);

    Long getAdminUserId();

    void setAdminUserId(Long adminUserId);

    boolean isShared();
    
    void setShared(boolean shared);

}
