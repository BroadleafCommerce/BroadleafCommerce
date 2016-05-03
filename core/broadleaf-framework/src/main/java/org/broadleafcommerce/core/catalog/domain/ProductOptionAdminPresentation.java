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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * @author Jon Fleschler (jfleschler)
 */
@AdminPresentationClass(friendlyName = "ProductOptionImpl_baseProductOption", populateToOneFields=PopulateToOneFieldsEnum.TRUE,
    tabs = {
        @AdminTabPresentation(name = ProductOptionAdminPresentation.TabName.General,
            order = ProductOptionAdminPresentation.TabOrder.General,
            groups = {
                @AdminGroupPresentation(name = ProductOptionAdminPresentation.GroupName.General,
                    order = ProductOptionAdminPresentation.GroupOrder.General,
                    untitled = true),
                @AdminGroupPresentation(name = ProductOptionAdminPresentation.GroupName.Details,
                    order = ProductOptionAdminPresentation.GroupOrder.Details,
                    column = 1),
                @AdminGroupPresentation(name = ProductOptionAdminPresentation.GroupName.Validation,
                    order = ProductOptionAdminPresentation.GroupOrder.Validation,
                    column = 1)
            }
        )
    }
)

public interface ProductOptionAdminPresentation {

    public static class TabName {
        public static final String General = "General";
    }

    public static class TabOrder {
        public static final int General = 1000;
    }

    public static class GroupName {
        public static final String General = "General";
        public static final String Details = "productOption_details";
        public static final String Validation = "productOption_validation";
    }

    public static class GroupOrder {
        public static final int General = 1000;
        public static final int Details = 1000;
        public static final int Validation = 2000;
    }

    public static class FieldOrder {
        public static final int name = 1000;
        public static final int label = 2000;
        public static final int type = 3000;

        public static final int attributeName = 1000;
        public static final int displayOrder = 2000;
        public static final int useInSkuGeneration = 3000;

        public static final int required = 1000;
        public static final int validationStrategyType = 2000;
        public static final int validationType = 3000;
        public static final int validationString = 4000;
        public static final int errorCode = 5000;
        public static final int errorMessage = 6000;
    }
}
