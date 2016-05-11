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
