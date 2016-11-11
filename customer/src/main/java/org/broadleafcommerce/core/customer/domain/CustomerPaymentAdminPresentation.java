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
package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Chris Kittrell (ckittrell)
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE,
    tabs = {
        @AdminTabPresentation(name = CustomerPaymentAdminPresentation.TabName.Payment,
            order = CustomerPaymentAdminPresentation.TabOrder.Payment,
            groups = {
                @AdminGroupPresentation(name = CustomerPaymentAdminPresentation.GroupName.Payment,
                    order = CustomerPaymentAdminPresentation.GroupOrder.Payment,
                    untitled = true)
            }
        ),
        @AdminTabPresentation(name = CustomerPaymentAdminPresentation.TabName.BillingAddress,
            order = CustomerPaymentAdminPresentation.TabOrder.BillingAddress)
    }
)

public interface CustomerPaymentAdminPresentation {

    public static class TabName {

        public static final String Payment = "CustomerPaymentImpl_payment";
        public static final String BillingAddress = "CustomerPaymentImpl_billingAddress";

    }

    public static class TabOrder {

        public static final int Payment = 1000;
        public static final int BillingAddress = 2000;
    }

    public static class GroupName {

        public static final String Payment = "CustomerPaymentImpl_payment";
    }

    public static class GroupOrder {

        public static final int Payment = 1000;
    }

    public static class FieldOrder {

        public static final int PAYMENT_TYPE = 1000;
        public static final int PAYMENT_GATEWAY_TYPE = 2000;
        public static final int IS_DEFAULT = 3000;
        public static final int PAYMENT_TOKEN = 4000;
    }
}
