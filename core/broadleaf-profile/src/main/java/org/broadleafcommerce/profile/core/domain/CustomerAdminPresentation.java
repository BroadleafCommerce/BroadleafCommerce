/*
 * #%L
 * BroadleafCommerce Profile
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
                                        order = CustomerAdminPresentation.GroupOrder.Customer),
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.General,
                                        order = CustomerAdminPresentation.GroupOrder.General,
                                        column = 1),
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.PriceLists,
                                        order = CustomerAdminPresentation.GroupOrder.PriceLists,
                                        column = 1)
                        }
                ),
                @AdminTabPresentation(name = CustomerAdminPresentation.TabName.ContactInfo,
                        order = CustomerAdminPresentation.TabOrder.ContactInfo,
                        groups = {
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.CustomerAddresses,
                                        order = CustomerAdminPresentation.GroupOrder.CustomerAddresses),
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.PhoneNumbers,
                                        order = CustomerAdminPresentation.GroupOrder.PhoneNumbers),
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.SavedPaymentMethods,
                                        order = CustomerAdminPresentation.GroupOrder.SavedPaymentMethods)
                        }
                ),
                @AdminTabPresentation(name = CustomerAdminPresentation.TabName.Advanced,
                        order = CustomerAdminPresentation.TabOrder.Advanced,
                        groups = {
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.Attributes,
                                        order = CustomerAdminPresentation.GroupOrder.Attributes)
                        }
                ),
                @AdminTabPresentation(name = CustomerAdminPresentation.TabName.Accounts,
                        order = CustomerAdminPresentation.TabOrder.Accounts,
                        groups = {
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.Accounts,
                                        order = CustomerAdminPresentation.GroupOrder.Accounts)
                        }
                ),
                @AdminTabPresentation(name = CustomerAdminPresentation.TabName.History,
                        order = CustomerAdminPresentation.TabOrder.History,
                        groups = {
                                @AdminGroupPresentation(name = CustomerAdminPresentation.GroupName.Audits,
                                        order = CustomerAdminPresentation.GroupOrder.Audits)
                        }
                )
        }
)
public interface CustomerAdminPresentation {
    public static class TabName {
        public static final String General = "CustomerImpl_General_Tab";
        public static final String ContactInfo = "CustomerImpl_ContactInfo_Tab";
        public static final String Advanced = "CustomerImpl_Advanced_Tab";
        public static final String Accounts = "CustomerImpl_Accounts_Tab";
        public static final String History = "CustomerImpl_History_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int ContactInfo = 2000;
        public static final int Advanced = 3000;
        public static final int Accounts = 4000;
        public static final int History = 5000;
    }

    public static class GroupName {
        public static final String Customer = "CustomerImpl_Customer";
        public static final String General = "CustomerImpl_General";
        public static final String PriceLists = "CustomerImpl_PriceLists";
        public static final String CustomerAddresses = "CustomerImpl_CustomerAddresses";
        public static final String PhoneNumbers = "CustomerImpl_PhoneNumbers";
        public static final String SavedPaymentMethods = "CustomerImpl_SavedPaymentMethods";
        public static final String Attributes = "CustomerImpl_Attributes";
        public static final String Accounts = "CustomerImpl_Accounts";
        public static final String Audits = "CustomerImpl_Audits";
    }

    public static class GroupOrder {
        public static final int Customer = 1000;
        public static final int General = 2000;
        public static final int PriceLists = 3000;
        public static final int CustomerAddresses = 4000;
        public static final int PhoneNumbers = 5000;
        public static final int SavedPaymentMethods = 6000;
        public static final int Attributes = 7000;
        public static final int Accounts = 8000;
        public static final int Audits = 9000;
    }
}
