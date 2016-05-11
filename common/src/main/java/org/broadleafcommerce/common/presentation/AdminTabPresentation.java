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
package org.broadleafcommerce.common.presentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 *
 * @author ckittrell
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AdminTabPresentation {

    /**
     * These AdminGroupPresentation items define each group that will be displayed within the tab
     * of the entity's EntityForm.
     *
     * @return the tabs for the entity's EntityForm
     */
    AdminGroupPresentation[] groups() default {};

    /**
     * Specify a GUI tab name
     *
     * @return the tab name
     */
    String name() default "General";

    /**
     * Optional - only required if you want to order the appearance of tabs in the UI
     *
     * Specify an order for this tab. Tabs will be sorted in the resulting form in
     * ascending order based on this parameter.
     *
     * The default tab will render with an order of 100.
     *
     * @return the order for this tab
     */
    int order() default 100;
}
