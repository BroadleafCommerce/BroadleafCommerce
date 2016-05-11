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
package org.broadleafcommerce.openadmin.web.rulebuilder;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public enum BLCOperator {

    EQUALS, NOT_EQUAL, IEQUALS, INOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL, LESS_OR_EQUAL, CONTAINS,
    STARTS_WITH, ENDS_WITH, ICONTAINS, ISTARTS_WITH, IENDS_WITH, NOT_CONTAINS, NOT_STARTS_WITH, NOT_ENDS_WITH,
    INOT_CONTAINS, INOT_STARTS_WITH, INOT_ENDS_WITH, REGEXP, IREGEXP, IS_NULL, NOT_NULL, IN_SET, NOT_IN_SET,
    EQUALS_FIELD, NOT_EQUAL_FIELD, GREATER_THAN_FIELD, LESS_THAN_FIELD, GREATER_OR_EQUAL_FIELD, LESS_OR_EQUAL_FIELD,
    CONTAINS_FIELD, STARTS_WITH_FIELD, ENDS_WITH_FIELD, AND, NOT, OR, BETWEEN, BETWEEN_INCLUSIVE, COUNT_GREATER_THAN,
    COUNT_GREATER_OR_EQUAL, COUNT_LESS_THAN, COUNT_LESS_OR_EQUAL, COUNT_EQUALS, COLLECTION_IN, COLLECTION_NOT_IN

}
