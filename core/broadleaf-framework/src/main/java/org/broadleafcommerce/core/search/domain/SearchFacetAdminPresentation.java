/*
 * #%L
 * BroadleafCommerce Framework
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
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE,
    tabs = {
        @AdminTabPresentation(name = SearchFacetAdminPresentation.TabName.General,
            order = SearchFacetAdminPresentation.TabOrder.General,
                groups = {
                        @AdminGroupPresentation(name = SearchFacetAdminPresentation.GroupName.General,
                                order = SearchFacetAdminPresentation.GroupOrder.General,
                                untitled = true),
                        @AdminGroupPresentation(name = SearchFacetAdminPresentation.GroupName.Ranges,
                                order = SearchFacetAdminPresentation.GroupOrder.Ranges),
                        @AdminGroupPresentation(name = SearchFacetAdminPresentation.GroupName.Options,
                                order = SearchFacetAdminPresentation.GroupOrder.Options,
                                column = 1)
                }
        ),
            @AdminTabPresentation(name = SearchFacetAdminPresentation.TabName.Dependent,
            order = SearchFacetAdminPresentation.TabOrder.Dependent,
            groups = {
                    @AdminGroupPresentation(name = SearchFacetAdminPresentation.GroupName.Dependent,
                            order = SearchFacetAdminPresentation.GroupOrder.Dependent,
                            untitled = true)
            }
        )
    }
)

public interface SearchFacetAdminPresentation {

    public static class TabName {
        public static final String General = "General";
        public static final String Dependent = "SearchFacetImpl_Dependent_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int Dependent = 2000;
    }

    public static class GroupName {

        public static final String General = "General";
        public static final String Ranges = "SearchFacetImpl_ranges";
        public static final String Options = "SearchFacetImpl_options";
        public static final String Dependent = "SearchFacetImpl_dependent";
    }

    public static class GroupOrder {

        public static final int General = 1000;
        public static final int Ranges = 2000;
        public static final int Options = 3000;
        public static final int Dependent = 1000;

    }
}
