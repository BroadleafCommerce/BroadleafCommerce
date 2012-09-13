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

package org.broadleafcommerce.cms.admin.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;
import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.LocalizableResource.Generate;

/**
 * 
 * @author jfischer
 *
 */
@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface ContentManagementMessages extends ConstantsWithLookup {

    public String cmsModuleTitle();
    public String assetListTitle();
    public String newAssetTitle();
    public String deleteAssetTitle();
    public String pageListTitle();
    public String newPageTitle();
    public String deletePageTitle();
    public String structuredContentListTitle();
    public String newStructuredContentTitle();
    public String deleteStructuredContentTitle();
    public String pagesTitle();
    public String detailsTitle();
    public String allChildItemsTitle();
    public String defaultPageName();
    public String newItemTitle();
    public String staticAssetsTitle();
    public String staticAssetFoldersTitle();
    public String assetDescriptionTitle();
    public String newAssetDescriptionTitle();
    public String structuredContentTitle();
    public String userSandBoxTitle();
    public String approverSandBoxTitle();
    public String pendingApprovalTitle();
    public String contentTypeFilterTitle();
    public String scDetailsTabTitle();
    public String scRulesTabTitle();
    public String lockedMessage();
    public String scCustomerRule();
    public String scTimeRule();
    public String scRequestRule();
    public String scOrderItemRule();
    public String newItemRuleButtonTitle();
    public String scProductRule();
    public String basePage();
    public String basePageTemplate();
    public String baseStructuredContent();
    public String baseStructuredContentType();
    public String baseStructuredContentItemCriteria();
    public String baseStructuredContentFieldTemplate();
    public String baseSandBoxItem();
    public String assetUploadNameHint();
    public String assetUploadFullUrlHint();
    public String criteriaDoesNotMatch();

    public String PagesCustomPersistenceHandler_Page_Template();
    public String PagesCustomPersistenceHandler_Lock();
    public String PageTemplateCustomPersistenceHandler_ID();
    public String StaticAssetCustomPersistenceHandler_File();
//    public String StaticAssetCustomPersistenceHandler_Space_();
    public String StaticAssetCustomPersistenceHandler_Preview();
    public String StructuredContentCustomPersistenceHandler_Locale();
    public String StructuredContentCustomPersistenceHandler_Content_Type();
    public String StructuredContentCustomPersistenceHandler_Lock();
    public String StructuredContentTypeCustomPersistenceHandler_ID();
    //groups
    public String PagesCustomPersistenceHandler_Description();
    public String PagesCustomPersistenceHandler_Locked_Details();
    public String StaticAssetCustomPersistenceHandler_Upload();
    public String StaticAssetCustomPersistenceHandler_Asset_Details();
    public String StructuredContentCustomPersistenceHandler_Description();
    public String StructuredContentCustomPersistenceHandler_Locked_Details();
    public String StructuredContentCustomPersistenceHandler_Rules();
    public String redirectUrlPermissions();
    public String newURLRedirectTitle();
    public String URLHandlerImpl_friendyName();
    public String URLHandlerImpl_redirectType();
    public String URLHandlerImpl_newURL();
    public String URLHandlerImpl_incomingURL();
    public String URLHandlerImpl_ID();

}
