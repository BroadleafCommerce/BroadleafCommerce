/*
 * #%L
 * BroadleafCommerce Common Libraries
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
        public static final String General = "AbstractModuleConfiguration_General_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int History = 2000;
    }

    public static class GroupName {
        public static final String General = "AbstractModuleConfiguration_General";
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
