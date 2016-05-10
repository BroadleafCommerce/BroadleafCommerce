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
package org.broadleafcommerce.common.structure.dto;

import java.io.Serializable;

/**
 * StructuredContent data is converted into a DTO since it requires
 * pre-processing.   The data is fairly static so the desire is
 * to cache the value after it has been processed.
 *
 * This DTO represents a compact version of StructuredContentItemCriteria
 *
 * Created by bpolster.
 */
public class ItemCriteriaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer qty;
    protected String matchRule;

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getMatchRule() {
        return matchRule;
    }

    public void setMatchRule(String matchRule) {
        this.matchRule = matchRule;
    }

    public ItemCriteriaDTO getClone() {
        ItemCriteriaDTO clonedDto = new ItemCriteriaDTO();
        clonedDto.setQty(qty);
        clonedDto.setMatchRule(matchRule);
        return clonedDto;
    }
}
