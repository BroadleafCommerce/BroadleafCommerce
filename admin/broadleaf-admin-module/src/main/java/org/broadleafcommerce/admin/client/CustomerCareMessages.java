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

package org.broadleafcommerce.admin.client;

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
public interface CustomerCareMessages extends ConstantsWithLookup {

    public String customerCareModuleTitle();
    public String orderMainTitle();
    public String customerMainTitle();
    public String blcProjectPage();
    public String customerListTitle();
    public String customerDetailsTitle();
    public String resetPasswordPrompt();
    public String ordersListTitle();
    public String orderDetailsTabTitle();
    public String orderDetailsTitle();
    public String orderAdjustmentsTitle();
    public String orderItemsTabTitle();
    public String orderItemsListTitle();
    public String orderItemFeeListTitle();
    public String orderItemAdjustmentsListTitle();
    public String fgTabTitle();
    public String fgListTitle();
    public String fgAdjustmentsListTitle();
    public String paymentInfoTabTitle();
    public String additionalAttributesListTitle();
    public String paymentInfoListTitle();
    public String offerCodeTabTitle();
    public String offerCodeListTitle();
    public String countrySearchPrompt();
    public String stateSearchPrompt();
    public String paymentAttributeKeyDefault();
    public String paymentAttributeValueDefault();
    public String newOrderAdjustmentTitle();
    public String newOrderItemAdjustmentTitle();
    public String newFGAdjustmentTitle();
    public String newOrderItemFeeTitle();
    public String usernameDefault();
    public String newCustomerTitle();
    public String confirmResetPassword();
    public String resetPasswordSuccessful();
    public String challengeQuestionSearchPrompt();
    public String localeSearchPrompt();
    public String baseCustomer();
    public String baseFulfillmentGroup();
    public String baseOrderItem();
    public String bundleOrderItem();
    public String giftWrapOrderItem();
    public String discreteOrderItem();
    public String dynamicPriceOrderItem();
    public String baseOrder();
    public String baseDiscreteOrderItemFreePrice();
    public String baseFulfillmentGroupAdjustment();
    public String baseOfferCode();
    public String baseOrderAdjustment();
    public String baseOrderItemAdjustment();
    public String basePaymentInfo();
    public String baseCountry();
    public String baseState();
    public String baseChallengeQuestion();

}
