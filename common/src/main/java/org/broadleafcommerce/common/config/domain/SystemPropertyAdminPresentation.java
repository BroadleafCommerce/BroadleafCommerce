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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;

@AdminPresentationClass(friendlyName = "SystemPropertyImpl",
    tabs = {
        @AdminTabPresentation(
            groups = {
                @AdminGroupPresentation(name = SystemPropertyAdminPresentation.GroupName.General,
                    order = SystemPropertyAdminPresentation.GroupOrder.General,
                    untitled = true),
                @AdminGroupPresentation(name = SystemPropertyAdminPresentation.GroupName.Placement,
                    order = SystemPropertyAdminPresentation.GroupOrder.Placement,
                    column = 1)
            }
        )
    }
)
public interface SystemPropertyAdminPresentation {

    public static class TabName {
    }

    public static class TabOrder {
    }

    public static class GroupName {
        public static final String General = "General";
        public static final String Placement = "SystemPropertyImpl_placement";
    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int Placement = 2000;
    }

    public static class FieldOrder {
        public static final int FRIENDLY_NAME = 1000;
        public static final int ATTRIBUTE_NAME = 2000;
        public static final int PROPERTY_TYPE = 3000;
        public static final int VALUE = 4000;

        public static final int GROUP_NAME = 1000;
        public static final int TAB_NAME = 2000;
    }

}
