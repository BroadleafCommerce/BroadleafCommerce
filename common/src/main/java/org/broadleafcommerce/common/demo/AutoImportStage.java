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
package org.broadleafcommerce.common.demo;

/**
 * @author Jeff Fischer
 */
public class AutoImportStage {

    public static final int PRIMARY_EARLY = -100000;
    public static final int PRIMARY_FRAMEWORK_SECURITY = 1000;
    public static final int PRIMARY_PRE_MODULE_SECURITY = 2000;
    public static final int PRIMARY_MODULE_SECURITY = 3000;
    public static final int PRIMARY_POST_MODULE_SECURITY = 4000;
    public static final int PRIMARY_PRE_BASIC_DATA = 5000;
    public static final int PRIMARY_BASIC_DATA = 6000;
    public static final int PRIMARY_POST_BASIC_DATA = 7000;
    public static final int ALL_TABLE_SEQUENCE = 8000;
    public static final int PRIMARY_LATE = 100000;

}
