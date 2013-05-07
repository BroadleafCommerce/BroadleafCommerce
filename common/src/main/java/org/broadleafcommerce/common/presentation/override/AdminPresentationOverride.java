/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.presentation.override;

import org.broadleafcommerce.common.presentation.AdminPresentation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author pverheyden
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AdminPresentationOverride {
    
    /**
     * The name of the property whose {@link AdminPresentation} annotation should be overwritten
     * 
     * @return the name of the property that should be overwritten
     */
    String name();
    
    /**
     * The {@link AdminPresentation} to overwrite the property with. This is a comprehensive override,
     * meaning whatever was declared on the target property previously will be completely replaced
     * with what is defined in this {@link AdminPresentation}.
     * 
     * @return the {@link AdminPresentation} being mapped to the attribute
     * @deprecated use the mergeValues() property instead
     */
    @Deprecated
    AdminPresentation value() default @AdminPresentation();

    /**
     * Specify one or more values to replace on the target property. This is a merged override,
     * meaning that whatever you declare in {@link AdminPresentationMerge} will override that particular
     * property value on the target field, but will leave the other properties intact. This is the
     * preferred approach, as it offers the same capabilities as value(), but with the additional flexibility of
     * only having to declare the specific values you want to override, rather than having to re-declare
     * an entire AdminPresentation instance with a bunch of values you don't care about.
     *
     * @return The {@link AdminPresentationMerge} instance that contains the targeted {@link AdminPresentation} override properties
     */
    AdminPresentationMerge mergeValue() default @AdminPresentationMerge();
}
