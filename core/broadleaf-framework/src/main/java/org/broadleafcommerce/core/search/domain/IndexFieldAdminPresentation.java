/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;

@AdminPresentationClass(friendlyName = "IndexFieldImpl_friendly",
    tabs = {
        @AdminTabPresentation(
            groups = {
                @AdminGroupPresentation(name = IndexFieldAdminPresentation.GroupName.General,
                    order = IndexFieldAdminPresentation.GroupOrder.General,
                    untitled = true)
            }
        )
    }
)
public interface IndexFieldAdminPresentation {

    public static class TabName {
    }

    public static class TabOrder {
    }

    public static class GroupName {
        public static final String General = "General";
    }

    public static class GroupOrder {
        public static final int General = 1000;
    }

    public static class FieldOrder {
        public static final int ENTITY_TYPE = 1000;
        public static final int FRIENDLY_NAME = 2000;
        public static final int PROPERTY_NAME = 3000;
        public static final int OVERRIDE_GENERATED_PROPERTY_NAME = 4000;
        public static final int ABBREVIATION = 5000;
        public static final int TRANSLATABLE = 6000;
    }

}
