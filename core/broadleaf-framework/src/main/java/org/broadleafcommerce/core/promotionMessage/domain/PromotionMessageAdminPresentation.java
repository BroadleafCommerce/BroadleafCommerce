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
package org.broadleafcommerce.core.promotionMessage.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Chris Kittrell (ckittrell)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PromotionMessageImpl_basePromotionMessage",
    tabs = {
        @AdminTabPresentation(name = PromotionMessageAdminPresentation.TabName.General,
            order = PromotionMessageAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = PromotionMessageAdminPresentation.GroupName.General,
                    order = PromotionMessageAdminPresentation.GroupOrder.General,
                    untitled = true),
                @AdminGroupPresentation(name = PromotionMessageAdminPresentation.GroupName.Placement,
                    order = PromotionMessageAdminPresentation.GroupOrder.Placement,
                    untitled = true),
                @AdminGroupPresentation(name = PromotionMessageAdminPresentation.GroupName.ActiveRange,
                    order = PromotionMessageAdminPresentation.GroupOrder.ActiveRange,
                    tooltip = "PromotionMessageImpl_Offer_Active_Range_Tooltip",
                    column = 1)
            }
        ),
        @AdminTabPresentation(name = PromotionMessageAdminPresentation.TabName.Media,
            order = PromotionMessageAdminPresentation.TabOrder.Media,
            groups = {
                @AdminGroupPresentation(name = PromotionMessageAdminPresentation.GroupName.Media,
                    order = PromotionMessageAdminPresentation.GroupOrder.Media,
                    untitled = true)
            }
        )
    }
)
public interface PromotionMessageAdminPresentation {

    public static class TabName {
        public static final String General = "General";
        public static final String Media = "PromotionMessageImpl_Media";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int Media = 2000;
    }

    public static class GroupName {
        public static final String General = "General";
        public static final String Placement = "PromotionMessageImpl_Placement";
        public static final String ActiveRange = "PromotionMessageImpl_Active_Range";
        public static final String Media = "PromotionMessageImpl_Media";

    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int Placement = 2000;
        public static final int ActiveRange = 3000;
        public static final int Media = 4000;
    }

    public static class FieldOrder {
        public static final int Name = 1000;
        public static final int Message = 2000;
        public static final int Media = 3000;

        public static final int MessagePlacement = 1000;
        public static final int Locale = 2000;
        public static final int Priority = 3000;

        public static final int StartDate = 1000;
        public static final int EndDate = 2000;
    }

}
