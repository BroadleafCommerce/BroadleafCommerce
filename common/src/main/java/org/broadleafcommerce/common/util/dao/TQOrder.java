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
package org.broadleafcommerce.common.util.dao;

/**
 * Specify the attributes of a ORDER BY that should appear in the TypedQuery. Generally takes the form of:
 * </p>
 * <pre>
 * {@code
 * TypedQueryBuilder builder = new TypedQueryBuilder(com.MyClass, "item")
     .addOrder("i.name", true);
 * }
 * </pre>
 * </p>
 *
 * @author Jeff Fischer
 */
public class TQOrder {

    protected String expression;
    protected Boolean ascending = true;

    public TQOrder(String expression, Boolean ascending) {
        this.expression = expression;
        this.ascending = ascending;
    }

    public String toQl() {
        StringBuilder sb = new StringBuilder();
        sb.append(expression);
        sb.append(" ");
        sb.append(ascending != null && ascending?"ASC":"DESC");

        return sb.toString();
    }
}
