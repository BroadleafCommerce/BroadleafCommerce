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
package org.broadleafcommerce.common.config.domain;

/**
 * Created by Jon on 11/18/15.
 */

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;

@AdminPresentationClass(excludeFromPolymorphism = true, friendlyName = "AbstractModuleConfiguration",
        tabs = {
                @AdminTabPresentation(name = AbstractModuleConfigurationAdminPresentation.TabName.General,
                        order = AbstractModuleConfigurationAdminPresentation.TabOrder.General,
                        groups = {
                                @AdminGroupPresentation(name = AbstractModuleConfigurationAdminPresentation.GroupName.General,
                                        order = AbstractModuleConfigurationAdminPresentation.GroupOrder.General,
                                        untitled = true),
                                @AdminGroupPresentation(name = AbstractModuleConfigurationAdminPresentation.GroupName.ActiveDates,
                                        order = AbstractModuleConfigurationAdminPresentation.GroupOrder.ActiveDates,
                                        column = 1),
                                @AdminGroupPresentation(name = AbstractModuleConfigurationAdminPresentation.GroupName.Options,
                                        order = AbstractModuleConfigurationAdminPresentation.GroupOrder.Options,
                                        column = 1)
                        }
                )
        }
)
public interface AbstractModuleConfigurationAdminPresentation {

    public static class TabName {
        public static final String General = "General";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int History = 2000;
    }

    public static class GroupName {
        public static final String General = "General";
        public static final String ActiveDates = "AbstractModuleConfiguration_Active_Dates";
        public static final String Options = "AbstractModuleConfiguration_Options";
    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int ActiveDates = 2000;
        public static final int Options = 3000;
        public static final int RuleConfiguration = 4000;
        public static final int Advanced = 1000;
        public static final int CombineStack = 2000;
        public static final int QualifierRuleRestriction = 3000;
        public static final int TargetRuleRestriction = 4000;
        public static final int Codes = 1000;
    }
}
