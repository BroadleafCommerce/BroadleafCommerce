/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.gwt.admin.client;

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
public interface AdminMessages extends ConstantsWithLookup {

	public String promotionsTitle();
	public String blcProjectPage();
	public String defaultCategoryName();
	public String newCategoryTitle();
	public String confirmDelete();
	public String defaultProductName();
	public String newProductTitle();
	public String categorySearchTitle();
	public String productSearchTitle();
	public String setPromotionMessageTitle();
	public String productSearchPrompt();
	public String mediaNameDefault();
	public String mediaLabelDefault();
	public String newMediaTitle();
	public String mediaSizeSmall();
	public String mediaSizeMedium();
	public String mediaSizeLarge();
	public String newAttributeTitle();
	public String categorySearchPrompt();
	public String usernameDefault();
	public String newCustomerTitle();
	public String challengeQuestionSearchPrompt();
	public String countrySearchPrompt();
	public String stateSearchPrompt();
	public String paymentAttributeKeyDefault();
	public String paymentAttributeValueDefault();
	public String newOrderAdjustmentTitle();
	public String newOrderItemAdjustmentTitle();
	public String newFGAdjustmentTitle();
	public String offerNameDefault();
	public String newOfferTitle();
	public String offerObtainSettingsHelpTitle();
	public String offerObtainSettingsHelpContent();
	public String bogoHelpTitle();
	public String bogoHelpContent();
	public String categoryListTitle();
	public String orphanCategoryListTitle();
	public String categoryDetailsTitle();
	public String allChildCategoriesListTitle();
	public String productsTabTitle();
	public String featuredProductsListTitle();
	public String allProductsListTitle();
	public String mediaTabTitle();
	public String mediaListTitle();
	public String productsListTitle();
	public String productDetailsTitle();
	public String productAttributesTitle();
	public String crossSaleProductsTitle();
	public String upsaleProductsTitle();
	public String allParentCategoriesListTitle();
	public String customerListTitle();
	public String customerDetailsTitle();
	public String updatePasswordPrompt();
	public String saveButtonTitle();
	public String cancelButtonTitle();
	public String updateCustomerPasswordTitle();
	public String passwordNotMatchError();
	public String passwordPrompt();
	public String passwordAgainPrompt();
	public String passwordChangeRequiredTitle();
	public String categoryMainTitle();
	public String productMainTitle();
	public String orderMainTitle();
	public String customerMainTitle();
	public String promotionMainTitle();
	public String ordersListTitle();
	public String orderDetailsTabTitle();
	public String orderDetailsTitle();
	public String orderAdjustmentsTitle();
	public String orderItemsTabTitle();
	public String orderItemsListTitle();
	public String orderItemAdjustmentsListTitle();
	public String fgTabTitle();
	public String fgListTitle();
	public String fgAdjustmentsListTitle();
	public String paymentInfoTabTitle();
	public String paymentInfoListTitle();
	public String additionalAttributesListTitle();
	public String offerCodeTabTitle();
	public String offerCodeListTitle();
	public String promotionsListTitle();
	public String promotionDetailsTitle();
	public String advancedCriteriaButtonTitle();
	public String basicPromotionLabel();
	public String restrictOnlyPromotionLabel();
	public String yesRadioChoice();
	public String noRadioChoice();
	public String advancedRestrictionsViewTitle();
	public String customerObtainLabel();
	public String deliveryTypeEnumAutomatic();
	public String deliveryTypeEnumCode();
	public String deliveryTypeEnumManual();
	public String offerCodeFieldTitle();
	public String whichCustomerLabel();
	public String allCustomerRadioChoice();
	public String buildCustomerRadioChoice();
	public String customerSectionViewTitle();
	public String orderSectionLabel();
	public String noneOrderRadioChoice();
	public String buildOrderRadioChoice();
	public String orderCombineLabel();
	public String orderQualificationSectionViewTitle();
	public String bogoQuestionLabel();
	public String requiredItemsLabel();
	public String noneItemRadioChoice();
	public String buildItemRadioChoice();
	public String newItemRuleButtonTitle();
	public String receiveFromAnotherPromoLabel();
	public String qualifiyForAnotherPromoLabel();
	public String itemQualificationSectionTitle();
	public String targetItemsLabel();
	public String receiveFromAnotherPromoTargetLabel();
	public String qualifiyForAnotherPromoTargetLabel();
	public String itemTargetSectionTitle();
	public String stepFGLabel();
	public String allFGRadioChoice();
	public String buildFGRadioChoice();
	public String fgCombineLabel();
	public String fgSectionViewTitle();
	public String adminModuleTitle();
	public String mvelTranslationProblem();
	public String userManagementMainTitle();
	public String userListTitle();
	public String userDetailsTitle();
	public String userRolesTitle();
	public String newAdminUserTitle();
	public String clonePromotionHelp();
	public String orderItemCombineLabel();
	
}
