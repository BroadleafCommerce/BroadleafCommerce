/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.admin.client.presenter.order;

import org.broadleafcommerce.admin.client.AdminExporterType;
import org.broadleafcommerce.admin.client.datasource.order.BundledOrderItemListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.CountryListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.DiscreteOrderItemFeePriceDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.FulfillmentGroupAdjustmentListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.FulfillmentGroupListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OfferCodeListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OrderAdjustmentListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OrderItemAdjustmentListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OrderItemListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OrderItemPriceDetailAdjustmentListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OrderItemPriceDetailListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.OrderListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.PaymentAdditionalAttributesDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.PaymentInfoListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.PaymentLogListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.PaymentResponseItemListDataSourceFactory;
import org.broadleafcommerce.admin.client.datasource.order.StateListDataSourceFactory;
import org.broadleafcommerce.admin.client.service.AppServices;
import org.broadleafcommerce.admin.client.view.dialog.ExportListSelectionDialog;
import org.broadleafcommerce.admin.client.view.order.OrderDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.AdminExporterDTO;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.CreateBasedListStructurePresenter;
import org.broadleafcommerce.openadmin.client.presenter.structure.SimpleMapStructurePresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class OrderPresenter extends DynamicEntityPresenter implements Instantiable {
    
    protected OrderItemPresenter orderItemPresenter;
    protected SubPresentable fulfillmentGroupPresenter;
    protected SubPresentable paymentInfoPresenter;
    protected SubPresentable additionalPaymentAttributesPresenter;
    protected SubPresentable offerCodePresenter;
    protected SubPresentable orderAdjustmentPresenter;
    protected SubPresentable orderItemAdjustmentPresenter;
    protected CreateBasedListStructurePresenter orderItemPriceDetailPresenter;
    protected SubPresentable fulfillmentGroupAdjustmentPresenter;
    protected SubPresentable feesPresenter;
    protected SubPresentable paymentResponsePresenter;
    protected SubPresentable paymentLogPresenter;
    protected HashMap<String, Object> library = new HashMap<String, Object>(10);
    protected List<SubPresentable> subPresentables = new ArrayList<SubPresentable>();;
    protected HandlerRegistration extendedFetchDataHandlerRegistration;
    
    @Override
    protected void changeSelection(Record selectedRecord) {
        orderItemPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderDS"), null);
        fulfillmentGroupPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderDS"), null);
        paymentInfoPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderDS"), null);
        offerCodePresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderDS"), null);
        orderAdjustmentPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderDS"), null);
    }
    
    @Override
    public void bind() {
        super.bind();
        
        orderItemPresenter.bind();
        fulfillmentGroupPresenter.bind();
        paymentInfoPresenter.bind();
        additionalPaymentAttributesPresenter.bind();
        offerCodePresenter.bind();
        orderAdjustmentPresenter.bind();
        orderItemAdjustmentPresenter.bind();
        orderItemPriceDetailPresenter.bind();
        fulfillmentGroupAdjustmentPresenter.bind();
        feesPresenter.bind();
        paymentResponsePresenter.bind();
        paymentLogPresenter.bind();
        
        subPresentables.add(orderItemPresenter);
        subPresentables.add(fulfillmentGroupPresenter);
        subPresentables.add(paymentInfoPresenter);
        subPresentables.add(additionalPaymentAttributesPresenter);
        subPresentables.add(offerCodePresenter);
        subPresentables.add(orderAdjustmentPresenter);
        subPresentables.add(orderItemAdjustmentPresenter);
        subPresentables.add(fulfillmentGroupAdjustmentPresenter);
        subPresentables.add(feesPresenter);
        subPresentables.add(paymentResponsePresenter);
        subPresentables.add(paymentLogPresenter);
        
        selectionChangedHandlerRegistration.removeHandler();
        display.getListDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                            
                if (event.getState()) {
                    for (SubPresentable sp : subPresentables) {
                        sp.enable();
                    }
                    
                    if (!selectedRecord.equals(lastSelectedRecord)) {
                        lastSelectedRecord = selectedRecord;
                        if (selectedRecord.getAttributeAsStringArray("_type") == null){
                            formPresenter.disable();
                        } else {
                            formPresenter.setStartState();
                            getPresenterSequenceSetupManager().getDataSource("orderDS").resetPermanentFieldVisibilityBasedOnType(selectedRecord.getAttributeAsStringArray("_type"));
                            display.getDynamicFormDisplay().getFormOnlyDisplay().buildFields(display.getListDisplay().getGrid().getDataSource(), false, false, false, selectedRecord);
                            display.getDynamicFormDisplay().getFormOnlyDisplay().getForm().editRecord(selectedRecord);
                        }
                        changeSelection(selectedRecord);
                    }
                    
                    for (SubPresentable sp : subPresentables) {
                        sp.setReadOnly(true);
                    }
                }
            }
        });
        getDisplay().getPaymentInfoDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState()) {
                    additionalPaymentAttributesPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("paymentInfoDS"), null);
                    
                    String id = getPresenterSequenceSetupManager().getDataSource("paymentInfoDS").getPrimaryKeyValue(selectedRecord);
                    getDisplay().getPaymentResponseDisplay().getGrid().fetchData(new Criteria("paymentInfoReferenceNumber", selectedRecord.getAttributeAsString("referenceNumber")));
                    getDisplay().getPaymentLogDisplay().getGrid().fetchData(new Criteria("paymentInfoReferenceNumber", selectedRecord.getAttributeAsString("referenceNumber")));
                }
            }
        });
        getDisplay().getOrderItemsDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState()) {
                    orderItemAdjustmentPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderItemDS"), null);
                    feesPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("discreteOrderItemFeePriceDS"), null);
                    orderItemPriceDetailPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("orderItemDS"), null);
                }
            }
        });
        getDisplay().getFulfillmentGroupDisplay().getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState()) {
                    fulfillmentGroupAdjustmentPresenter.load(selectedRecord, getPresenterSequenceSetupManager().getDataSource("fulfillmentGroupDS"), null);
                }
            }
        });
        getDisplay().getExportOrdersButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AppServices.EXPORT.getExporters(AdminExporterType.ORDER.toString(), new AsyncCallback<List<AdminExporterDTO>>() {
                    @Override
                    public void onSuccess(final List<AdminExporterDTO> result) {
                        if (result == null || result.size() == 0) {
                            SC.say(BLCMain.getMessageManager().getString("noOrderExporters"));
                        } else {
                            ExportListSelectionDialog exportSelectionDialog = new ExportListSelectionDialog();
                            exportSelectionDialog.search(BLCMain.getMessageManager().getString("selectExporterTitle"), result);
                        }
                    }
                    
                    @Override
                    public void onFailure(Throwable caught) {
                        // Do nothing
                    }
                });
            }
        });
        
        extendedFetchDataHandlerRegistration = display.getListDisplay().getGrid().addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(FetchDataEvent event) {
                for (SubPresentable sp : subPresentables) {
                    sp.disable();
                }
            }
        });
        orderItemPriceDetailPresenter.getRowDoubleClickedHandlerRegistration().removeHandler();
        GridStructureDisplay detailPresenterView = getDisplay().getOrderItemPriceDetailDisplay();

        detailPresenterView.getGrid().addCellDoubleClickHandler(new CellDoubleClickHandler() {

            @Override
            public void onCellDoubleClick(CellDoubleClickEvent cellDoubleClickEvent) {

                OrderItemPriceDetailDialog dialog = new OrderItemPriceDetailDialog();
                GridStructureDisplay display = ((GridStructureDisplay) ((CreateBasedListStructurePresenter) orderItemPriceDetailPresenter).getDisplay());
                CreateBasedListStructurePresenter orderItemPriceDetailAdjustment = new CreateBasedListStructurePresenter("", dialog.getOrderItemPriceDetailAdjustmentDisplay(), BLCMain.getMessageManager().getString("newOrderItemPriceDetailTitle"));
                orderItemPriceDetailAdjustment.setDataSource((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("orderItemPriceDetailAdjustmentDS"), new String[] { "offerName", "reason", "value", "appliedToSalePrice" }, new Boolean[] { false, false, false, false });
                orderItemPriceDetailAdjustment.setReadOnly(true);
                dialog.editRecord("View of Price Details", (DynamicEntityDataSource) display.getGrid().getDataSource(), display.getGrid().getSelectedRecord(), null, null, null, true);
                orderItemPriceDetailAdjustment.bind();
                orderItemPriceDetailAdjustment.load(display.getGrid().getSelectedRecord(), getPresenterSequenceSetupManager().getDataSource("orderItemPriceDetailDS"));
                dialog.getCancelButton().setTitle("Close");
                orderItemPriceDetailAdjustment.setReadOnly(true);
            }
        });
        
        setReadOnly(true);
        //enable the toolbar so that the export button will be able to be clicked
        getDisplay().getListDisplay().getToolBar().enable();
    }
    
    @Override
    public OrderDisplay getDisplay() {
        return (OrderDisplay) display;
    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orderDS", new OrderListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource top) {
                setupDisplayItems(top);
                ((ListGridDataSource) top).setupGridFields(new String[]{"customer.firstName", "customer.lastName", "total","subTotal","name", "orderNumber", "status", "submitDate"});
                getDisplay().getListDisplay().getGrid().sort("submitDate", SortDirection.DESCENDING);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orderItemDS", new OrderItemListDataSourceFactory(), new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("bundleOrderItemDS", new BundledOrderItemListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                orderItemPresenter = new OrderItemPresenter(getDisplay().getOrderItemsDisplay(), null);
                orderItemPresenter.setDataSource((ListGridDataSource) getPresenterSequenceSetupManager().getDataSource("orderItemDS"), new String[]{"name", "quantity", "price", "retailPrice", "salePrice"}, new Boolean[]{false, false, false, false, false});
                orderItemPresenter.setExpansionDataSource((ListGridDataSource) result, new String[]{"name", "quantity", "price", "retailPrice", "salePrice"}, new Boolean[]{false, false, false, false, false});
                orderItemPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("countryDS", new CountryListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((ListGridDataSource) result).resetPermanentFieldVisibility(
                    "abbreviation",
                    "name"
                );
                EntitySearchDialog countrySearchView = new EntitySearchDialog((ListGridDataSource) result);
                getPresenterSequenceSetupManager().getDataSource("orderDS").
                getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                    "address.country", 
                    countrySearchView,
                    BLCMain.getMessageManager().getString("countrySearchPrompt"),
                    getDisplay().getFulfillmentGroupDisplay()
                );
                library.put("countrySearchView", countrySearchView);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("stateDS", new StateListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                ((ListGridDataSource) result).resetPermanentFieldVisibility(
                    "abbreviation",
                    "name"
                );
                EntitySearchDialog stateSearchView = new EntitySearchDialog((ListGridDataSource) result);
                getPresenterSequenceSetupManager().getDataSource("orderDS").
                getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                    "address.state", 
                    stateSearchView,
                    BLCMain.getMessageManager().getString("stateSearchPrompt"),
                    getDisplay().getFulfillmentGroupDisplay()
                );
                library.put("stateSearchView", stateSearchView);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("fulfillmentGroupDS", new FulfillmentGroupListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                fulfillmentGroupPresenter = new SubPresenter("",getDisplay().getFulfillmentGroupDisplay());
                fulfillmentGroupPresenter.setDataSource((ListGridDataSource) result, new String[]{"referenceNumber", "method", "service", "shippingPrice", "status", "address.postalCode"}, new Boolean[]{false, false, false, false, false, false});
                fulfillmentGroupPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("paymentInfoDS", new PaymentInfoListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                paymentInfoPresenter = new SubPresenter("",getDisplay().getPaymentInfoDisplay());
                paymentInfoPresenter.setDataSource((ListGridDataSource) result, new String[]{"referenceNumber", "type", "amount"}, new Boolean[]{false, false, false});
                paymentInfoPresenter.setReadOnly(true);
                
                ((DynamicEntityDataSource) result).
                getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                    "address.country", 
                    (EntitySearchDialog) library.get("countrySearchView"),
                    BLCMain.getMessageManager().getString("countrySearchPrompt"),
                    getDisplay().getFulfillmentGroupDisplay()
                );
                ((DynamicEntityDataSource) result).
                getFormItemCallbackHandlerManager().addSearchFormItemCallback(
                    "address.state", 
                    (EntitySearchDialog) library.get("stateSearchView"),
                    BLCMain.getMessageManager().getString("stateSearchPrompt"),
                    getDisplay().getFulfillmentGroupDisplay()
                );
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("paymentAdditionalAttributesDS", new PaymentAdditionalAttributesDataSourceFactory(this), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                Map<String, Object> initialValues = new HashMap<String, Object>(2);
                initialValues.put("key", BLCMain.getMessageManager().getString("paymentAttributeKeyDefault"));
                initialValues.put("value", BLCMain.getMessageManager().getString("paymentAttributeValueDefault"));
                additionalPaymentAttributesPresenter = new SimpleMapStructurePresenter("", getDisplay().getAdditionalAttributesDisplay(), initialValues);
                additionalPaymentAttributesPresenter.setDataSource((ListGridDataSource) result, new String[]{"key", "value"}, new Boolean[]{true, true});
                additionalPaymentAttributesPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("offerCodeDS", new OfferCodeListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                offerCodePresenter = new SubPresenter("",getDisplay().getOfferCodeDisplay());
                offerCodePresenter.setDataSource((ListGridDataSource) result, new String[]{"offerCode", "startDate", "endDate", "offer.name", "offer.type", "offer.value"}, new Boolean[]{false, false, false, false, false, false});
                offerCodePresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orderAdjustmentDS", new OrderAdjustmentListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                orderAdjustmentPresenter = new CreateBasedListStructurePresenter("", getDisplay().getOrderAdjustmentDisplay(), BLCMain.getMessageManager().getString("newOrderAdjustmentTitle"));
                orderAdjustmentPresenter.setDataSource((ListGridDataSource) result, new String[]{"reason", "value", "offer.name", "offer.type"}, new Boolean[]{false, false, false, false});
                orderAdjustmentPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orderItemAdjustmentDS", new OrderItemAdjustmentListDataSourceFactory(), new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource result) {
                orderItemAdjustmentPresenter = new CreateBasedListStructurePresenter("", getDisplay().getOrderItemAdjustmentDisplay(), BLCMain.getMessageManager().getString("newOrderItemAdjustmentTitle"));
                orderItemAdjustmentPresenter.setDataSource((ListGridDataSource) result, new String[]{"reason", "value", "offer.type"}, new Boolean[]{false, false, false});
                orderItemAdjustmentPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orderItemPriceDetailAdjustmentDS", new OrderItemPriceDetailAdjustmentListDataSourceFactory(), new AsyncCallbackAdapter() {

            @Override
            public void onSetupSuccess(final DataSource result) {

            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("orderItemPriceDetailDS", new OrderItemPriceDetailListDataSourceFactory(), new AsyncCallbackAdapter() {

            @Override
            public void onSetupSuccess(DataSource result) {
                orderItemPriceDetailPresenter = new CreateBasedListStructurePresenter("", getDisplay().getOrderItemPriceDetailDisplay(), BLCMain.getMessageManager().getString("newOrderItemPriceDetailTitle"));
                orderItemPriceDetailPresenter.setDataSource((ListGridDataSource) result, new String[] { "quantity", "useSalePrice" }, new Boolean[] { false, false });
                orderItemPriceDetailPresenter.setReadOnly(true);
            }
        }));


        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("fulfillmentGroupAdjustmentDS", new FulfillmentGroupAdjustmentListDataSourceFactory(), new AsyncCallbackAdapter() {

            @Override
            public void onSetupSuccess(DataSource result) {
                fulfillmentGroupAdjustmentPresenter = new CreateBasedListStructurePresenter("", getDisplay().getFulfillmentGroupAdjustmentDisplay(), BLCMain.getMessageManager().getString("newFGAdjustmentTitle"));
                fulfillmentGroupAdjustmentPresenter.setDataSource((ListGridDataSource) result, new String[] { "reason", "value", "offer.type" }, new Boolean[] { false, false, false });
                fulfillmentGroupAdjustmentPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("discreteOrderItemFeePriceDS", new DiscreteOrderItemFeePriceDataSourceFactory(), new AsyncCallbackAdapter() {

            @Override
            public void onSetupSuccess(DataSource result) {
                feesPresenter = new CreateBasedListStructurePresenter("", getDisplay().getOrderItemFeeDisplay(), BLCMain.getMessageManager().getString("newOrderItemFeeTitle"));
                feesPresenter.setDataSource((ListGridDataSource) result, new String[] { "name", "amount", "reportingCode" }, new Boolean[] { false, false, false });
                feesPresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("paymentResponseItemDS", new PaymentResponseItemListDataSourceFactory(), new AsyncCallbackAdapter() {

            @Override
            public void onSetupSuccess(DataSource result) {
                paymentResponsePresenter = new CreateBasedListStructurePresenter("", getDisplay().getPaymentResponseDisplay(), null, BLCMain.getMessageManager().getString("paymentResponseListTitle"));
                paymentResponsePresenter.setDataSource((ListGridDataSource) result, new String[] { "transactionTimestamp", "amountPaid", "transactionSuccess", "transactionType" }, new Boolean[] { false, false, false, false });
                paymentResponsePresenter.setReadOnly(true);
            }
        }));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("paymentLogDS", new PaymentLogListDataSourceFactory(), new AsyncCallbackAdapter() {

            @Override
            public void onSetupSuccess(DataSource result) {
                paymentLogPresenter = new CreateBasedListStructurePresenter("", getDisplay().getPaymentLogDisplay(), null, BLCMain.getMessageManager().getString("paymentLogListTitle"));
                paymentLogPresenter.setDataSource((ListGridDataSource) result, new String[] { "transactionTimestamp", "amountPaid", "transactionType", "transactionSuccess", "logType" }, new Boolean[] { false, false, false, false, false });
                paymentLogPresenter.setReadOnly(true);

            }
        }));

    }

}
