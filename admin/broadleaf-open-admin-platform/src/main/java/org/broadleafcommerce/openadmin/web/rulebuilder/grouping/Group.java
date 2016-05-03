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
package org.broadleafcommerce.openadmin.web.rulebuilder.grouping;

import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jfischer
 * @author Elbert Bautista (elbertbautista)
 */
public class Group {

    private List<String> phrases = new ArrayList<String>();
    private List<Group> subGroups = new ArrayList<Group>();
    private BLCOperator operatorType;
    private Boolean isTopGroup = false;

    public List<String> getPhrases() {
        return phrases;
    }

    public BLCOperator getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(BLCOperator operatorType) {
        this.operatorType = operatorType;
    }

    public List<Group> getSubGroups() {
        return subGroups;
    }

    public Boolean getIsTopGroup() {
        return isTopGroup;
    }

    public void setIsTopGroup(Boolean isTopGroup) {
        this.isTopGroup = isTopGroup;
    }
}
