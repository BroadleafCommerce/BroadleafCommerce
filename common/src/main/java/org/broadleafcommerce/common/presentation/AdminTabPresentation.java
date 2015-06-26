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
