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
package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Chris Kittrell (ckittrell)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OfferImpl_baseOffer",
    tabs = {
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.General,
            order = OfferAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Description,
                    order = OfferAdminPresentation.GroupOrder.Description,
                    untitled = true),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.RuleConfiguration,
                    order = OfferAdminPresentation.GroupOrder.RuleConfiguration,
                    untitled = true),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.ActivityRange,
                    order = OfferAdminPresentation.GroupOrder.ActivityRange,
                    column = 1),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Customer,
                    order = OfferAdminPresentation.GroupOrder.Customer,
                    column = 1),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.CombineStack,
                    order = OfferAdminPresentation.GroupOrder.CombineStack,
                    column = 1,
                    collapsed = true),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Restrictions,
                    order = OfferAdminPresentation.GroupOrder.Restrictions,
                    column = 1,
                    collapsed = true)
            }
        ),
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.Qualifiers,
            order = OfferAdminPresentation.TabOrder.Qualifiers,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.ShouldBeRelated,
                    order = OfferAdminPresentation.GroupOrder.ShouldBeRelated,
                    column = 1,
                    untitled = true),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.QualifierRuleRestriction,
                    order = OfferAdminPresentation.GroupOrder.QualifierRuleRestriction,
                    column = 1),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.TargetRuleRestriction,
                    order = OfferAdminPresentation.GroupOrder.TargetRuleRestriction,
                    column = 1)
            }
        ),
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.Marketing,
            order = OfferAdminPresentation.TabOrder.Marketing,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Marketing,
                    order = OfferAdminPresentation.GroupOrder.Marketing,
                    untitled = true)
            }
        ),
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.Codes,
            order = OfferAdminPresentation.TabOrder.Codes,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Codes,
                    order = OfferAdminPresentation.GroupOrder.Codes,
                    untitled = true)
            }
        ),
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.Advanced,
            order = OfferAdminPresentation.TabOrder.Advanced,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Advanced,
                    order = OfferAdminPresentation.GroupOrder.Advanced,
                    untitled = true)
            }
        )
    }
)
public interface OfferAdminPresentation {

    public static class TabName {
        public static final String General = "General";
        public static final String Marketing = "OfferImpl_Marketing_Tab";
        public static final String Qualifiers = "OfferImpl_Qualifiers_Tab";
        public static final String Codes = "OfferImpl_Codes_Tab";
        public static final String Advanced = "OfferImpl_Advanced_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int Qualifiers = 2000;
        public static final int Marketing = 3000;
        public static final int Codes = 4000;
        public static final int Advanced = 5000;
    }

    public static class GroupName {
        public static final String Description = "OfferImpl_Description";
        public static final String ActivityRange = "OfferImpl_Activity_Range";
        public static final String Restrictions = "OfferImpl_Restrictions";
        public static final String Customer = "OfferImpl_Customer";
        public static final String CombineStack = "OfferImpl_Combine_Stack";
        public static final String RuleConfiguration = "OfferImpl_Rule_Configuration";
        public static final String Marketing = "OfferImpl_Marketing";
        public static final String Advanced = "OfferImpl_Advanced";
        public static final String QualifierRuleRestriction = "OfferImpl_Qualifier_Rule_Restriction";
        public static final String TargetRuleRestriction = "OfferImpl_Target_Rule_Restriction";
        public static final String Codes = "OfferImpl_Codes_Tab";
        public static final String ShouldBeRelated = "OfferImpl_ShouldBeRelated";

    }

    public static class GroupOrder {
        public static final int Description = 1000;
        public static final int ActivityRange = 2000;
        public static final int Customer = 3000;
        public static final int Restrictions = 5000;
        public static final int RuleConfiguration = 5000;
        public static final int Marketing = 1000;
        public static final int Advanced = 1000;
        public static final int CombineStack = 4000;
        public static final int QualifierRuleRestriction = 2000;
        public static final int TargetRuleRestriction = 3000;
        public static final int Codes = 1000;
        public static final int ShouldBeRelated = 1000;
    }

    public static class FieldOrder {
        public static final int Name = 1000;
        public static final int Description = 2000;
        public static final int Message = 3000;
        public static final int TemplateType = 4000;
        public static final int Amount = 5000;
        public static final int OfferType = 5000;
        public static final int DiscountType = 5000;

        public static final int StartDate = 1000;
        public static final int EndDate = 2000;
        public static final int TimeRule = 3000;

        public static final int CustomerRule = 1000;
        public static final int AutomaticallyAdded = 2000;

        public static final int MaxUsesPerOrder = 1000;
        public static final int MaxUsesPerCustomer = 2000;
        public static final int OrderMinSubTotal = 3000;
        public static final int TargetMinSubTotal = 4000;

        public static final int Priority = 1000;
        public static final int Proration = 2000;

        public static final int CombinableWithOtherOffers = 1000;
        public static final int StackableWithOtherOffers = 2000;

        public static final int QualifyingItemSubTotal = 1000;
        public static final int QualifiersCanBeQualifiers = 2000;
        public static final int QualifiersCanBeTargets = 3000;

        public static final int OfferCodes = 1000;
    }

}
