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

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.FormItem;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferItemCriteriaListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferItemTargetCriteriaListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.promotion.OfferDisplay;
import org.broadleafcommerce.openadmin.client.translation.AdvancedCriteriaToMVELTranslator;
import org.broadleafcommerce.openadmin.client.translation.IncompatibleMVELTranslationException;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class OfferPresenterExtractor {

    private static Map<FilterType, String> MVELKEYWORDMAP = new HashMap<FilterType, String>();
    static {
        MVELKEYWORDMAP.put(FilterType.ORDER, "order");
        MVELKEYWORDMAP.put(FilterType.ORDER_ITEM, "discreteOrderItem");
        MVELKEYWORDMAP.put(FilterType.FULFILLMENT_GROUP, "fulfillmentGroup");
        MVELKEYWORDMAP.put(FilterType.CUSTOMER, "customer");
    }

    private static final AdvancedCriteriaToMVELTranslator TRANSLATOR = new AdvancedCriteriaToMVELTranslator();
    
    protected OfferPresenter presenter;
    protected StateManager stateManager = new StateManager();
    
    public OfferPresenterExtractor(OfferPresenter presenter) {
        this.presenter = presenter;
    }

    protected OfferDisplay getDisplay() {
        return presenter.getDisplay();
    }
    
    public void removeItemQualifer(final ItemBuilderDisplay builder) {
        if (builder.getRecord() != null) {
            presenter.getPresenterSequenceSetupManager().getDataSource("offerItemCriteriaDS").removeData(builder.getRecord(), new DSCallback() {
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    getDisplay().removeItemBuilder(builder);
                    stateManager.finishWatchedItem(CriteriaType.QUALIFIER);
                }
            });
        } else {
            getDisplay().removeItemBuilder(builder);
            stateManager.finishWatchedItem(CriteriaType.QUALIFIER);
        }
    }

    public void removeItemTarget(final ItemBuilderDisplay builder) {
        if (builder.getRecord() != null) {
            presenter.getPresenterSequenceSetupManager().getDataSource("offerItemTargetCriteriaDS").removeData(builder.getRecord(), new DSCallback() {
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    getDisplay().removeTargetItemBuilder(builder);
                    stateManager.finishWatchedItem(CriteriaType.TARGET);
                }
            });
        } else {
            getDisplay().removeTargetItemBuilder(builder);
        }
    }
    
    protected void setData(Record record, String fieldName, Object value, Map<String, Object> dirtyValues) {
        String attr = record.getAttribute(fieldName);
        String val = value==null?null:String.valueOf(value);
        if (attr != val && (attr == null || val == null || !attr.equals(val))) {
            record.setAttribute(fieldName, value);
            dirtyValues.put(fieldName, value);
        }
    }
    
    public void applyData(final Record selectedRecord) {
        try {
            final int index = getDisplay().getListDisplay().getGrid().getRecordIndex(selectedRecord);
            Record tempRecord = new Record();
            for (String attribute : selectedRecord.getAttributes()) {
                if (attribute.equals("_type")) {
                    tempRecord.setAttribute(attribute, selectedRecord.getAttributeAsStringArray(attribute));
                } else {
                    tempRecord.setAttribute(attribute, selectedRecord.getAttribute(attribute));
                }
            }
            final Map<String, Object> dirtyValues = new HashMap<String, Object>();
            
            setData(tempRecord, "totalitarianOffer", getDisplay().getRestrictRuleRadio().getValue().equals("YES"), dirtyValues);
            setData(tempRecord, "deliveryType",getDisplay().getDeliveryTypeRadio().getValue(), dirtyValues);
            if (getDisplay().getDeliveryTypeRadio().getValue().equals("CODE")) {
                setData(tempRecord, "offerCode.offerCode", getDisplay().getCodeField().getValue().toString().trim(), dirtyValues);
            }
            setData(tempRecord, "qualifyingItemSubTotal", getDisplay().getQualifyingItemSubTotal().getValue().toString().trim(), dirtyValues);
            final String type = getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("type").getValue().toString();
            
            extractCustomerData(tempRecord, dirtyValues);
            extractOrderData(tempRecord, type, dirtyValues);
            
            extractQualifierRuleType(tempRecord, dirtyValues);
            extractTargetRuleType(tempRecord, dirtyValues);
            extractFulfillmentGroupData(tempRecord, type, dirtyValues);
            
            for (FormItem formItem : getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getFields()) {
                setData(tempRecord, formItem.getName(), formItem.getValue(), dirtyValues);
            }

            stateManager.clear();

            extractTargetItemData(tempRecord, type, true, dirtyValues);
            extractQualifierData(tempRecord, type, true, dirtyValues);
            
            DSRequest requestProperties = new DSRequest();
            requestProperties.setAttribute("dirtyValues", dirtyValues);

            stateManager.setStateFinishedCallback(new StateFinishedCallback() {
                @Override
                public void finished() {
                    if (getDisplay().getListDisplay().getGrid().anySelected()) {
                        getDisplay().getListDisplay().getGrid().deselectAllRecords();
                    }
                    getDisplay().getListDisplay().getGrid().selectRecord(index);
                }
            });

            if (getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().validate() && getDisplay().getQualifyingItemSubTotalForm().validate()) {
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getDataSource().updateData(tempRecord, new DSCallback() {
                    public void execute(DSResponse response, Object rawData, DSRequest request) {
                        try {
                            stateManager.start();
                            extractTargetItemData(selectedRecord, type, false, dirtyValues);
                            extractQualifierData(selectedRecord, type, false, dirtyValues);
                            getDisplay().getDynamicFormDisplay().getSaveButton().disable();
                            getDisplay().getDynamicFormDisplay().getRefreshButton().disable();
                        } catch (IncompatibleMVELTranslationException e) {
                            SC.warn(e.getMessage());
                        }
                    }
                }, requestProperties);
            }
        } catch (IncompatibleMVELTranslationException e) {
            SC.warn(e.getMessage());
        }
    }
    
    protected void extractQualifierData(final Record selectedRecord, final String type, boolean isValidation, Map<String, Object> dirtyValues) throws IncompatibleMVELTranslationException {
        int count = 0;
        for (final ItemBuilderDisplay builder : getDisplay().getItemBuilderViews()) {
            if (builder.getDirty()) {
                count++;
            }
        }
        stateManager.setWatchedItem(CriteriaType.QUALIFIER, count);
        if ((getDisplay().getBogoRadio().getValue().equals("YES") && type.equals("ORDER_ITEM")) || getDisplay().getItemRuleRadio().getValue().equals("ITEM_RULE") && !type.equals("ORDER_ITEM")) {
            for (final ItemBuilderDisplay builder : getDisplay().getItemBuilderViews()) {
                if (builder.getDirty()) {
                    String temper = builder.getItemQuantity().getValue().toString();
                    Integer quantity = Integer.parseInt(temper);
                    String mvel;
                    if (builder.getIncompatibleMVEL()) {
                        mvel = builder.getRawItemTextArea().getValueAsString();
                    } else {
                        mvel = TRANSLATOR.createMVEL(MVELKEYWORDMAP.get(FilterType.ORDER_ITEM), builder.getItemFilterBuilder().getCriteria(), builder.getItemFilterBuilder().getDataSource());
                    }
                    if (!isValidation) {
                        if (builder.getRecord() != null) {
                            setData(builder.getRecord(), "quantity", quantity, dirtyValues);
                            setData(builder.getRecord(), "orderItemMatchRule", mvel, dirtyValues);
                            presenter.getPresenterSequenceSetupManager().getDataSource("offerItemCriteriaDS").updateData(builder.getRecord(), new DSCallback() {
                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                    builder.setDirty(false);
                                    stateManager.finishWatchedItem(CriteriaType.QUALIFIER);
                                }
                            });
                        } else {
                            final Record temp = new Record();
                            temp.setAttribute("quantity", quantity);
                            temp.setAttribute("orderItemMatchRule", mvel);
                            temp.setAttribute("_type", new String[]{presenter.getPresenterSequenceSetupManager().getDataSource("offerItemCriteriaDS").getDefaultNewEntityFullyQualifiedClassname()});
                            temp.setAttribute(OfferItemCriteriaListDataSourceFactory.foreignKeyName, presenter.getPresenterSequenceSetupManager().getDataSource("offerDS").getPrimaryKeyValue(selectedRecord));
                            temp.setAttribute("id", "");
                            presenter.getPresenterSequenceSetupManager().getDataSource("offerItemCriteriaDS").addData(temp, new DSCallback() {
                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                    builder.setDirty(false);
                                    builder.setRecord(temp);
                                    stateManager.finishWatchedItem(CriteriaType.QUALIFIER);
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
            setData(selectedRecord, "combinableWithOtherOffers", getDisplay().getOrderItemCombineRuleRadio().getValue().equals("YES"), dirtyValues);
        }
    }

    protected void extractTargetItemData(final Record selectedRecord, final String type, boolean isValidation, Map<String, Object> dirtyValues) throws IncompatibleMVELTranslationException {
        int count = 0;
        for (final ItemBuilderDisplay builder : getDisplay().getTargetItemBuilderViews()) {
            if (builder.getDirty()) {
                count++;
            }
        }
        stateManager.setWatchedItem(CriteriaType.TARGET, count);
        if (type.equals("ORDER_ITEM")) {
            for (final ItemBuilderDisplay builder : getDisplay().getTargetItemBuilderViews()) {
                if (builder.getDirty()) {
                    String temper = builder.getItemQuantity().getValue().toString();
                    Integer quantity = Integer.parseInt(temper);
                    String mvel;
                    if (builder.getIncompatibleMVEL()) {
                        mvel = builder.getRawItemTextArea().getValueAsString();
                    } else {
                        mvel = TRANSLATOR.createMVEL(MVELKEYWORDMAP.get(FilterType.ORDER_ITEM), builder.getItemFilterBuilder().getCriteria(), builder.getItemFilterBuilder().getDataSource());
                    }
                    if (!isValidation) {
                        if (builder.getRecord() != null) {
                            setData(builder.getRecord(), "quantity", quantity, dirtyValues);
                            setData(builder.getRecord(), "orderItemMatchRule", mvel, dirtyValues);
                            presenter.getPresenterSequenceSetupManager().getDataSource("offerItemTargetCriteriaDS").updateData(builder.getRecord(), new DSCallback() {
                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                    builder.setDirty(false);
                                    stateManager.finishWatchedItem(CriteriaType.TARGET);
                                }
                            });
                        } else {
                            final Record temp = new Record();
                            temp.setAttribute("quantity", quantity);
                            temp.setAttribute("orderItemMatchRule", mvel);
                            temp.setAttribute("_type", new String[]{presenter.getPresenterSequenceSetupManager().getDataSource("offerItemTargetCriteriaDS").getDefaultNewEntityFullyQualifiedClassname()});
                            temp.setAttribute(OfferItemTargetCriteriaListDataSourceFactory.foreignKeyName, presenter.getPresenterSequenceSetupManager().getDataSource("offerDS").getPrimaryKeyValue(selectedRecord));
                            temp.setAttribute("id", "");
                            presenter.getPresenterSequenceSetupManager().getDataSource("offerItemTargetCriteriaDS").addData(temp, new DSCallback() {
                                public void execute(DSResponse response, Object rawData, DSRequest request) {
                                    builder.setDirty(false);
                                    builder.setRecord(temp);
                                    stateManager.finishWatchedItem(CriteriaType.TARGET);
                                }
                            });
                        }
                    }
                }
            }
        } else {
            if (!isValidation) {
                ItemBuilderDisplay[] displays = new ItemBuilderDisplay[]{};
                displays = getDisplay().getTargetItemBuilderViews().toArray(displays);
                for (final ItemBuilderDisplay builder : displays) {
                    removeItemTarget(builder);
                }
            }
        }
    }

    protected void extractFulfillmentGroupData(final Record selectedRecord, final String type, Map<String, Object> dirtyValues) throws IncompatibleMVELTranslationException {
        if (type.equals("FULFILLMENT_GROUP")) {
            if (getDisplay().getFgRuleRadio().getValue().equals("FG_RULE")) {
                if (!presenter.initializer.fgRuleIncompatible) {
                    setData(selectedRecord, "appliesToFulfillmentGroupRules", TRANSLATOR.createMVEL(MVELKEYWORDMAP.get(FilterType.FULFILLMENT_GROUP), getDisplay().getFulfillmentGroupFilterBuilder().getCriteria(), getDisplay().getFulfillmentGroupFilterBuilder().getDataSource()), dirtyValues);
                } else {
                    setData(selectedRecord, "appliesToFulfillmentGroupRules", getDisplay().getRawFGTextArea().getValue(), dirtyValues);
                }
            } else {
                Object value = null;
                setData(selectedRecord, "appliesToFulfillmentGroupRules", value, dirtyValues);
                getDisplay().getFulfillmentGroupFilterBuilder().clearCriteria();
                getDisplay().getRawFGTextArea().setValue("");
            }
            setData(selectedRecord, "combinableWithOtherOffers", getDisplay().getFgCombineRuleRadio().getValue().equals("YES"), dirtyValues);
        } else {
            Object value = null;
            setData(selectedRecord, "appliesToFulfillmentGroupRules", value, dirtyValues);
            getDisplay().getFulfillmentGroupFilterBuilder().clearCriteria();
            getDisplay().getRawFGTextArea().setValue("");
        }
    }

    protected void extractTargetRuleType(final Record selectedRecord, Map<String, Object> dirtyValues) {
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
        setData(selectedRecord, "offerItemTargetRuleType", offerItemTargetRuleType, dirtyValues);
    }

    protected void extractQualifierRuleType(final Record selectedRecord, Map<String, Object> dirtyValues) {
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
        setData(selectedRecord, "offerItemQualifierRuleType", offerItemQualifierRuleType, dirtyValues);
    }

    protected void extractOrderData(final Record selectedRecord, String type, Map<String, Object> dirtyValues) throws IncompatibleMVELTranslationException {
        if (getDisplay().getOrderRuleRadio().getValue().equals("ORDER_RULE")) {
            if (!presenter.initializer.orderRuleIncompatible) {
                setData(selectedRecord, "appliesToOrderRules", TRANSLATOR.createMVEL(MVELKEYWORDMAP.get(FilterType.ORDER), getDisplay().getOrderFilterBuilder().getCriteria(), getDisplay().getOrderFilterBuilder().getDataSource()), dirtyValues);
            } else {
                setData(selectedRecord, "appliesToOrderRules", getDisplay().getRawOrderTextArea().getValue(), dirtyValues);
            }
        } else {
            Object value = null;
            setData(selectedRecord, "appliesToOrderRules", value, dirtyValues);
            getDisplay().getOrderFilterBuilder().clearCriteria();
            getDisplay().getRawOrderTextArea().setValue("");
        }
        if (type.equals("ORDER")) {
            setData(selectedRecord, "combinableWithOtherOffers", getDisplay().getOrderCombineRuleRadio().getValue().equals("YES"), dirtyValues);
        }
    }

    protected void extractCustomerData(final Record selectedRecord, Map<String, Object> dirtyValues) throws IncompatibleMVELTranslationException {
        if (getDisplay().getCustomerRuleRadio().getValue().equals("CUSTOMER_RULE")) {
            if (!presenter.initializer.customerRuleIncompatible) {
                setData(selectedRecord, "appliesToCustomerRules", TRANSLATOR.createMVEL(MVELKEYWORDMAP.get(FilterType.CUSTOMER), getDisplay().getCustomerFilterBuilder().getCriteria(), getDisplay().getCustomerFilterBuilder().getDataSource()), dirtyValues);
            } else {
                setData(selectedRecord, "appliesToCustomerRules", getDisplay().getRawCustomerTextArea().getValue(), dirtyValues);
            }
        } else {
            Object value = null;
            setData(selectedRecord, "appliesToCustomerRules", value, dirtyValues);
            getDisplay().getCustomerFilterBuilder().clearCriteria();
            getDisplay().getRawCustomerTextArea().setValue("");
        }
    }
}
