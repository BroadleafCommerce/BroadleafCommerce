package org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer;

import org.broadleafcommerce.gwt.admin.client.datasource.promotion.offer.OfferItemCriteriaListDataSourceFactory;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.AdvancedCriteriaToMVELTranslator;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.FilterType;
import org.broadleafcommerce.gwt.admin.client.presenter.promotion.offer.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.gwt.admin.client.view.promotion.offer.ItemBuilderDisplay;
import org.broadleafcommerce.gwt.admin.client.view.promotion.offer.OfferDisplay;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;

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
			
			extractCustomerData(selectedRecord);
			extractOrderData(selectedRecord);
			
			final String type = getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("type").getValue().toString();
			
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
		if ((getDisplay().getBogoRadio().getValue().equals("YES") && type.equals("ORDER_ITEM")) || getDisplay().getItemRuleRadio().equals("ITEM_RULE") && !type.equals("ORDER_ITEM")) {
			for (final ItemBuilderDisplay builder : getDisplay().getItemBuilderViews()) {
				if (builder.getDirty()) {
					Integer quantity = (Integer) builder.getItemQuantity().getValue();
					String mvel;
					if (builder.getIncompatibleMVEL()) {
						mvel = builder.getRawItemTextArea().getValueAsString();
					} else {
						mvel = TRANSLATOR.createMVEL(builder.getItemFilterBuilder().getCriteria(), FilterType.ORDER_ITEM, builder.getItemFilterBuilder().getDataSource());
					}
					if (!isValidation) {
						if (builder.getRecord() != null) {
							builder.getRecord().setAttribute("requiresQuantity", quantity);
							builder.getRecord().setAttribute("receiveQuantity", 0);
							builder.getRecord().setAttribute("orderItemMatchRule", mvel);
							presenter.offerItemCriteriaDataSource.updateData(builder.getRecord(), new DSCallback() {
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									builder.setDirty(false);
								}
							});
						} else {
							final Record temp = new Record();
							temp.setAttribute("requiresQuantity", quantity);
							temp.setAttribute("receiveQuantity", 0);
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
	}

	protected void extractFulfillmentGroupData(final Record selectedRecord, final String type) throws IncompatibleMVELTranslationException {
		if (type.equals("FULFILLMENT_GROUP")) {
			if (getDisplay().getOrderRuleRadio().getValue().equals("FG_RULE")) {
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
			Integer quantity = (Integer) getDisplay().getTargetItemBuilder().getItemQuantity().getValue();
			String mvel;
			if (getDisplay().getTargetItemBuilder().getIncompatibleMVEL()) {
				mvel = getDisplay().getTargetItemBuilder().getRawItemTextArea().getValueAsString();
			} else {
				mvel = TRANSLATOR.createMVEL(getDisplay().getTargetItemBuilder().getItemFilterBuilder().getCriteria(), FilterType.ORDER_ITEM, getDisplay().getTargetItemBuilder().getItemFilterBuilder().getDataSource());
			}
			selectedRecord.setAttribute("targetItemCriteria.receiveQuantity", quantity);
			selectedRecord.setAttribute("targetItemCriteria.requiresQuantity", 0);
			selectedRecord.setAttribute("targetItemCriteria.orderItemMatchRule", mvel);
		} else {
			Object value = null;
			selectedRecord.setAttribute("targetItemCriteria", 0);
			selectedRecord.setAttribute("targetItemCriteria.orderItemMatchRule", value);
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

	protected void extractOrderData(final Record selectedRecord) throws IncompatibleMVELTranslationException {
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
