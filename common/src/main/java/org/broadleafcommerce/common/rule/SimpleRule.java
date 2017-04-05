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
package org.broadleafcommerce.common.rule;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Represents a class containing an MVEL rule
 *
 * @author Jeff Fischer
 */
public interface SimpleRule extends Serializable {

    /**
     * The rule in the form of an MVEL expression
     *
     * @return the rule as an MVEL string
     */
    public String getMatchRule();

    /**
     * Sets the match rule used to test this item.
     *
     * @param matchRule the rule as an MVEL string
     */
    public void setMatchRule(@Nonnull String matchRule);

}
