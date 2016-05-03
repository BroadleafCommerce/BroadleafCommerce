/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.domain;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author elbertbautista
 *
 */
public interface AdminSection extends Serializable {

    public Long getId();

    public String getName();

    public void setName(String name);

    public String getSectionKey();

    public void setSectionKey(String sectionKey);

    public String getUrl();

    public void setUrl(String url);

    public List<AdminPermission> getPermissions();

    public void setPermissions(List<AdminPermission> permissions);

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public void setDisplayController(String displayController);

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public String getDisplayController();

    public AdminModule getModule();

    public void setModule(AdminModule module);

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public Boolean getUseDefaultHandler();

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    public void setUseDefaultHandler(Boolean useDefaultHandler);

    public String getCeilingEntity();

    public void setCeilingEntity(String ceilingEntity);

    public Integer getDisplayOrder();

    public void setDisplayOrder(Integer displayOrder);
}
