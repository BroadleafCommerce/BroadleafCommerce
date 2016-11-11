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
        @AdminTabPresentation(name = OrderAdminPresentation.TabName.General,
            order = OrderAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = OrderAdminPresentation.GroupName.General,
                    order = OrderAdminPresentation.GroupOrder.General,
                    untitled = true),
                @AdminGroupPresentation(name = OrderAdminPresentation.GroupName.Customer,
                    order = OrderAdminPresentation.GroupOrder.Customer,
                    column = 1),
                @AdminGroupPresentation(name = OrderAdminPresentation.GroupName.OrderTotals,
                    order = OrderAdminPresentation.GroupOrder.OrderTotals,
                    untitled = true, column = 1)
            }
        ),
        @AdminTabPresentation(name = OrderAdminPresentation.TabName.FulfillmentGroups,
            order = OrderAdminPresentation.TabOrder.FulfillmentGroups),
        @AdminTabPresentation(name = OrderAdminPresentation.TabName.Payment,
            order = OrderAdminPresentation.TabOrder.Payment),
        @AdminTabPresentation(name = OrderAdminPresentation.TabName.Advanced,
            order = OrderAdminPresentation.TabOrder.Advanced,
            groups = {
                @AdminGroupPresentation(name = OrderAdminPresentation.GroupName.Advanced,
                    order = OrderAdminPresentation.GroupOrder.Advanced,
                    untitled = true)
            }
        )
    }
)

public interface OrderAdminPresentation {
    class TabName {
        public static final String General = "General";
        public static final String FulfillmentGroups = "OrderImpl_Fulfillment_Groups_Tab";
        public static final String Payment = "OrderImpl_Payment_Tab";
        public static final String Advanced = "OrderImpl_Advanced_Tab";
    }

    class TabOrder {
        public static final int General = 1000;
        public static final int FulfillmentGroups = 2000;
        public static final int Payment = 3000;
        public static final int Advanced = 4000;
    }
    
    class GroupName {
        public static final String General = "General";
        public static final String Customer = "OrderImpl_Customer_Details_Group";
        public static final String OrderTotals = "OrderImpl_Order_Totals_Group";
        public static final String Advanced = "OrderImpl_Advanced";
    }

    class GroupOrder {
        public static final int General = 1000;
        public static final int Customer = 2000;
        public static final int OrderTotals = 3000;

        public static final int Advanced = 1000;
    }

    class FieldOrder {
        public static final int NAME = 1000;
        public static final int ORDERNUMBER = 2000;
        public static final int STATUS = 3000;
        public static final int SUBMITDATE = 4000;

        public static final int CUSTOMER = 1000;
        public static final int EMAILADDRESS = 2000;

        public static final int SUBTOTAL = 1000;
        public static final int TOTALTAX = 2000;
        public static final int TOTALFGCHARGES = 3000;
        public static final int TOTAL = 4000;

        public static final int ADJUSTMENTS = 1000;
        public static final int OFFERCODES = 2000;
        public static final int ATTRIBUTES = 3000;
    }
}
