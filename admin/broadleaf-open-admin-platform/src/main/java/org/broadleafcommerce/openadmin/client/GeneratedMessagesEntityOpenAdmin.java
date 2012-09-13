/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;

/**
 * 
 * @author ppatel
 *
 */
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface GeneratedMessagesEntityOpenAdmin extends ConstantsWithLookup {

    public String AdminAuditable_Date_Created();
    public String AdminAuditable_Date_Updated();
    public String SandBoxItemImpl_Date_Created();
    public String SandBoxItemImpl_Date_Updated();
    public String SandBoxItemImpl_Admin_User_Login();
    public String SandBoxItemImpl_Original_Id();
    public String SandBoxItemImpl_baseSandBoxItem();
    public String SandBoxItemImpl_Item_Type();
    public String SandBoxItemImpl_Operation_Type();
    public String SandBoxItemImpl_Description();
    public String AdminPermissionImpl_baseAdminPermission();
    public String AdminPermissionImpl_Admin_Permission_ID();
    public String AdminPermissionImpl_Name();
    public String AdminPermissionImpl_Permission_Type();
    public String AdminPermissionImpl_Description();
    public String AdminPermissionQualifiedEntityImpl_Ceiling_Entity_Name();
    public String AdminRoleImpl_baseAdminRole();
    public String AdminRoleImpl_Admin_Role_ID();
    public String AdminRoleImpl_Name();
    public String AdminRoleImpl_Description();
    public String AdminUserImpl_baseAdminUser();
    public String AdminUserImpl_Admin_User_ID();
    public String AdminUserImpl_Admin_Name();
    public String AdminUserImpl_Admin_Login();
    public String AdminUserImpl_Admin_Email_Address();
    public String AdminUserImpl_Phone_Number();
    public String AdminUserImpl_Active_Status();

    public String AdminAuditable_Audit();
    public String SandBoxItemImpl_Audit();
    public String SandBoxItemImpl_Details();
    public String AdminPermissionImpl_Permission();
    public String AdminPermissionImpl_Primary_Key();
    public String AdminPermissionQualifiedEntityImpl_Permission();
    public String AdminRoleImpl_Primary_Key();
    public String AdminRoleImpl_Role();
    public String AdminUserImpl_Primary_Key();
    public String AdminUserImpl_User();
    public String BLCRichTextItem_Edit();
}
