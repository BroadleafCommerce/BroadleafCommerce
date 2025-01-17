/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
 * @author elbertbautista
 */
public interface AdminSection extends Serializable {

    Long getId();

    String getName();

    void setName(String name);

    String getSectionKey();

    void setSectionKey(String sectionKey);

    String getUrl();

    void setUrl(String url);

    List<AdminPermission> getPermissions();

    void setPermissions(List<AdminPermission> permissions);

    /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
    @Deprecated
    void setDisplayController(String displayController);

    /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
    @Deprecated
    String getDisplayController();

    AdminModule getModule();

    void setModule(AdminModule module);

    /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
    @Deprecated
    Boolean getUseDefaultHandler();

    /**
     * No longer needed after GWT removal
     *
     * @param displayController
     */
    @Deprecated
    void setUseDefaultHandler(Boolean useDefaultHandler);

    String getCeilingEntity();

    void setCeilingEntity(String ceilingEntity);

    Integer getDisplayOrder();

    void setDisplayOrder(Integer displayOrder);

    boolean isFolderable();

    void setFolderable(boolean folderable);

    boolean isFolderedByDefault();

    void setFolderedByDefault(boolean folderedByDefault);
}
