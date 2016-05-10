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
package org.broadleafcommerce.core.search.redirect.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "SearchRedirectImpl_friendyName",
    tabs = {
        @AdminTabPresentation(name = SearchRedirectAdminPresentation.TabName.General,
            order = SearchRedirectAdminPresentation.TabOrder.General,
                groups = {
                        @AdminGroupPresentation(name = SearchRedirectAdminPresentation.GroupName.General,
                                order = SearchRedirectAdminPresentation.GroupOrder.General),
                        @AdminGroupPresentation(name = SearchRedirectAdminPresentation.GroupName.Dates,
                                order = SearchRedirectAdminPresentation.GroupOrder.Dates,
                                column = 1)
                }
        )
    }
)

public interface SearchRedirectAdminPresentation {


    public static class TabName {
        public static final String General = "General";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {

        public static final String General = "General";
        public static final String Dates = "SearchRedirectImpl_Group_Dates";
    }

    public static class GroupOrder {

        public static final int General = 1000;
        public static final int Dates = 2000;

    }
}
