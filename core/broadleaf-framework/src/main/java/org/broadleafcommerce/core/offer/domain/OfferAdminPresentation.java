/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
                    borderless = true),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Amount,
                    order = OfferAdminPresentation.GroupOrder.Amount),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.ActivityRange,
                    order = OfferAdminPresentation.GroupOrder.ActivityRange,
                    column = 1),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Qualifiers,
                    order = OfferAdminPresentation.GroupOrder.Qualifiers,
                    column = 1),
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.ItemTarget,
                    order = OfferAdminPresentation.GroupOrder.ItemTarget)
            }
        ),
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.Codes,
            order = OfferAdminPresentation.TabOrder.Codes,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Codes,
                    order = OfferAdminPresentation.GroupOrder.Codes, borderless = true)
            }
        ),
        @AdminTabPresentation(name = OfferAdminPresentation.TabName.Advanced,
            order = OfferAdminPresentation.TabOrder.Advanced,
            groups = {
                @AdminGroupPresentation(name = OfferAdminPresentation.GroupName.Advanced,
                    order = OfferAdminPresentation.GroupOrder.Advanced)
            }
        )
    }
)
public interface OfferAdminPresentation {

    public static class TabName {
        public static final String General = "OfferImpl_General_Tab";
        public static final String Codes = "OfferImpl_Codes_Tab";
        public static final String Advanced = "OfferImpl_Advanced_Tab";
    }

    public static class TabOrder {
        public static final int General = 1000;
        public static final int Codes = 2000;
        public static final int Advanced = 3000;
    }

    public static class GroupName {
        public static final String Description = "OfferImpl_Description";
        public static final String Amount = "OfferImpl_Amount";
        public static final String ActivityRange = "OfferImpl_Activity_Range";
        public static final String Qualifiers = "OfferImpl_Qualifiers";
        public static final String ItemTarget = "OfferImpl_Item_Target";
        public static final String Advanced = "OfferImpl_Advanced";
        public static final String Codes = "OfferImpl_Codes_Tab";
    }

    public static class GroupOrder {
        public static final int Description = 1000;
        public static final int Amount = 2000;
        public static final int ActivityRange = 3000;
        public static final int Qualifiers = 4000;
        public static final int ItemTarget = 5000;
        public static final int Advanced = 1000;
        public static final int Codes = 1000;
    }

}
