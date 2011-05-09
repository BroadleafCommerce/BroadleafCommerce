package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.MVELToAdvancedCriteriaTranslator;
import org.broadleafcommerce.gwt.admin.client.view.promotion.offer.ItemBuilderDisplay;
import org.broadleafcommerce.gwt.admin.client.view.promotion.offer.OfferDisplay;
import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class OfferPresenterInitializer {

	private static final MVELToAdvancedCriteriaTranslator TRANSLATOR = new MVELToAdvancedCriteriaTranslator();
	
	protected OfferPresenter presenter;
	protected boolean customerRuleIncompatible = false;
	protected boolean orderRuleIncompatible = false;
	protected boolean fgRuleIncompatible = false;
	protected DynamicEntityDataSource offerItemCriteriaDataSource;
	protected DynamicEntityDataSource orderItemDataSource;
	
	public OfferPresenterInitializer(OfferPresenter presenter, DynamicEntityDataSource offerItemCriteriaDataSource, DynamicEntityDataSource orderItemDataSource) {
		this.presenter = presenter;
		this.offerItemCriteriaDataSource = offerItemCriteriaDataSource;
		this.orderItemDataSource = orderItemDataSource;
	}
	
	protected OfferDisplay getDisplay() {
		return presenter.getDisplay();
	}
	
	public void initItemTargets(final Record selectedRecord) {
		String targetQuantity = selectedRecord.getAttribute("targetItemCriteria.receiveQuantity");
		if (targetQuantity != null) {
			getDisplay().getTargetItemBuilder().getItemQuantity().setValue(targetQuantity);
			String itemTargetRules = selectedRecord.getAttribute("targetItemCriteria.orderItemMatchRule");
			try {
				getDisplay().getTargetItemBuilder().getItemFilterBuilder().setCriteria(TRANSLATOR.createAdvancedCriteria(itemTargetRules, getDisplay().getTargetItemBuilder().getItemFilterBuilder().getDataSource()));
			} catch (IncompatibleMVELTranslationException e) {
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(AdminModule.ADMINMESSAGES.mvelTranslationProblem());
				getDisplay().getTargetItemBuilder().setIncompatibleMVEL(true);
				getDisplay().getTargetItemBuilder().getItemFilterBuilder().setVisible(false);
				getDisplay().getTargetItemBuilder().getRawItemForm().setVisible(true);
				getDisplay().getTargetItemBuilder().getRawItemTextArea().setValue(itemTargetRules);
			}
		}
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

	public void initBasicItems(final Record selectedRecord) {
		Boolean isTotalitarianOffer = selectedRecord.getAttributeAsBoolean("totalitarianOffer");
		getDisplay().getRestrictRuleRadio().setValue(isTotalitarianOffer?"YES":"NO");
		String deliveryType = selectedRecord.getAttribute("deliveryType");
		getDisplay().getDeliveryTypeRadio().setValue(deliveryType);
		initDeliveryType(deliveryType);
	}

	public void initCustomerCriteria(final Record selectedRecord) {
		customerRuleIncompatible = false;
		String customerRules = selectedRecord.getAttribute("appliesToCustomerRules");
		String customerRule = customerRules==null?"ALL":"CUSTOMER_RULE";
		if (customerRules == null) {
			getDisplay().getCustomerRuleRadio().setValue(customerRule);
		} else {
			getDisplay().getCustomerRuleRadio().setValue(customerRule);
			try {
				getDisplay().getCustomerFilterBuilder().setCriteria(TRANSLATOR.createAdvancedCriteria(customerRules, getDisplay().getCustomerFilterBuilder().getDataSource()));
			} catch (IncompatibleMVELTranslationException e) {
				customerRuleIncompatible = true;
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(AdminModule.ADMINMESSAGES.mvelTranslationProblem());
				getDisplay().getRawCustomerTextArea().setValue(customerRules);
			}
		}
		initCustomerRule(customerRule);
	}
	
	public void initFGCriteria(final Record selectedRecord) {
		fgRuleIncompatible = false;
		String fgRules = selectedRecord.getAttribute("appliesToFulfillmentGroupRules");
		String fgRule = fgRules==null?"ALL":"FG_RULE";
		if (fgRules == null) {
			getDisplay().getFgRuleRadio().setValue(fgRule);
		} else {
			getDisplay().getFgRuleRadio().setValue(fgRule);
			try {
				getDisplay().getFulfillmentGroupFilterBuilder().setCriteria(TRANSLATOR.createAdvancedCriteria(fgRules, getDisplay().getFulfillmentGroupFilterBuilder().getDataSource()));
			} catch (IncompatibleMVELTranslationException e) {
				fgRuleIncompatible = true;
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(AdminModule.ADMINMESSAGES.mvelTranslationProblem());
				getDisplay().getRawFGTextArea().setValue(fgRules);
			}
		}
		Boolean combinable = selectedRecord.getAttributeAsBoolean("combinableWithOtherOffers");
		if (combinable == null) {
			combinable = false;
		}
		getDisplay().getFgCombineRuleRadio().setValue(combinable?"YES":"NO");
		initFGRule(fgRule);
	}

	public void initOrderCriteria(final Record selectedRecord) {
		orderRuleIncompatible = false;
		String orderRules = selectedRecord.getAttribute("appliesToOrderRules");
		String orderRule = orderRules==null?"NONE":"ORDER_RULE";
		if (orderRules == null) {
			getDisplay().getOrderRuleRadio().setValue(orderRule);
		} else {
			getDisplay().getOrderRuleRadio().setValue(orderRule);
			try {
				getDisplay().getOrderFilterBuilder().setCriteria(TRANSLATOR.createAdvancedCriteria(orderRules, getDisplay().getOrderFilterBuilder().getDataSource()));
			} catch (IncompatibleMVELTranslationException e) {
				orderRuleIncompatible = true;
				GWT.log("Could not translate MVEL", e);
				BLCMain.MASTERVIEW.getStatus().setContents(AdminModule.ADMINMESSAGES.mvelTranslationProblem());
				getDisplay().getRawOrderTextArea().setValue(orderRules);
			}
		}
		Boolean combinable = selectedRecord.getAttributeAsBoolean("combinableWithOtherOffers");
		if (combinable == null) {
			combinable = false;
		}
		getDisplay().getOrderCombineRuleRadio().setValue(combinable?"YES":"NO");
		initOrderRule(orderRule);
	}

	public void initItemQualifiers(final Record selectedRecord, final String type) {
		Criteria relationshipCriteria = offerItemCriteriaDataSource.createRelationshipCriteria(offerItemCriteriaDataSource.getPrimaryKeyValue(selectedRecord));
		offerItemCriteriaDataSource.fetchData(relationshipCriteria, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				boolean isItemQualifierCriteria = false;
				if (response.getTotalRows() > 0) {
					for (Record record : response.getData()) {
						if (record.getAttributeAsInt("requiresQuantity") > 0) {
							isItemQualifierCriteria = true;
							break;
						}
					}
				}
				
				if (isItemQualifierCriteria) {
					if (type.equals("ORDER_ITEM")) {
						initBogoRule("YES");
					} else {
						initItemRule("YES");
					}
					getDisplay().getBogoRadio().setValue("YES");
					getDisplay().getItemRuleRadio().setValue("YES");
					getDisplay().removeAllItemBuilders();
					for (Record record : response.getData()) {
						if (record.getAttributeAsInt("requiresQuantity") > 0) {
							final ItemBuilderDisplay display = getDisplay().addItemBuilder(orderItemDataSource);
							display.setRecord(record);
							display.getItemQuantity().setValue(record.getAttributeAsInt("requiresQuantity"));
							try {
								display.getItemFilterBuilder().setCriteria(
									TRANSLATOR.createAdvancedCriteria(record.getAttribute("orderItemMatchRule"), offerItemCriteriaDataSource)
								);
							} catch (IncompatibleMVELTranslationException e) {
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
					getDisplay().addItemBuilder(orderItemDataSource);
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
	}
	
	public void initSectionBasedOnType(String sectionType, Record selectedRecord) {
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
		} else {
			getDisplay().getBogoQuestionLayout().setVisible(false);
			getDisplay().getItemTargetSectionView().setVisible(false);
			getDisplay().getRequiredItemsLayout().setVisible(true);
			getDisplay().getOrderItemLayout().setVisible(true);
		}
		if (sectionType.equals("ORDER")) {
			getDisplay().getOrderCombineForm().setVisible(true);
			getDisplay().getOrderCombineLabel().setVisible(true);
		} else {
			getDisplay().getOrderCombineForm().setVisible(false);
			getDisplay().getOrderCombineLabel().setVisible(false);
		}
		
		initBasicItems(selectedRecord);
		initCustomerCriteria(selectedRecord);
		initOrderCriteria(selectedRecord);
		initItemQualifiers(selectedRecord, sectionType);
		
		if (sectionType.equals("ORDER_ITEM")) {
			initItemTargets(selectedRecord);
		} else if (sectionType.equals("FULFILLMENT_GROUP")) {
			initFGCriteria(selectedRecord);
		}
	}
	
	public void initDeliveryType(String deliveryType) {
		if (deliveryType.equals("CODE")) {
			getDisplay().getCodeField().enable();
		} else {
			getDisplay().getCodeField().disable();
		}
	}
	
	public void initCustomerRule(String customerRule) {
		if (customerRule.equals("CUSTOMER_RULE")) {
			if (customerRuleIncompatible) {
				getDisplay().getRawCustomerForm().setVisible(true);
			} else {
				getDisplay().getCustomerFilterBuilder().setVisible(true);
			}
		} else {
			getDisplay().getRawCustomerForm().setVisible(false);
			getDisplay().getCustomerFilterBuilder().setVisible(false);
		}
	}
	
	public void initOrderRule(String orderRule) {
		if (orderRule.equals("ORDER_RULE")) {
			if (orderRuleIncompatible) {
				getDisplay().getRawOrderForm().setVisible(true);
			} else {
				getDisplay().getOrderFilterBuilder().setVisible(true);
			}
		} else {
			getDisplay().getRawOrderForm().setVisible(false);
			getDisplay().getOrderFilterBuilder().setVisible(false);
		}
	}
	
	public void initFGRule(String fgRule) {
		if (fgRule.equals("FG_RULE")) {
			if (fgRuleIncompatible) {
				getDisplay().getRawFGForm().setVisible(true);
			} else {
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
