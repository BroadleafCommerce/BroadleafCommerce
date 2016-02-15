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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "OrderImpl_baseOrder",
        tabs = {
                @AdminTabPresentation(name = OrderAdminPresentation.TabName.OrderItems,
                        order = OrderAdminPresentation.TabOrder.OrderItems,
                        groups = {
                                @AdminGroupPresentation(name = OrderAdminPresentation.GroupName.General,
                                        order = OrderAdminPresentation.GroupOrder.General,
                                        untitled = true)
                        }
                ),
                @AdminTabPresentation(name = OrderAdminPresentation.TabName.FulfillmentGroups,
                        order = OrderAdminPresentation.TabOrder.FulfillmentGroups
                ),
                @AdminTabPresentation(name = OrderAdminPresentation.TabName.Payment,
                        order = OrderAdminPresentation.TabOrder.Payment
                ),
                @AdminTabPresentation(name = OrderAdminPresentation.TabName.Advanced,
                        order = OrderAdminPresentation.TabOrder.Advanced
                )
        }
)

public interface OrderAdminPresentation {
    public static class TabName {
        public static final String OrderItems = "OrderImpl_Order_Items_Tab";
        public static final String FulfillmentGroups = "OrderImpl_Fulfillment_Groups_Tab";
        public static final String Payment = "OrderImpl_Payment_Tab";
        public static final String Advanced = "OrderImpl_Advanced_Tab";
    }

    public static class TabOrder {
        public static final int OrderItems = 2000;
        public static final int FulfillmentGroups = 3000;
        public static final int Payment = 4000;
        public static final int Advanced = 5000;
    }
    
    public static class GroupName {
        public static final String General = "General";
    }

    public static class GroupOrder {
        public static final int General = 1000;
    }

    public static class FieldOrder {
        public static final int NAME = 1000;
        public static final int CUSTOMER = 2000;
        public static final int TOTAL = 3000;
        public static final int STATUS = 4000;
        public static final int SUBTOTAL = 5000;
        public static final int ORDERNUMBER = 6000;
        public static final int TOTALTAX = 7000;
        public static final int TOTALFGCHARGES = 8000;
        public static final int SUBMITDATE = 9000;
        public static final int EMAILADDRESS = 10000;

        public static final int ADJUSTMENTS = 1000;
        public static final int OFFERCODES = 2000;
    }
}