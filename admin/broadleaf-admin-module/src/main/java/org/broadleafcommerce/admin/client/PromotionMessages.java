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

@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
@DefaultLocale("en_US")
public interface PromotionMessages extends ConstantsWithLookup {

    public String promotionMainTitle();
    public String blcProjectPage();
    public String promotionsListTitle();
    public String clonePromotionHelp();
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
    public String orderItemCombineLabel();
    public String bogoQuestionLabel();
    public String requiredItemsLabel();
    public String buildItemRadioChoice();
    public String noneItemRadioChoice();
    public String newItemRuleButtonTitle();
    public String receiveFromAnotherPromoLabel();
    public String qualifiyForAnotherPromoLabel();
    public String itemQualificationSectionTitle();
    public String targetItemsLabel();
    public String receiveFromAnotherPromoTargetLabel();
    public String qualifiyForAnotherPromoTargetLabel();
    public String itemTargetSectionTitle();
    public String fgCombineLabel();
    public String stepFGLabel();
    public String allFGRadioChoice();
    public String buildFGRadioChoice();
    public String fgSectionViewTitle();
    public String mvelTranslationProblem();
    public String offerNameDefault();
    public String offerObtainSettingsHelpTitle();
    public String offerObtainSettingsHelpContent();
    public String bogoHelpTitle();
    public String bogoHelpContent();
    public String baseOffer();
    public String baseOfferItemCriteria();

}
