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
package org.broadleafcommerce.common.sandbox.domain;

import org.broadleafcommerce.common.persistence.Status;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface SandBox extends Serializable, Status {

    Long getId();

    void setId(Long id);

    /**
     * The name of the sandbox.
     * Certain sandbox names are reserved in the system.    User created
     * sandboxes cannot start with "", "approve_", or "deploy_".
     *
     * @return String sandbox name
     */
    String getName();

    void setName(String name);

    SandBoxType getSandBoxType();

    void setSandBoxType(SandBoxType sandBoxType);

    Long getAuthor();

    void setAuthor(Long author);

    SandBox getParentSandBox();

    void setParentSandBox(SandBox parentSandBox);

    String getColor();

    void setColor(String color);

    Date getGoLiveDate();

    void setGoLiveDate(Date goLiveDate);

    List<Long> getSandBoxIdsForUpwardHierarchy(boolean includeInherited);

    List<Long> getSandBoxIdsForUpwardHierarchy(boolean includeInherited, boolean includeCurrent);

    List<SandBox> getChildSandBoxes();

    void setChildSandBoxes(List<SandBox> childSandBoxes);

    /**
     * @return whether or not this sandbox, or any of its parent sandboxes, has type DEFAULT.
     */
    public boolean getIsInDefaultHierarchy();

    public void setArchived(Character archived);

    public Character getArchived();

    public boolean isActive();

}


