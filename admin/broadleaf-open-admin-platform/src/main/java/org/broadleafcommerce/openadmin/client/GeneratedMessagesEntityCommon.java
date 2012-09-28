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
public interface GeneratedMessagesEntityCommon extends ConstantsWithLookup {
    public String Auditable_Date_Created();
    public String Auditable_Created_By();
    public String Auditable_Date_Updated();
    public String Auditable_Updated_By();
    public String LocaleImpl_baseLocale();
    public String LocaleImpl_Locale_Code();
    public String LocaleImpl_Name();
    public String LocaleImpl_Is_Default();
    public String RequestDTOImpl_Request_URI();
    public String RequestDTOImpl_Full_Url();
    public String RequestDTOImpl_Is_Secure();
    public String SandBoxImpl_SandBox_Type();
    public String SiteImpl_Site_Name();
    public String SiteImpl_Site_Identifier_Type();
    public String SiteImpl_Site_Identifier_Value();
    public String SiteImpl_Production_SandBox();
    public String TimeDTO_Hour_Of_Day();
    public String TimeDTO_Day_Of_Week();
    public String TimeDTO_Month();
    public String TimeDTO_Day_Of_Month();
    public String TimeDTO_Minute();
    public String TimeDTO_Date();

    public String Auditable_Audit();
    public String LocaleImpl_Details();
    public String SandBoxImpl_Description();
    public String SiteImpl_Site();
}
