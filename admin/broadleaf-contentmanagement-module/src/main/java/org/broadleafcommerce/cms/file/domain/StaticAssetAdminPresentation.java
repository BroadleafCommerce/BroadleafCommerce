/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

/**
 * Created by Jon on 11/2/15.
 */
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE,
        tabs = {
                @AdminTabPresentation(name = StaticAssetAdminPresentation.TabName.General,
                        order = StaticAssetAdminPresentation.TabOrder.General,
                        groups = {
                                @AdminGroupPresentation(name = StaticAssetAdminPresentation.GroupName.General,
                                        order = StaticAssetAdminPresentation.GroupOrder.General,
                                        untitled = true),
                                @AdminGroupPresentation(name = StaticAssetAdminPresentation.GroupName.Options,
                                        order = StaticAssetAdminPresentation.GroupOrder.Options,
                                        column = 1),
                                @AdminGroupPresentation(name = StaticAssetAdminPresentation.GroupName.Image,
                                        order = StaticAssetAdminPresentation.GroupOrder.Image,
                                        untitled = true),
                                @AdminGroupPresentation(name = StaticAssetAdminPresentation.GroupName.File_Details,
                                        order = StaticAssetAdminPresentation.GroupOrder.File_Details,
                                        column = 1)
                        }
                )
        }
)
public interface StaticAssetAdminPresentation {
    public static class TabName {

        public static final String General = "StaticAssetImpl_FileDetails_Tab";
        public static final String Advanced = "StaticAssetImpl_Advanced_Tab";
    }

    public static class TabOrder {

        public static final int General = 100;
        public static final int Advanced = 2000;
    }

    public static class GroupName {
        public static final String General = "StaticAssetImpl_Asset_Description";
        public static final String Options = "StaticAssetImpl_Asset_Options";
        public static final String Image = "StaticAssetImpl_Asset_Image";
        public static final String Dates = "StaticAssetImpl_Asset_Dates";
        public static final String File_Details = "StaticAssetImpl_Asset_File_Details";

    }

    public static class GroupOrder {
        public static final int General = 2000;
        public static final int Options = 6000;
        public static final int Image = 1000;
        public static final int Dates = 4000;
        public static final int File_Details = 5000;
    }

    public static class FieldOrder {

        // General Fields
        public static final int NAME = 3000;
        public static final int URL = 6000;
        public static final int TITLE = 1000;
        public static final int ALT_TEXT = 2000;

        public static final int MIME_TYPE = 5000;
        public static final int FILE_EXTENSION = 6000;
        public static final int FILE_SIZE = 7000;

        // Used by subclasses to know where the last field is.
        public static final int LAST = 7000;

    }
}
