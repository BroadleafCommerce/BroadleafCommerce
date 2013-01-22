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

package org.broadleafcommerce.admin.client.presenter.promotion;

import org.broadleafcommerce.admin.client.datasource.promotion.CustomerListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.FulfillmentGroupListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferItemCriteriaListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferItemTargetCriteriaListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OfferListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OrderItemListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.promotion.OrderListDataSourceFactory;
import org.broadleafcommerce.admin.client.view.promotion.OfferDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.AdditionalFilterEventManager;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterBuilderAdditionalEventHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterRestartCallback;
import org.broadleafcommerce.openadmin.client.view.dynamic.FilterStateRunnable;
import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jfischer
 */
public class OfferPresenter extends DynamicEntityPresenter implements Instantiable {

    protected Window currentHelp = null;
    protected OfferPresenterInitializer initializer;
    protected OfferPresenterExtractor extractor;
    protected AdditionalFilterEventManager additionalFilterEventManager = new AdditionalFilterEventManager();
    protected List<String> permanentlyHideFieldsList=new ArrayList<String>();
    protected List<String> resetVisibilityOnlyList=new ArrayList<String>();
    
    
    public OfferPresenter() {
        resetVisibilityOnlyList.addAll(Arrays.asList(new String[]{"name", "description", "type", "discountType","maxUsesPerCustomer", "maxUsesPerOrder", "value", "priority", "startDate", "endDate"}));
        permanentlyHideFieldsList.addAll(Arrays.asList(new String[]{"deliveryType", "offerItemQualifierRuleType", "offerItemTargetRuleType", "targetItemCriteria.id", "targetItemCriteria.quantity", "targetItemCriteria.orderItemMatchRule"}));
    }
    @Override
    public void changeSelection(final Record selectedRecord) {
        additionalFilterEventManager.resetFilterState(new FilterStateRunnable() {
            @Override
            public void run(FilterRestartCallback cb) {
                rebindFormItems(selectedRecord);
                initializeDisplay(selectedRecord, cb);
            }
        });
    }

    protected void initializeDisplay(final Record selectedRecord, final FilterRestartCallback cb) {
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
            @Override
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
            @Override
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
                @Override
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
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getStepItemForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getStepBogoForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getOrderCombineForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getOrderItemCombineForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getRawCustomerForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getRawOrderForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getRawFGForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getRestrictForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getCustomerObtainForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getWhichCustomerForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getOrderForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getReceiveFromAnotherPromoForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getQualifyForAnotherPromoForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getReceiveFromAnotherPromoTargetForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getQualifyForAnotherPromoTargetForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getFGCombineForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getQualifyingItemSubTotalForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
            }
        });
        getDisplay().getListDisplay().getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
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
           
            @Override
            public void onClick(ClickEvent event) {
                if (((ToolStripButton) event.getSource()).getSelected()) {
                    getPresenterSequenceSetupManager().getDataSource("offerDS").resetPermanentFieldVisibilityBasedOnType(lastSelectedRecord.getAttributeAsStringArray("_type"));
                    getPresenterSequenceSetupManager().getDataSource("offerDS").permanentlyHideFields(getPermanentlyHideFieldsList().toArray(new String[getPermanentlyHideFieldsList().size()]));
                    getDisplay().getAdvancedItemCriteria().setVisible(true);
                    getDisplay().getAdvancedItemCriteriaTarget().setVisible(true);
                    getDisplay().getRestrictionSectionView().setVisible(true);
                } else {
                    getPresenterSequenceSetupManager().getDataSource("offerDS").resetVisibilityOnly(getResetVisibilityOnlyList().toArray(new String[getResetVisibilityOnlyList().size()]));
                    getDisplay().getAdvancedItemCriteria().setVisible(false);
                    getDisplay().getAdvancedItemCriteriaTarget().setVisible(false);
                    getDisplay().getRestrictionSectionView().setVisible(false);
                }
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
            @Override
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
                            getPresenterSequenceSetupManager().getDataSource("offerDS").resetVisibilityOnly(getResetVisibilityOnlyList().toArray(new String[getResetVisibilityOnlyList().size()]));
                            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().buildFields(getDisplay().getListDisplay().getGrid().getDataSource(), true, true, false, selectedRecord);
                            getDisplay().getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
                            getDisplay().getListDisplay().getRemoveButton().enable();
                        }
                        changeSelectionWrapper(selectedRecord);
                        for (Map.Entry<String, SubPresentable> subPresentable : subPresentables.entrySet()) {
                            //this is only suitable when no callback is required for the load - which is most cases
                            subPresentable.getValue().setStartState();
                            subPresentable.getValue().load(selectedRecord, (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource());
                        }
                    }
                }
            }
        });
        getSaveButtonHandlerRegistration().removeHandler();
        getDisplay().getDynamicFormDisplay().getSaveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    extractor.applyData(lastSelectedRecord);
                }
            }
        });
        getDisplay().getHelpButtonType().addMouseMoveHandler(new MouseMoveHandler() {
            @Override
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
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (currentHelp != null) {
                    currentHelp.destroy();
                    currentHelp = null;
                }
            }
        });
        getDisplay().getDeliveryTypeRadio().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String deliveryType = event.getValue().toString();
                initializer.initDeliveryType(deliveryType, lastSelectedRecord);
            }
        });
        getDisplay().getCustomerRuleRadio().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String customerRule = event.getValue().toString();
                initializer.initCustomerRule(customerRule, lastSelectedRecord);
            }
        });
        getDisplay().getFgRuleRadio().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String fgRule = event.getValue().toString();
                initializer.initFGRule(fgRule, lastSelectedRecord);
            }
        });
        getDisplay().getItemRuleRadio().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String itemRule = event.getValue().toString();
                initializer.initItemRule(itemRule);
            }
        });
        getDisplay().getOrderRuleRadio().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String orderRule = event.getValue().toString();
                initializer.initOrderRule(orderRule, lastSelectedRecord);
            }
        });
        getDisplay().getHelpButtonBogo().addMouseMoveHandler(new MouseMoveHandler() {
            @Override
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
            @Override
            public void onMouseOut(MouseOutEvent event) {
                if (currentHelp != null) {
                    currentHelp.destroy();
                    currentHelp = null;
                }
            }
        });
        getDisplay().getBogoRadio().addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                String bogoRule = event.getValue().toString();
                initializer.initBogoRule(bogoRule);
            }
        });
        
        getDisplay().getAddItemButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    final ItemBuilderDisplay display = getDisplay().addItemBuilder(getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"));
                    bindItemBuilderEvents(display, true);
                    display.setDirty(true);
                    setDirtyState();
                }
            }
        });
        for (final ItemBuilderDisplay display : getDisplay().getItemBuilderViews()) {
            bindItemBuilderEvents(display, true);
        }
        getDisplay().getCustomerFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                setDirtyState();
                getDisplay().getRawCustomerTextArea().setAttribute("dirty", true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getCustomerFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                setDirtyState();
                getDisplay().getRawCustomerTextArea().setAttribute("dirty", true);
            }
        });
        getDisplay().getOrderFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                setDirtyState();
                getDisplay().getRawOrderTextArea().setAttribute("dirty", true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getOrderFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                setDirtyState();
                getDisplay().getRawOrderTextArea().setAttribute("dirty", true);
            }
        });
        getDisplay().getFulfillmentGroupFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                setDirtyState();
                getDisplay().getRawFGTextArea().setAttribute("dirty", true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(getDisplay().getFulfillmentGroupFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                setDirtyState();
                getDisplay().getRawFGTextArea().setAttribute("dirty", true);
            }
        });

        getDisplay().getTargetAddItemButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    final ItemBuilderDisplay display = getDisplay().addTargetItemBuilder(getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"));
                    bindItemBuilderEvents(display, true);
                    display.setDirty(true);
                    setDirtyState();
                }
            }
        });
        for (final ItemBuilderDisplay display : getDisplay().getTargetItemBuilderViews()) {
            bindItemBuilderEvents(display, true);
        }
        getDisplay().getDynamicFormDisplay().getRefreshButton().addClickHandler(new ClickHandler() {
            @Override
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

    protected void setDirtyState() {
        getDisplay().getDynamicFormDisplay().getSaveButton().enable();
        getDisplay().getDynamicFormDisplay().getRefreshButton().enable();
    }

    protected void bindItemBuilderEvents(final ItemBuilderDisplay display, final boolean isTarget) {
        display.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                boolean greaterThanOne;
                if (isTarget) {
                    greaterThanOne = getDisplay().getTargetItemBuilderViews().size() > 1;
                } else {
                    greaterThanOne = getDisplay().getItemBuilderViews().size() > 1;
                }
                if (greaterThanOne) {
                    additionalFilterEventManager.removeFilterBuilderAdditionalEventHandler(display.getItemFilterBuilder());
                    if (isTarget) {
                        extractor.removeItemTarget(display);
                    } else {
                        extractor.removeItemQualifer(display);
                    }
                }
            }
        });
        display.getRawItemForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
                display.setDirty(true);
            }
        });
        display.getItemForm().addItemChangedHandler(new ItemChangedHandler() {
            @Override
            public void onItemChanged(ItemChangedEvent event) {
                setDirtyState();
                display.setDirty(true);
            }
        });
        display.getItemFilterBuilder().addFilterChangedHandler(new FilterChangedHandler() {
            @Override
            public void onFilterChanged(FilterChangedEvent event) {
                setDirtyState();
                display.setDirty(true);
            }
        });
        additionalFilterEventManager.addFilterBuilderAdditionalEventHandler(display.getItemFilterBuilder(), new FilterBuilderAdditionalEventHandler() {
            @Override
            public void onAdditionalChangeEvent() {
                setDirtyState();
                display.setDirty(true);
            }
        });
    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerDS", new OfferListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerOrderDS", new OrderListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerOrderItemDS", new OrderItemListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((DynamicEntityDataSource) result).permanentlyShowFields("product.id", "category.id", "sku.id");
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerFGDS", new FulfillmentGroupListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerCustomerDS", new CustomerListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((DynamicEntityDataSource) result).permanentlyShowFields("id"); 
                getPresenterSequenceSetupManager().getDataSource("offerDS").permanentlyHideFields("appliesToOrderRules", "appliesToCustomerRules", "appliesToFulfillmentGroupRules", "id");
                getPresenterSequenceSetupManager().getDataSource("offerDS").resetVisibilityOnly("name", "description", "type", "discountType", "value", "priority", "startDate", "endDate");
                setupDisplayItems(getPresenterSequenceSetupManager().getDataSource("offerDS"), getPresenterSequenceSetupManager().getDataSource("offerOrderDS"), getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"), getPresenterSequenceSetupManager().getDataSource("offerFGDS"), result);
                ((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("offerDS")).setupGridFields(new String[]{"name"}, new Boolean[]{true});
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerItemCriteriaDS", new OfferItemCriteriaListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerItemTargetCriteriaDS", new OfferItemTargetCriteriaListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                initializer = new OfferPresenterInitializer(OfferPresenter.this, getPresenterSequenceSetupManager().getDataSource("offerItemCriteriaDS"), (DynamicEntityDataSource) result, getPresenterSequenceSetupManager().getDataSource("offerOrderItemDS"));
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
    
    public List<String> getResetVisibilityOnlyList() {
        return resetVisibilityOnlyList;
    }
    public List<String> getPermanentlyHideFieldsList() {
        return permanentlyHideFieldsList;
    }
}
