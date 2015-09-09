/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "URLHandlerImpl_friendyName",
    tabs = {
        @AdminTabPresentation(name = URLHandlerAdminPresentation.TabName.General,
            order = URLHandlerAdminPresentation.TabOrder.General,
                groups = {
                        @AdminGroupPresentation(name = URLHandlerAdminPresentation.GroupName.General,
                                order = URLHandlerAdminPresentation.GroupOrder.General,
                                borderless = true),
                        @AdminGroupPresentation(name = URLHandlerAdminPresentation.GroupName.Redirect,
                                order = URLHandlerAdminPresentation.GroupOrder.Redirect,
                                column = 1)
                }
        )
    }
)

public interface URLHandlerAdminPresentation {


    public static class TabName {
        public static final String General = "URLHandlerImpl_tab_General";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {
        public static final String General = "URLHandlerImpl_group_General";
        public static final String Redirect = "URLHandlerImpl_group_Redirect";
    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int Redirect = 2000;
    }
}
