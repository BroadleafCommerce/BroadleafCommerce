/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * Specify a GUI grouping for this field
     * Fields in the same group will be visually grouped together in the GUI
     * <br />
     * <br />
     * Note: for support I18N, this can also be a key to retrieve a localized String
     *
     * @return the group for this field
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
     *
     */
    int column() default 0;

    /**
     *
     */
    boolean borderless() default false;

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
