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
public @interface AdminGroupPresentation {

    /**
     * Specify a GUI group name
     *
     * @return the group name
     */
    String name() default "General";

    /**
     * Optional - only required if you want to order the appearance of groups in the UI
     *
     * Specify an order for this group. Groups will be sorted in the resulting
     * form in ascending order based on this parameter.
     *
     * @return the order for this group
     */
    int order() default 99999;

    /**
     * Optional - only required if you want to place a group in a column other than the "main" column
     *
     * Specify which column that the group should be placed into.
     * By default, groups are placed in the "main" column (0). To place in the right-side ("sidebar") column,
     * set column to 1.
     *
     * @return the containing column of the group
     */
    int column() default 0;

    /**
     * Optional - only required if you want to remove the group's border
     *
     * This only applies to groups that are in the "main" column.
     *
     * @return whether or not the group is untitled
     */
    boolean untitled() default false;

    /**
     * Optional - only required if you want to provide help text for this group
     *
     * On the form for this entity, this will show a question
     * mark icon next to the group title. When the user hovers on the icon, whatever
     * HTML that is specified in this helpText is shown in a popup.
     *
     * For i18n support, this can also be a key to a localized version of the text
     *
     * Reference implementation: http://www.smartclient.com/smartgwt/showcase/#form_details_hints
     *
     */
    String tooltip() default "";

    /**
     * Optional - only required if you want to control the initial collapsed state of the group
     *
     * Specify whether a group is collapsed by default in the admin UI.
     *
     * @return whether or not the group is collapsed by default
     * @deprecated not supported
     */
    boolean collapsed() default false;
}
