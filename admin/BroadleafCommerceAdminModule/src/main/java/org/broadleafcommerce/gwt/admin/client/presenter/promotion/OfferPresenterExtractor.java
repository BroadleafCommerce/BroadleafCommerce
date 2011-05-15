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
package org.broadleafcommerce.gwt.admin.client.presenter.promotion;

import org.broadleafcommerce.gwt.admin.client.datasource.promotion.OfferItemCriteriaListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.translation.AdvancedCriteriaToMVELTranslator;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.translation.FilterType;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.gwt.admin.client.view.promotion.ItemBuilderDisplay;
import org.broadleafcommerce.gwt.admin.client.view.promotion.OfferDisplay;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;

/**
 * 
 * @author jfischer
 *
 */
public class OfferPresenterExtractor {
	
	private static final AdvancedCriteriaToMVELTranslator TRANSLATOR = new AdvancedCriteriaToMVELTranslator();
	
	protected OfferPresenter presenter;
	
	public OfferPresenterExtractor(OfferPresenter presenter) {
		this.presenter = presenter;
	}

	protected OfferDisplay getDisplay() {
		return presenter.getDisplay();
	}
	
	public void removeItemQualifer(final ItemBuilderDisplay builder) {
		if (builder.getRecord() != null) {
			presenter.offerItemCriteriaDataSource.removeData(builder.getRecord(), new DSCallback() {
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					getDisplay().removeItemBuilder(builder);
				}
			});
		} else {
			getDisplay().removeItemBuilder(builder);
		}
	}
	
	public void applyData(final Record selectedRecord) {
		try {
			selectedRecord.setAttribute("totalitarianOffer", getDisplay().getRestrictRuleRadio().getValue().equals("YES"));
			selectedRecord.setAttribute("deliveryType",getDisplay().getDeliveryTypeRadio().getValue());
			if (getDisplay().getDeliveryTypeRadio().getValue().equals("CODE")) {
				selectedRecord.setAttribute("offerCode.offerCode", getDisplay().getCodeField().getValue().toString().trim());
			}
			
			final String type = getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("type").getValue().toString();
			
			extractCustomerData(selectedRecord);
			extractOrderData(selectedRecord, type);
			
			extractQualifierRuleType(selectedRecord);
			extractTargetItemData(selectedRecord, type);
			extractTargetRuleType(selectedRecord);
			extractFulfillmentGroupData(selectedRecord, type);
			
			for (FormItem formItem : getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getFields()) {
				selectedRecord.setAttribute(formItem.getName(), formItem.getValue());
			}
			
			extractQualifierData(selectedRecord, type, true);
			
			getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getDataSource().updateData(selectedRecord, new DSCallback() {
				public void execute(DSResponse response, Object rawData, DSRequest request) {
					try {
						extractQualifierData(selectedRecord, type, false);
						getDisplay().getDynamicFormDisplay().getSaveButton().disable();
					} catch (IncompatibleMVELTranslationException e) {
						SC.warn(e.getMessage());
					}
				}
			});
		} catch (IncompatibleMVELTranslationException e) {
			SC.warn(e.getMessage());
		}
	}
	
	protected void extractQualifierData(final Record selectedRecord, final String type, boolean isValidation) throws IncompatibleMVELTranslationException {
		if ((getDisplay().getBogoRadio().getValue().equals("YES") && type.equals("ORDER_ITEM")) || getDisplay().getItemRuleRadio().getValue().equals("ITEM_RULE") && !type.equals("ORDER_ITEM")) {
			for (final ItemBuilderDisplay builder : getDisplay().getItemBuilderViews()) {
				if (builder.getDirty()) {
					String temper = builder.getItemQuantity().getValue().toString();
					Integer quantity = Integer.parseInt(temper);
					String mvel;
					if (builder.getIncompatibleMVEL()) {
						mvel = builder.getRawItemTextArea().getValueAsString();
					} else {
						mvel = TRANSLATOR.createMVEL(builder.getItemFilterBuilder().getCriteria(), FilterType.ORDER_ITEM, builder.getItemFilterBuilder().getDataSource());
					}
					if (!isValidation) {
						if (builder.getRecord() != null) {
							builder.getRecord().setAttribute("quantity", quantity);
							builder.getRecord().setAttribute("orderItemMatchRule", mvel);
							presenter.offerItemCriteriaDataSource.updateData(builder.getRecord(), new DSCallback() {
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									builder.setDirty(false);
								}
							});
						} else {
							final Record temp = new Record();
							temp.setAttribute("quantity", quantity);
							temp.setAttribute("orderItemMatchRule", mvel);
							temp.setAttribute("_type", new String[]{((DynamicEntityDataSource) presenter.offerItemCriteriaDataSource).getDefaultNewEntityFullyQualifiedClassname()});
							temp.setAttribute(OfferItemCriteriaListDataSourceFactory.foreignKeyName, presenter.entityDataSource.getPrimaryKeyValue(selectedRecord));
							presenter.offerItemCriteriaDataSource.addData(temp, new DSCallback() {
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									builder.setDirty(false);
									builder.setRecord(temp);
								}
							});
						}
					}
				}
			}
		} else {
			if (!isValidation) {
				ItemBuilderDisplay[] displays = new ItemBuilderDisplay[]{};
				displays = getDisplay().getItemBuilderViews().toArray(displays);
				for (final ItemBuilderDisplay builder : displays) {
					removeItemQualifer(builder);
				}
			}
		}
		if (type.equals("ORDER_ITEM")) {
			selectedRecord.setAttribute("combinableWithOtherOffers", getDisplay().getOrderItemCombineRuleRadio().getValue().equals("YES"));
		}
	}

	protected void extractFulfillmentGroupData(final Record selectedRecord, final String type) throws IncompatibleMVELTranslationException {
		if (type.equals("FULFILLMENT_GROUP")) {
			if (getDisplay().getFgRuleRadio().getValue().equals("FG_RULE")) {
				if (!presenter.initializer.fgRuleIncompatible) {
					selectedRecord.setAttribute("appliesToFulfillmentGroupRules", TRANSLATOR.createMVEL(getDisplay().getFulfillmentGroupFilterBuilder().getCriteria(), FilterType.FULFILLMENT_GROUP, getDisplay().getFulfillmentGroupFilterBuilder().getDataSource()));
				} else {
					selectedRecord.setAttribute("appliesToFulfillmentGroupRules", getDisplay().getRawFGTextArea().getValue());
				}
			} else {
				Object value = null;
				selectedRecord.setAttribute("appliesToFulfillmentGroupRules", value);
				getDisplay().getFulfillmentGroupFilterBuilder().clearCriteria();
				getDisplay().getRawFGTextArea().setValue("");
			}
			selectedRecord.setAttribute("combinableWithOtherOffers", getDisplay().getFgCombineRuleRadio().getValue().equals("YES"));
		} else {
			Object value = null;
			selectedRecord.setAttribute("appliesToFulfillmentGroupRules", value);
			getDisplay().getFulfillmentGroupFilterBuilder().clearCriteria();
			getDisplay().getRawFGTextArea().setValue("");
		}
	}

	protected void extractTargetRuleType(final Record selectedRecord) {
		String offerItemTargetRuleType;
		if (
			getDisplay().getQualifyForAnotherPromoTargetRadio().getValue().equals("YES") &&
			getDisplay().getReceiveFromAnotherPromoTargetRadio().getValue().equals("NO")
		) {
			offerItemTargetRuleType = "QUALIFIER";
		} else if (
			getDisplay().getQualifyForAnotherPromoTargetRadio().getValue().equals("NO") &&
			getDisplay().getReceiveFromAnotherPromoTargetRadio().getValue().equals("YES")
		) {
			offerItemTargetRuleType = "TARGET";
		} else if (
			getDisplay().getQualifyForAnotherPromoTargetRadio().getValue().equals("YES") &&
			getDisplay().getReceiveFromAnotherPromoTargetRadio().getValue().equals("YES")
		) {
			offerItemTargetRuleType = "QUALIFIER_TARGET";
		} else {
			offerItemTargetRuleType = "NONE";
		}
		selectedRecord.setAttribute("offerItemTargetRuleType", offerItemTargetRuleType);
	}

	protected void extractTargetItemData(final Record selectedRecord, final String type) throws IncompatibleMVELTranslationException {
		if (type.equals("ORDER_ITEM")) {
			String temp = getDisplay().getTargetItemBuilder().getItemQuantity().getValue().toString();
			Integer quantity = Integer.parseInt(temp);
			String mvel;
			if (getDisplay().getTargetItemBuilder().getIncompatibleMVEL()) {
				mvel = getDisplay().getTargetItemBuilder().getRawItemTextArea().getValueAsString();
			} else {
				mvel = TRANSLATOR.createMVEL(getDisplay().getTargetItemBuilder().getItemFilterBuilder().getCriteria(), FilterType.ORDER_ITEM, getDisplay().getTargetItemBuilder().getItemFilterBuilder().getDataSource());
			}
			selectedRecord.setAttribute("targetItemCriteria.quantity", quantity);
			selectedRecord.setAttribute("targetItemCriteria.orderItemMatchRule", mvel);
		} else {
			selectedRecord.setAttribute("targetItemCriteria.quantity", 0);
			selectedRecord.setAttribute("targetItemCriteria.orderItemMatchRule", "");
		}
	}

	protected void extractQualifierRuleType(final Record selectedRecord) {
		String offerItemQualifierRuleType;
		if (
			getDisplay().getQualifyForAnotherPromoRadio().getValue().equals("YES") &&
			getDisplay().getReceiveFromAnotherPromoRadio().getValue().equals("NO")
		) {
			offerItemQualifierRuleType = "QUALIFIER";
		} else if (
			getDisplay().getQualifyForAnotherPromoRadio().getValue().equals("NO") &&
			getDisplay().getReceiveFromAnotherPromoRadio().getValue().equals("YES")
		) {
			offerItemQualifierRuleType = "TARGET";
		} else if (
			getDisplay().getQualifyForAnotherPromoRadio().getValue().equals("YES") &&
			getDisplay().getReceiveFromAnotherPromoRadio().getValue().equals("YES")
		) {
			offerItemQualifierRuleType = "QUALIFIER_TARGET";
		} else {
			offerItemQualifierRuleType = "NONE";
		}
		selectedRecord.setAttribute("offerItemQualifierRuleType", offerItemQualifierRuleType);
	}

	protected void extractOrderData(final Record selectedRecord, String type) throws IncompatibleMVELTranslationException {
		if (getDisplay().getOrderRuleRadio().getValue().equals("ORDER_RULE")) {
			if (!presenter.initializer.orderRuleIncompatible) {
				selectedRecord.setAttribute("appliesToOrderRules", TRANSLATOR.createMVEL(getDisplay().getOrderFilterBuilder().getCriteria(), FilterType.ORDER, getDisplay().getOrderFilterBuilder().getDataSource()));
			} else {
				selectedRecord.setAttribute("appliesToOrderRules", getDisplay().getRawOrderTextArea().getValue());
			}
		} else {
			Object value = null;
			selectedRecord.setAttribute("appliesToOrderRules", value);
			getDisplay().getOrderFilterBuilder().clearCriteria();
			getDisplay().getRawOrderTextArea().setValue("");
		}
		if (type.equals("ORDER")) {
			selectedRecord.setAttribute("combinableWithOtherOffers", getDisplay().getOrderCombineRuleRadio().getValue().equals("YES"));
		}
	}

	protected void extractCustomerData(final Record selectedRecord) throws IncompatibleMVELTranslationException {
		if (getDisplay().getCustomerRuleRadio().getValue().equals("CUSTOMER_RULE")) {
			if (!presenter.initializer.customerRuleIncompatible) {
				selectedRecord.setAttribute("appliesToCustomerRules", TRANSLATOR.createMVEL(getDisplay().getCustomerFilterBuilder().getCriteria(), FilterType.CUSTOMER, getDisplay().getCustomerFilterBuilder().getDataSource()));
			} else {
				selectedRecord.setAttribute("appliesToCustomerRules", getDisplay().getRawCustomerTextArea().getValue());
			}
		} else {
			Object value = null;
			selectedRecord.setAttribute("appliesToCustomerRules", value);
			getDisplay().getCustomerFilterBuilder().clearCriteria();
			getDisplay().getRawCustomerTextArea().setValue("");
		}
	}
}
