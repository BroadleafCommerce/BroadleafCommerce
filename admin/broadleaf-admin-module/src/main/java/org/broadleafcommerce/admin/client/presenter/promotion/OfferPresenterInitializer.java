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

package org.broadleafcommerce.admin.client.presenter.promotion;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import org.broadleafcommerce.admin.client.view.promotion.OfferDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.openadmin.client.translation.MVELToAdvancedCriteriaTranslator;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterRestartCallback;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

import java.util.LinkedHashMap;

/**
 * 
 * @author jfischer
 *
 */
public class OfferPresenterInitializer {

	private static final MVELToAdvancedCriteriaTranslator TRANSLATOR = new MVELToAdvancedCriteriaTranslator();
	
	protected OfferPresenter presenter;
	protected boolean customerRuleIncompatible = false;
	protected boolean orderRuleIncompatible = false;
	protected boolean fgRuleIncompatible = false;
	protected DynamicEntityDataSource offerItemCriteriaDataSource;
    protected DynamicEntityDataSource offerItemTargetCriteriaDataSource;
	protected DynamicEntityDataSource orderItemDataSource;
	
	public OfferPresenterInitializer(OfferPresenter presenter, DynamicEntityDataSource offerItemCriteriaDataSource, DynamicEntityDataSource offerItemTargetCriteriaDataSource, DynamicEntityDataSource orderItemDataSource) {
		this.presenter = presenter;
		this.offerItemCriteriaDataSource = offerItemCriteriaDataSource;
        this.offerItemTargetCriteriaDataSource = offerItemTargetCriteriaDataSource;
		this.orderItemDataSource = orderItemDataSource;
	}
	
	protected OfferDisplay getDisplay() {
		return presenter.getDisplay();
	}
	
	public void initSectionBasedOnType(String sectionType, Record selectedRecord, final FilterRestartCallback cb) {
        getDisplay().getCustomerLayout().setVisible(true);
        getDisplay().getOrderSectionLayout().setVisible(true);
        getDisplay().getCustomerSection().setVisible(true);
        getDisplay().getOrderSection().setVisible(true);
		getDisplay().getItemQualificationSectionView().setVisible(true);
		if (sectionType.equals("FULFILLMENT_GROUP")) {
			getDisplay().getFgSectionView().setVisible(true);
		} else {
			getDisplay().getFgSectionView().setVisible(false);
		}
		if (sectionType.equals("ORDER_ITEM")) {
			getDisplay().getBogoQuestionLayout().setVisible(true);
			getDisplay().getItemTargetSectionView().setVisible(true);
			getDisplay().getOrderItemLayout().setVisible(false);
			getDisplay().getRequiredItemsLayout().setVisible(false);
			getDisplay().getOrderItemCombineForm().setVisible(true);
			getDisplay().getOrderItemCombineLabel().setVisible(true);
		} else {
			getDisplay().getBogoQuestionLayout().setVisible(false);
			getDisplay().getItemTargetSectionView().setVisible(false);
			getDisplay().getRequiredItemsLayout().setVisible(true);
			getDisplay().getOrderItemLayout().setVisible(true);
			getDisplay().getOrderItemCombineForm().setVisible(false);
			getDisplay().getOrderItemCombineLabel().setVisible(false);
		}
		if (sectionType.equals("ORDER")) {
			getDisplay().getOrderCombineForm().setVisible(true);
			getDisplay().getOrderCombineLabel().setVisible(true);
			
			//Order promotions cannot have a discount type of "FIX_PRICE", since it would not be correct to set the order total to a fixed amount
			LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
			String[][] enumerationValues = (String[][]) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getDataSource().getField("discountType").getAttributeAsObject("enumerationValues");
			for (int j=0; j<enumerationValues.length; j++) {
				if (!enumerationValues[j][0].equals("FIX_PRICE")) {
					valueMap.put(enumerationValues[j][0], enumerationValues[j][1]);
				}
			}
			getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").setValueMap(valueMap);
			if (getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").getValue().equals("FIX_PRICE")) {
				getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").setValue("PERCENT_OFF");
				getDisplay().getOrderCombineForm().enable();
				getDisplay().getFGCombineForm().enable();
				getDisplay().getOrderItemCombineForm().enable();
				getDisplay().getOrderCombineRuleRadio().setValue("YES");
				getDisplay().getFgCombineRuleRadio().setValue("YES");
				getDisplay().getOrderItemCombineRuleRadio().setValue("YES");
			}
		} else {
			getDisplay().getOrderCombineForm().setVisible(false);
			getDisplay().getOrderCombineLabel().setVisible(false);
			
			//reset the discount types to display all discount types, in case we had previously hidden the "FIX_PRICE" option
			LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
			String[][] enumerationValues = (String[][]) getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getDataSource().getField("discountType").getAttributeAsObject("enumerationValues");
			for (int j=0; j<enumerationValues.length; j++) {
				valueMap.put(enumerationValues[j][0], enumerationValues[j][1]);
			}
			
			getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").setValueMap(valueMap);
		}
		
		initBasicItems(selectedRecord);
		initCustomerCriteria(selectedRecord);
		initOrderCriteria(selectedRecord);
		initItemQualifiers(selectedRecord, sectionType, cb);
		
		if (sectionType.equals("ORDER_ITEM")) {
			initItemTargets(selectedRecord, cb);
		} else if (sectionType.equals("FULFILLMENT_GROUP")) {
			initFGCriteria(selectedRecord);
		}
	}

	public void initBasicItems(final Record selectedRecord) {
		Boolean isTotalitarianOffer = selectedRecord.getAttributeAsBoolean("totalitarianOffer");
		getDisplay().getRestrictRuleRadio().setValue(isTotalitarianOffer?"YES":"NO");
		String deliveryType = selectedRecord.getAttribute("deliveryType");
		getDisplay().getDeliveryTypeRadio().setValue(deliveryType);
		initDeliveryType(deliveryType, selectedRecord);
		getDisplay().getQualifyingItemSubTotal().setValue(selectedRecord.getAttribute("qualifyingItemSubTotal")==null?0.0:Float.parseFloat(selectedRecord.getAttribute("qualifyingItemSubTotal")));
	}

	public void initCustomerCriteria(final Record selectedRecord) {
		customerRuleIncompatible = false;
		getDisplay().getCustomerFilterBuilder().clearCriteria();
		String customerRules = selectedRecord.getAttribute("appliesToCustomerRules");
		String customerRule = customerRules==null?"ALL":"CUSTOMER_RULE";
		getDisplay().getCustomerRuleRadio().setValue(customerRule);
		if (customerRules != null) {
			getDisplay().getCustomerRuleRadio().setValue(customerRule);
			try {
				AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(customerRules, getDisplay().getCustomerFilterBuilder().getDataSource());
				if (myCriteria != null) {
					getDisplay().getCustomerFilterBuilder().setCriteria(myCriteria);
				}
			} catch (IncompatibleMVELTranslationException e) {
				customerRuleIncompatible = true;
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(BLCMain.getMessageManager().getString("mvelTranslationProblem"));
				getDisplay().getRawCustomerTextArea().setValue(customerRules);
			}
		}
		initCustomerRule(customerRule, selectedRecord);
	}
	
	public void initFGCriteria(final Record selectedRecord) {
		fgRuleIncompatible = false;
		getDisplay().getFulfillmentGroupFilterBuilder().clearCriteria();
		String fgRules = selectedRecord.getAttribute("appliesToFulfillmentGroupRules");
		String fgRule = fgRules==null?"ALL":"FG_RULE";
		getDisplay().getFgRuleRadio().setValue(fgRule);
		if (fgRules != null) {
			getDisplay().getFgRuleRadio().setValue(fgRule);
			try {
				AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(fgRules, getDisplay().getFulfillmentGroupFilterBuilder().getDataSource());
				if (myCriteria != null) {
					getDisplay().getFulfillmentGroupFilterBuilder().setCriteria(myCriteria);
				}
			} catch (IncompatibleMVELTranslationException e) {
				fgRuleIncompatible = true;
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(BLCMain.getMessageManager().getString("mvelTranslationProblem"));
				getDisplay().getRawFGTextArea().setValue(fgRules);
			}
		}
		Boolean combinable = selectedRecord.getAttributeAsBoolean("combinableWithOtherOffers");
		if (combinable == null) {
			combinable = true;
		}
		getDisplay().getFgCombineRuleRadio().setValue(combinable?"YES":"NO");
		
		if (getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").getValue().equals("FIX_PRICE")) {
			getDisplay().getOrderCombineForm().disable();
			getDisplay().getFGCombineForm().disable();
			getDisplay().getOrderItemCombineForm().disable();
			getDisplay().getOrderCombineRuleRadio().setValue("NO");
			getDisplay().getFgCombineRuleRadio().setValue("NO");
			getDisplay().getOrderItemCombineRuleRadio().setValue("NO");
		}
		
		initFGRule(fgRule, selectedRecord);
	}

	public void initOrderCriteria(final Record selectedRecord) {
		orderRuleIncompatible = false;
		getDisplay().getOrderFilterBuilder().clearCriteria();
		String orderRules = selectedRecord.getAttribute("appliesToOrderRules");
		String orderRule = orderRules==null?"NONE":"ORDER_RULE";
		getDisplay().getOrderRuleRadio().setValue(orderRule);
		if (orderRules != null) {
			try {
				AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(orderRules, getDisplay().getOrderFilterBuilder().getDataSource());
				if (myCriteria != null) {
					getDisplay().getOrderFilterBuilder().setCriteria(myCriteria);
				}
			} catch (IncompatibleMVELTranslationException e) {
				orderRuleIncompatible = true;
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(BLCMain.getMessageManager().getString("mvelTranslationProblem"));
				getDisplay().getRawOrderTextArea().setValue(orderRules);
			}
		}
		Boolean combinable = selectedRecord.getAttributeAsBoolean("combinableWithOtherOffers");
		if (combinable == null) {
			combinable = true;
		}
		getDisplay().getOrderCombineRuleRadio().setValue(combinable?"YES":"NO");
		initOrderRule(orderRule, selectedRecord);
	}

    public void initItemTargets(final Record selectedRecord, final FilterRestartCallback cb) {
        Criteria relationshipCriteria = offerItemTargetCriteriaDataSource.createRelationshipCriteria(offerItemTargetCriteriaDataSource.getPrimaryKeyValue(selectedRecord));
        offerItemTargetCriteriaDataSource.fetchData(relationshipCriteria, new DSCallback() {
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                boolean isTargetCriteria = false;
                if (response.getTotalRows() > 0) {
                    for (Record record : response.getData()) {
                        if (Integer.parseInt(record.getAttribute("quantity")) > 0) {
                            isTargetCriteria = true;
                            break;
                        }
                    }
                }

                getDisplay().getNewTargetItemBuilderLayout().setVisible(true);
                if (isTargetCriteria) {
                    getDisplay().removeAllTargetItemBuilders();
                    for (Record record : response.getData()) {
                        if (Integer.parseInt(record.getAttribute("quantity")) > 0) {
                            final ItemBuilderDisplay display = getDisplay().addTargetItemBuilder(orderItemDataSource);
                            presenter.bindItemBuilderEvents(display, true);
                            display.getItemFilterBuilder().clearCriteria();
                            display.setRecord(record);
                            display.getItemQuantity().setValue(Integer.parseInt(record.getAttribute("quantity")));
                            try {
                                display.getItemFilterBuilder().setVisible(true);
                                display.getRawItemForm().setVisible(false);
                                AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(record.getAttribute("orderItemMatchRule"), orderItemDataSource);
                                if (myCriteria != null) {
                                    display.getItemFilterBuilder().setCriteria(myCriteria);
                                }
                            } catch (IncompatibleMVELTranslationException e) {
                                GWT.log("Could not translate MVEL", e);
                                BLCMain.MASTERVIEW.getStatus().setContents(BLCMain.getMessageManager().getString("mvelTranslationProblem"));
                                display.setIncompatibleMVEL(true);
                                display.getItemFilterBuilder().setVisible(false);
                                display.getRawItemForm().setVisible(true);
                                display.getRawItemTextArea().setValue(record.getAttribute("orderItemMatchRule"));
                            }
                            display.getRemoveButton().addClickHandler(new ClickHandler() {
                                public void onClick(ClickEvent event) {
                                    getDisplay().removeTargetItemBuilder(display);
                                }
                            });
                        }
                    }
                } else {
                    getDisplay().removeAllTargetItemBuilders();
                    presenter.bindItemBuilderEvents(getDisplay().addTargetItemBuilder(orderItemDataSource), true);
                }
                if (cb != null) {
                    cb.processComplete();
                }
            }
        });

        String offerItemTargetRuleType = selectedRecord.getAttribute("offerItemTargetRuleType");
        if (offerItemTargetRuleType == null) {
            offerItemTargetRuleType = "NONE";
        }
        if (offerItemTargetRuleType.equals("NONE")) {
            getDisplay().getQualifyForAnotherPromoTargetRadio().setValue("NO");
            getDisplay().getReceiveFromAnotherPromoTargetRadio().setValue("NO");
        } else if (offerItemTargetRuleType.equals("QUALIFIER")) {
            getDisplay().getQualifyForAnotherPromoTargetRadio().setValue("YES");
            getDisplay().getReceiveFromAnotherPromoTargetRadio().setValue("NO");
        } else if (offerItemTargetRuleType.equals("TARGET")) {
            getDisplay().getQualifyForAnotherPromoTargetRadio().setValue("NO");
            getDisplay().getReceiveFromAnotherPromoTargetRadio().setValue("YES");
        } else if (offerItemTargetRuleType.equals("QUALIFIER_TARGET")) {
            getDisplay().getQualifyForAnotherPromoTargetRadio().setValue("YES");
            getDisplay().getReceiveFromAnotherPromoTargetRadio().setValue("YES");
        }
    }

	public void initItemQualifiers(final Record selectedRecord, final String type, final FilterRestartCallback cb) {
		Criteria relationshipCriteria = offerItemCriteriaDataSource.createRelationshipCriteria(offerItemCriteriaDataSource.getPrimaryKeyValue(selectedRecord));
		offerItemCriteriaDataSource.fetchData(relationshipCriteria, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				boolean isItemQualifierCriteria = false;
				if (response.getTotalRows() > 0) {
					for (Record record : response.getData()) {
						if (Integer.parseInt(record.getAttribute("quantity")) > 0) {
							isItemQualifierCriteria = true;
							break;
						}
					}
				}
				
				if (isItemQualifierCriteria) {
					if (type.equals("ORDER_ITEM")) {
						initBogoRule("YES");
					} else {
						initItemRule("ITEM_RULE");
					}
					getDisplay().getBogoRadio().setValue("YES");
					getDisplay().getItemRuleRadio().setValue("ITEM_RULE");
					getDisplay().removeAllItemBuilders();
					for (Record record : response.getData()) {
						if (Integer.parseInt(record.getAttribute("quantity")) > 0) {
							final ItemBuilderDisplay display = getDisplay().addItemBuilder(orderItemDataSource);
							presenter.bindItemBuilderEvents(display, false);
							display.getItemFilterBuilder().clearCriteria();
							display.setRecord(record);
							display.getItemQuantity().setValue(Integer.parseInt(record.getAttribute("quantity")));
							try {
								display.getItemFilterBuilder().setVisible(true);
								display.getRawItemForm().setVisible(false);
								AdvancedCriteria myCriteria = TRANSLATOR.createAdvancedCriteria(record.getAttribute("orderItemMatchRule"), orderItemDataSource);
								if (myCriteria != null) {
									display.getItemFilterBuilder().setCriteria(myCriteria);
								}
							} catch (IncompatibleMVELTranslationException e) {
								GWT.log("Could not translate MVEL", e);
								BLCMain.MASTERVIEW.getStatus().setContents(BLCMain.getMessageManager().getString("mvelTranslationProblem"));
								display.setIncompatibleMVEL(true);
								display.getItemFilterBuilder().setVisible(false);
								display.getRawItemForm().setVisible(true);
								display.getRawItemTextArea().setValue(record.getAttribute("orderItemMatchRule"));
							}
							display.getRemoveButton().addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									getDisplay().removeItemBuilder(display);
								}
							});
						}
					}
				} else {
					if (type.equals("ORDER_ITEM")) {
						initBogoRule("NO");
					} else {
						initItemRule("NO");
					}
					getDisplay().getBogoRadio().setValue("NO");
					getDisplay().getItemRuleRadio().setValue("NO");
					getDisplay().removeAllItemBuilders();
					presenter.bindItemBuilderEvents(getDisplay().addItemBuilder(orderItemDataSource), false);
				}
                if (cb != null) {
                    cb.processComplete();
                }
			}
		});
		String offerItemQualifierRuleType = selectedRecord.getAttribute("offerItemQualifierRuleType");
		if (offerItemQualifierRuleType == null) {
			offerItemQualifierRuleType = "NONE";
		}
		if (offerItemQualifierRuleType.equals("NONE")) {
			getDisplay().getQualifyForAnotherPromoRadio().setValue("NO");
			getDisplay().getReceiveFromAnotherPromoRadio().setValue("NO");
		} else if (offerItemQualifierRuleType.equals("QUALIFIER")) {
			getDisplay().getQualifyForAnotherPromoRadio().setValue("YES");
			getDisplay().getReceiveFromAnotherPromoRadio().setValue("NO");
		} else if (offerItemQualifierRuleType.equals("TARGET")) {
			getDisplay().getQualifyForAnotherPromoRadio().setValue("NO");
			getDisplay().getReceiveFromAnotherPromoRadio().setValue("YES");
		} else if (offerItemQualifierRuleType.equals("QUALIFIER_TARGET")) {
			getDisplay().getQualifyForAnotherPromoRadio().setValue("YES");
			getDisplay().getReceiveFromAnotherPromoRadio().setValue("YES");
		}
		
		Boolean combinable = selectedRecord.getAttributeAsBoolean("combinableWithOtherOffers");
		if (combinable == null) {
			combinable = true;
		}
		getDisplay().getOrderItemCombineRuleRadio().setValue(combinable?"YES":"NO");
		
		if (getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").getValue().equals("FIX_PRICE")) {
			getDisplay().getOrderCombineForm().disable();
			getDisplay().getFGCombineForm().disable();
			getDisplay().getOrderItemCombineForm().disable();
			getDisplay().getOrderCombineRuleRadio().setValue("NO");
			getDisplay().getFgCombineRuleRadio().setValue("NO");
			getDisplay().getOrderItemCombineRuleRadio().setValue("NO");
		}
	}
	
	public void disable() {
		getDisplay().getItemQualificationSectionView().setVisible(false);
		getDisplay().getFgSectionView().setVisible(false);
		getDisplay().getBogoQuestionLayout().setVisible(false);
		getDisplay().getItemTargetSectionView().setVisible(false);
		getDisplay().getRequiredItemsLayout().setVisible(false);
		getDisplay().getOrderItemLayout().setVisible(false);
		getDisplay().getOrderCombineForm().setVisible(false);
		getDisplay().getOrderCombineLabel().setVisible(false);
        getDisplay().getCustomerLayout().setVisible(false);
        getDisplay().getOrderSectionLayout().setVisible(false);
        getDisplay().getCustomerSection().setVisible(false);
        getDisplay().getOrderSection().setVisible(false);
	}
	
	public void initDeliveryType(String deliveryType, Record selectedRecord) {
		if (deliveryType.equals("CODE")) {
			getDisplay().getCodeField().enable();
			getDisplay().getCodeField().setValue(selectedRecord.getAttribute("offerCode.offerCode"));
		} else {
			getDisplay().getCodeField().disable();
			getDisplay().getCodeField().setValue("");
		}
		
	}
	
	public void initCustomerRule(String customerRule, Record selectedRecord) {
		if (customerRule.equals("CUSTOMER_RULE")) {
			String customerRules = selectedRecord.getAttribute("appliesToCustomerRules");
			if (customerRules == null || customerRules.trim().equals("")) {
				customerRuleIncompatible = false;
			}
			if (customerRuleIncompatible) {
				getDisplay().getRawCustomerForm().setVisible(true);
				getDisplay().getCustomerFilterBuilder().setVisible(false);
			} else {
				getDisplay().getRawCustomerForm().setVisible(false);
				getDisplay().getCustomerFilterBuilder().setVisible(true);
			}
		} else {
			getDisplay().getRawCustomerForm().setVisible(false);
			getDisplay().getCustomerFilterBuilder().setVisible(false);
		}
	}
	
	public void initOrderRule(String orderRule, Record selectedRecord) {
		if (orderRule.equals("ORDER_RULE")) {
			String orderRules = selectedRecord.getAttribute("appliesToOrderRules");
			if (orderRules == null || orderRules.trim().equals("")) {
				orderRuleIncompatible = false;
			}
			if (orderRuleIncompatible) {
				getDisplay().getRawOrderForm().setVisible(true);
				getDisplay().getOrderFilterBuilder().setVisible(false);
			} else {
				getDisplay().getRawOrderForm().setVisible(false);
				getDisplay().getOrderFilterBuilder().setVisible(true);
			}
		} else {
			getDisplay().getRawOrderForm().setVisible(false);
			getDisplay().getOrderFilterBuilder().setVisible(false);
		}
	}
	
	public void initFGRule(String fgRule, Record selectedRecord) {
		if (fgRule.equals("FG_RULE")) {
			String fgRules = selectedRecord.getAttribute("appliesToFulfillmentGroupRules");
			if (fgRules == null || fgRules.trim().equals("")) {
				fgRuleIncompatible = false;
			}
			if (fgRuleIncompatible) {
				getDisplay().getRawFGForm().setVisible(true);
				getDisplay().getFulfillmentGroupFilterBuilder().setVisible(false);
			} else {
				getDisplay().getRawFGForm().setVisible(false);
				getDisplay().getFulfillmentGroupFilterBuilder().setVisible(true);
			}
		} else {
			getDisplay().getRawFGForm().setVisible(false);
			getDisplay().getFulfillmentGroupFilterBuilder().setVisible(false);
		}
	}

	public void initItemRule(String itemRule) {
		if (itemRule.equals("ITEM_RULE")) {
			getDisplay().getNewItemBuilderLayout().setVisible(true);
		} else {
			getDisplay().getNewItemBuilderLayout().setVisible(false);
		}
	}

	public void initBogoRule(String bogoRule) {
		if (bogoRule.equals("YES")) {
			getDisplay().getRequiredItemsLayout().setVisible(true);
			getDisplay().getNewItemBuilderLayout().setVisible(true);
		} else {
			getDisplay().getRequiredItemsLayout().setVisible(false);
			getDisplay().getNewItemBuilderLayout().setVisible(false);
		}
	}
	
}
