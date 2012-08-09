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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.events.MouseMoveEvent;
import com.smartgwt.client.widgets.events.MouseMoveHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.form.events.FilterChangedEvent;
import com.smartgwt.client.widgets.form.events.FilterChangedHandler;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.admin.client.datasource.promotion.CustomerListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.FulfillmentGroupListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferItemCriteriaListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OrderItemListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OrderListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.promotion.OfferDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.AdditionalFilterEventManager;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterBuilderAdditionalEventHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterRestartCallback;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterStateRunnable;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

/**
 * @author jfischer
 */
public class OfferPresenter extends DynamicEntityPresenter implements Instantiable {

    protected Window currentHelp = null;
    protected OfferPresenterInitializer initializer;
    protected OfferPresenterExtractor extractor;
    protected AdditionalFilterEventManager additionalFilterEventManager = new AdditionalFilterEventManager();

    @Override
    protected void changeSelection(final Record selectedRecord) {
        additionalFilterEventManager.resetFilterState(new FilterStateRunnable() {
            @Override
            public void run(FilterRestartCallback cb) {
                rebindFormItems(selectedRecord);
                initializeDisplay(selectedRecord, cb);
            }
        });
    }

    protected void initializeDisplay(final Record selectedRecord, final FilterRestartCallback cb) {
        BLCMain.MASTERVIEW.clearStatus();
        getDisplay().getAdvancedButton().setSelected(false);
        getDisplay().getAdvancedButton().enable();
        getDisplay().getDeliveryTypeRadio().enable();
        getDisplay().getCustomerRuleRadio().enable();
        getDisplay().getOrderRuleRadio().enable();

        String sectionType = getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("type").getValue().toString();
        initializer.initSectionBasedOnType(sectionType, selectedRecord, cb);
    }

    protected void rebindFormItems(final Record selectedRecord) {
        //Since the form is built dynamically each time the grid selection changes, we have to re-bind the event
        getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("type").addChangedHandler(new ChangedHandler() {
            public void onChanged(final ChangedEvent event) {
                additionalFilterEventManager.resetFilterState(new FilterStateRunnable() {
                    @Override
                    public void run(FilterRestartCallback cb) {
                        String eventValue = event.getValue().toString();
                        initializer.initSectionBasedOnType(eventValue, selectedRecord, cb);
                    }
                });
            }
        });
        getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("discountType").addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String eventValue = event.getValue().toString();
                //FIX_PRICE promotions cannot combine with other promotions of the same type
                if (eventValue.equals("FIX_PRICE")) {
                    getDisplay().getOrderCombineForm().disable();
                    getDisplay().getFGCombineForm().disable();
                    getDisplay().getOrderItemCombineForm().disable();
                    getDisplay().getOrderCombineRuleRadio().setValue("NO");
                    getDisplay().getFgCombineRuleRadio().setValue("NO");
                    getDisplay().getOrderItemCombineRuleRadio().setValue("NO");
                } else {
                    getDisplay().getOrderCombineForm().enable();
                    getDisplay().getFGCombineForm().enable();
                    getDisplay().getOrderItemCombineForm().enable();
                    getDisplay().getOrderCombineRuleRadio().setValue("YES");
                    getDisplay().getFgCombineRuleRadio().setValue("YES");
                    getDisplay().getOrderItemCombineRuleRadio().setValue("YES");
                }
            }
        });
        FormItem endDate = getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getField("endDate");
        if (endDate != null) {
            endDate.addChangedHandler(new ChangedHandler() {
                @SuppressWarnings("deprecation")
                public void onChanged(ChangedEvent event) {
                    Date myDate = (Date) event.getValue();
                    if (myDate != null) {
                        event.getItem().setValue(myDate);
                    }
                }
            });
        }
    }

    @Override
    protected void addClicked() {
        Map<String, Object> initialValues = new HashMap<String, Object>();
        initialValues.put("name", BLCMain.getMessageManager().getString("offerNameDefault"));
        initialValues.put("type", "ORDER_ITEM");
        initialValues.put("value", 0);
        initialValues.put("stackable", true);
        initialValues.put("treatAsNewFormat", true);
        initialValues.put("deliveryType", "AUTOMATIC");
        initialValues.put("discountType", "PERCENT_OFF");
        initialValues.put("combinableWithOtherOffers", true);
        initialValues.put("_type", new String[]{((DynamicEntityDataSource) getDisplay().getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
        getDisplay().getListDisplay().getGrid().startEditingNew(initialValues);
    }

    @Override
    public void bind() {
        super.bind();
        getDisplay().getStepFGForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getStepItemForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getStepBogoForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getOrderCombineForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getOrderItemCombineForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getRawCustomerForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getRawOrderForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getRawFGForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getRestrictForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getCustomerObtainForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getWhichCustomerForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getOrderForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getReceiveFromAnotherPromoForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getQualifyForAnotherPromoForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getReceiveFromAnotherPromoTargetForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getQualifyForAnotherPromoTargetForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getFGCombineForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getQualifyingItemSubTotalForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
            }
        });
        getDisplay().getListDisplay().getRemoveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    BLCMain.MASTERVIEW.clearStatus();
                    getDisplay().getAdvancedButton().setSelected(false);
                    getDisplay().getAdvancedButton().disable();
                    getDisplay().getDeliveryTypeRadio().disable();
                    getDisplay().getCustomerRuleRadio().disable();
                    getDisplay().getOrderRuleRadio().disable();
                    initializer.disable();
                }
            }
        });
        getDisplay().getAdvancedButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (((ToolStripButton) event.getSource()).getSelected()) {
                    getPresenterSequenceSetupManager().getDataSource("offerDS").resetPermanentFieldVisibilityBasedOnType(lastSelectedRecord.getAttributeAsStringArray("_type"));
                    getPresenterSequenceSetupManager().getDataSource("offerDS").permanentlyHideFields("deliveryType", "offerItemQualifierRuleType", "offerItemTargetRuleType", "uses", "targetItemCriteria.id", "targetItemCriteria.quantity", "targetItemCriteria.orderItemMatchRule");
                    getDisplay().getAdvancedItemCriteria().setVisible(true);
                    getDisplay().getAdvancedItemCriteriaTarget().setVisible(true);
                    getDisplay().getRestrictionSectionView().setVisible(true);
                } else {
                    getPresenterSequenceSetupManager().getDataSource("offerDS").resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority", "startDate", "endDate");
                    getDisplay().getAdvancedItemCriteria().setVisible(false);
                    getDisplay().getAdvancedItemCriteriaTarget().setVisible(false);
                    getDisplay().getRestrictionSectionView().setVisible(false);
                }
                @SuppressWarnings("rawtypes")
                Map values = getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().getValues();
                Object[] keys = values.keySet().toArray();
                for (Object key : keys) {
                    if (key.toString().equals("__ref")) {
                        values.remove(key);
                    }
                }
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().buildFields(getPresenterSequenceSetupManager().getDataSource("offerDS"), true, true, false, lastSelectedRecord);
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(lastSelectedRecord);
                getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().setValues(values);
                rebindFormItems(lastSelectedRecord);
            }
        });
        selectionChangedHandlerRegistration.removeHandler();
        getDisplay().getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState()) {
                    if (!selectedRecord.equals(lastSelectedRecord)) {
                        lastSelectedRecord = selectedRecord;
                        if (selectedRecord.getAttributeAsStringArray("_type") == null) {
                            formPresenter.disable();
                            getDisplay().getListDisplay().getRemoveButton().disable();
                        } else {
                            formPresenter.setStartState();
                            getPresenterSequenceSetupManager().getDataSource("offerDS").resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority", "startDate", "endDate");
                            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().buildFields(getDisplay().getListDisplay().getGrid().getDataSource(), true, true, false, selectedRecord);
                            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
                            getDisplay().getListDisplay().getRemoveButton().enable();
                        }
                        changeSelection(selectedRecord);
                    }
                }
            }
        });
        getSaveButtonHandlerRegistration().removeHandler();
        getDisplay().getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    extractor.applyData(lastSelectedRecord);
                }
            }
        });
        getDisplay().getHelpButtonType().addMouseMoveHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                if (currentHelp == null) {
                    currentHelp = createHelpWin(
                            BLCMain.getMessageManager().getString("offerObtainSettingsHelpTitle"),
                            BLCMain.getMessageManager().getString("offerObtainSettingsHelpContent"),
                            true, 300, 200, getDisplay().getHelpButtonType().getAbsoluteLeft() + 26, getDisplay().getHelpButtonType().getAbsoluteTop()
                    );
                    currentHelp.show();
                }
            }
        });
        getDisplay().getHelpButtonType().addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                if (currentHelp != null) {
                    currentHelp.destroy();
                    currentHelp = null;
                }
            }
        });
        getDisplay().getDeliveryTypeRadio().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String deliveryType = event.getValue().toString();
                initializer.initDeliveryType(deliveryType, lastSelectedRecord);
            }
        });
        getDisplay().getCustomerRuleRadio().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String customerRule = event.getValue().toString();
                initializer.initCustomerRule(customerRule, lastSelectedRecord);
            }
        });
        getDisplay().getFgRuleRadio().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String fgRule = event.getValue().toString();
                initializer.initFGRule(fgRule, lastSelectedRecord);
            }
        });
        getDisplay().getItemRuleRadio().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String itemRule = event.getValue().toString();
                initializer.initItemRule(itemRule);
            }
        });
        getDisplay().getOrderRuleRadio().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String orderRule = event.getValue().toString();
                initializer.initOrderRule(orderRule, lastSelectedRecord);
            }
        });
        getDisplay().getHelpButtonBogo().addMouseMoveHandler(new MouseMoveHandler() {
            public void onMouseMove(MouseMoveEvent event) {
                if (currentHelp == null) {
                    currentHelp = createHelpWin(
                            BLCMain.getMessageManager().getString("bogoHelpTitle"),
                            BLCMain.getMessageManager().getString("bogoHelpContent"),
                            true, 300, 200, getDisplay().getHelpButtonBogo().getAbsoluteLeft() + 26, getDisplay().getHelpButtonBogo().getAbsoluteTop()
                    );
                    currentHelp.show();
                }
            }
        });
        getDisplay().getHelpButtonBogo().addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                if (currentHelp != null) {
                    currentHelp.destroy();
                    currentHelp = null;
                }
            }
        });
        getDisplay().getBogoRadio().addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                String bogoRule = event.getValue().toString();
                initializer.initBogoRule(bogoRule);
            }
        });
        
        getDisplay().getAddItemButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    final ItemBuilderDisplay display = getDisplay().addItemBuilder(getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"));
                    bindItemBuilderEvents(display);
                    display.setDirty(true);
                    getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                    getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                }
            }
        });
        for (final ItemBuilderDisplay display : getDisplay().getItemBuilderViews()) {
            bindItemBuilderEvents(display);
        }
        getDisplay().getCustomerFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            public void onFilterChanged(FilterChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getRawCustomerTextArea().setAttribute("dirty", true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getCustomerFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getRawCustomerTextArea().setAttribute("dirty", true);
            }
        });
        getDisplay().getOrderFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            public void onFilterChanged(FilterChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getRawOrderTextArea().setAttribute("dirty", true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getOrderFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getRawOrderTextArea().setAttribute("dirty", true);
            }
        });
        getDisplay().getFulfillmentGroupFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            public void onFilterChanged(FilterChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getRawFGTextArea().setAttribute("dirty", true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getFulfillmentGroupFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getRawFGTextArea().setAttribute("dirty", true);
            }
        });
        getDisplay().getTargetItemBuilder().getRawItemForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getTargetItemBuilder().setDirty(true);
            }
        });
        getDisplay().getTargetItemBuilder().getItemForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getTargetItemBuilder().setDirty(true);
            }
        });
        getDisplay().getTargetItemBuilder().getItemFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            public void onFilterChanged(FilterChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getTargetItemBuilder().setDirty(true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getTargetItemBuilder().getItemFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                getDisplay().getTargetItemBuilder().setDirty(true);
            }
        });
        getDisplay().getDynamicFormDisplay().getRefreshButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    additionalFilterEventManager.resetFilterState(new FilterStateRunnable() {
                        @Override
                        public void run(FilterRestartCallback cb) {
                            initializeDisplay(lastSelectedRecord, cb);
                        }
                    });
                }
            }
        });
        getDisplay().getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                initializer.disable();
            }
        });
    }

    protected void bindItemBuilderEvents(final ItemBuilderDisplay display) {
        display.getRemoveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (getDisplay().getItemBuilderViews().size() > 1) {
                    additionalFilterEventManager.removeFilterBuilderAdditionalEventHandler(display.getItemFilterBuilder());
                    extractor.removeItemQualifer(display);
                }
            }
        });
        display.getRawItemForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                display.setDirty(true);
            }
        });
        display.getItemForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                display.setDirty(true);
            }
        });
        display.getItemFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            public void onFilterChanged(FilterChangedEvent event) {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                display.setDirty(true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(display.getItemFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                getDisplay().getDynamicFormDisplay().getSaveButton().enable();
                getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
                display.setDirty(true);
            }
        });
    }

    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerDS", new OfferListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerOrderDS", new OrderListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerOrderItemDS", new OrderItemListDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource result) {
                ((DynamicEntityDataSource) result).permanentlyShowFields("product.id", "category.id", "sku.id");
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerFGDS", new FulfillmentGroupListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerCustomerDS", new CustomerListDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource result) {
                ((DynamicEntityDataSource) result).permanentlyShowFields("id");
                ((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("offerDS")).permanentlyHideFields("appliesToOrderRules", "appliesToCustomerRules", "appliesToFulfillmentGroupRules", "id");
                ((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("offerDS")).resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority", "startDate", "endDate");
                setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("offerDS"), getPresenterSequenceSetupManager().getDataSource("offerOrderDS"), getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"), getPresenterSequenceSetupManager().getDataSource("offerFGDS"), result);
                ((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("offerDS")).setupGridFields(new String[]{"name"}, new Boolean[]{true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerItemCriteriaDS", new OfferItemCriteriaListDataSourceFactory(), new AsyncCallbackAdapter() {
            public void onSetupSuccess(DataSource result) {
                initializer = new OfferPresenterInitializer(OfferPresenter.this, (DynamicEntityDataSource) result, getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"));
                extractor = new OfferPresenterExtractor(OfferPresenter.this);
            }
        }));
    }

    @Override
    public OfferDisplay getDisplay() {
        return (OfferDisplay) display;
    }

    public Window createHelpWin(String title, String content, boolean autoSizing, int width, int height, int left, int top) {
        Label label = new Label(content);
        label.setWidth100();
        label.setHeight100();
        label.setPadding(5);
        label.setValign(VerticalAlignment.TOP);

        Window window = new Window();
        window.setAutoSize(autoSizing);
        window.setTitle(title);
        window.setWidth(width);
        window.setHeight(height);
        window.setLeft(left);
        window.setCanDragReposition(true);
        window.setCanDragResize(true);
        window.addItem(label);
        window.setShowCloseButton(false);
        window.setShowMinimizeButton(false);
        window.setTop(top - window.getHeight());

        return window;
    }

}
