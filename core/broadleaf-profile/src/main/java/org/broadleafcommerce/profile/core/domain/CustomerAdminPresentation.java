/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * Created by brandon on 9/4/15.
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "CustomerImpl_baseCustomer",
    tabs = {
        @AdminTabPresentation(name = CustomerAdminPresentation.TabName.General,
            order = CustomerAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.Customer,
                    order = CustomerAdminPresentation.GroupOrder.Customer,
                    untitled = true),
                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.ContactInfo,
                    order = CustomerAdminPresentation.GroupOrder.ContactInfo),
                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.QualificationOptions,
                    order = CustomerAdminPresentation.GroupOrder.QualificationOptions,
                    column = 1, untitled = true),
                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.Audit,
                    order = CustomerAdminPresentation.GroupOrder.Audit,
                    column = 1, collapsed = true)
            }
        ),
        @AdminTabPresentation(name = CustomerAdminPresentation.TabName.PaymentMethods,
            order = CustomerAdminPresentation.TabOrder.PaymentMethods
        ),
        @AdminTabPresentation(name = CustomerAdminPresentation.TabName.Pricing,
            order = CustomerAdminPresentation.TabOrder.Pricing,
            groups = {
                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.Pricing,
                    order = CustomerAdminPresentation.GroupOrder.Pricing,
                    untitled = true)
            }
        )
    }
)
public interface CustomerAdminPresentation {
    public static class TabName {
        public static final String General = "General";
        public static final String PaymentMethods = "CustomerImpl_PaymentMethods_Tab";
        public static final String Pricing = "CustomerImpl_Pricing_Tab";
        public static final String Advanced = "CustomerImpl_Advanced_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int PaymentMethods = 2000;
        public static final int Pricing = 3000;
        public static final int Advanced = 5000;
    }

    public static class GroupName {
        public static final String Customer = "CustomerImpl_Customer";
        public static final String ContactInfo = "CustomerImpl_ContactInfo";
        public static final String QualificationOptions = "CustomerImpl_Qualification_Options";
        public static final String Pricing = "Pricing";
        public static final String Audit = "AdminAuditable_Audit";
    }

    public static class GroupOrder {
        public static final int Customer = 1000;
        public static final int ContactInfo = 2000;
        public static final int QualificationOptions = 1000;
        public static final int Pricing = 1000;
        public static final int Audit = 2000;
    }

    public static class FieldOrder {
        public static final int FIRST_NAME = 1000;
        public static final int LAST_NAME = 2000;
        public static final int EMAIL = 3000;
        public static final int USERNAME = 4000;

        public static final int ADDRESSES = 1000;
        public static final int PHONES = 2000;

        public static final int RECIEVE_EMAIL = 1000;
        public static final int REGISTERED = 2000;
        public static final int DEACTIVATED = 3000;

        public static final int IS_TAX_EXEMPT = 1000;
        public static final int TAX_EXEMPTION_CODE = 2000;
    }
}
