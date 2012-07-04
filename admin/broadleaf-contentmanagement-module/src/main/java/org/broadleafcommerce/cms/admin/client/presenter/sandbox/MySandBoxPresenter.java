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

package org.broadleafcommerce.cms.admin.client.presenter.sandbox;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import org.broadleafcommerce.cms.admin.client.datasource.sandbox.SandBoxItemListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.sandbox.MySandBoxDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

/**
 * @author Jeff Fischer
 */
public class MySandBoxPresenter extends SandBoxPresenter implements Instantiable {

    protected HandlerRegistration pendingRefreshClickHandlerRegistration;
    protected HandlerRegistration pendingPreviewClickHandlerRegistration;
    protected HandlerRegistration reclaimAllClickHandlerRegistration;
    protected HandlerRegistration reclaimSelectionClickHandlerRegistration;
    protected HandlerRegistration pendingSelectionChangedHandlerRegistration;
    protected HandlerRegistration releaseAllClickHandlerRegistration;
    protected HandlerRegistration releaseSelectionClickHandlerRegistration;

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("sandBoxItemDS", new SandBoxItemListDataSourceFactory(), null, new Object[]{BLCMain.currentViewKey, "fetch", "", "", "standard"}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pendingSandBoxItemDS", new SandBoxItemListDataSourceFactory(), null, new Object[]{BLCMain.currentViewKey, "fetch", "", "", "pending"}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                CustomCriteriaListGridDataSource sandBoxItemDS = (CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS");
                setupDisplayItems(sandBoxItemDS, dataSource);
                sandBoxItemDS.setupGridFields(new String[]{});
                ((CustomCriteriaListGridDataSource) dataSource).setAssociatedGrid(((MySandBoxDisplay) getDisplay()).getPendingGrid());
                ((CustomCriteriaListGridDataSource) dataSource).setupGridFields(new String[]{});
            }
        }));
    }

    protected MySandBoxDisplay getMySandBoxDisplay() {
        return (MySandBoxDisplay) getDisplay();
    }

    @Override
    protected void setupDisplayItems(DataSource entityDataSource, DataSource... additionalDataSources) {
        super.setupDisplayItems(entityDataSource, additionalDataSources);
        setPendingStartState();
    }

    public void setPendingStartState() {
        getMySandBoxDisplay().getReclaimAllButton().disable();
        getMySandBoxDisplay().getReclaimSelectionButton().disable();
        getMySandBoxDisplay().getReleaseAllButton().disable();
        getMySandBoxDisplay().getReleaseSelectionButton().disable();
        getMySandBoxDisplay().getPendingRefreshButton().enable();
        getMySandBoxDisplay().getPendingPreviewButton().enable();
    }

	public void pendingEnable() {
        getMySandBoxDisplay().getReclaimAllButton().enable();
        getMySandBoxDisplay().getReclaimSelectionButton().disable();
        getMySandBoxDisplay().getReleaseAllButton().enable();
        getMySandBoxDisplay().getReleaseSelectionButton().disable();
	}

    protected String getPendingSelectedRecords() {
        ListGridRecord[] records = getMySandBoxDisplay().getPendingGrid().getSelection();
        StringBuffer sb = new StringBuffer();
        for (int j=0;j<records.length;j++) {
            String id = getPresenterSequenceSetupManager().getDataSource("pendingSandBoxItemDS").getPrimaryKeyValue(records[j]);
            sb.append(id);
            if (j < records.length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    @Override
    protected void invalidateOtherCache() {
        ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("pendingSandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"fetch", "", "", "pending"});
        setPendingStartState();
        getMySandBoxDisplay().getPendingGrid().invalidateCache();
    }

    @Override
    public void bind() {
        super.bind();
        pendingPreviewClickHandlerRegistration = getMySandBoxDisplay().getPendingPreviewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ListGridRecord[] records = getMySandBoxDisplay().getPendingGrid().getSelection();
                    previewSelection(records);
                }
            }
        });
        pendingRefreshClickHandlerRegistration = getMySandBoxDisplay().getPendingRefreshButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    invalidateOtherCache();
                }
            }
        });
        reclaimAllClickHandlerRegistration = getMySandBoxDisplay().getReclaimAllButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("pendingSandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey, "reclaimAll", "", "", "pending"});
                    setPendingStartState();
                    getMySandBoxDisplay().getPendingGrid().invalidateCache();
                    Timer timer = new Timer() {
                        public void run() {
                            invalidateMyCache();
                        }
                    };
                    timer.schedule(1000);
                }
            }
        });
		reclaimSelectionClickHandlerRegistration = getMySandBoxDisplay().getReclaimSelectionButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("pendingSandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"reclaimSelected", getPendingSelectedRecords(), "", "pending"});
                    setPendingStartState();
                    getMySandBoxDisplay().getPendingGrid().invalidateCache();
                    Timer timer = new Timer() {
                        public void run() {
                            invalidateMyCache();
                        }
                    };
                    timer.schedule(1000);
                }
            }
        });
		pendingSelectionChangedHandlerRegistration = getMySandBoxDisplay().getPendingGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord selectedRecord = event.getSelectedRecord();
				if (event.getState() && selectedRecord != null) {
                    getMySandBoxDisplay().getReclaimSelectionButton().enable();
                    getMySandBoxDisplay().getReleaseSelectionButton().enable();
                    getMySandBoxDisplay().getPendingPreviewButton().enable();
				}
			}
		});
		releaseAllClickHandlerRegistration = getMySandBoxDisplay().getReleaseAllButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("pendingSandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"releaseAll", "", "", "pending"});
                    setPendingStartState();
                    getMySandBoxDisplay().getPendingGrid().invalidateCache();
                }
            }
        });
		releaseSelectionClickHandlerRegistration = getMySandBoxDisplay().getReleaseSelectionButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("pendingSandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey, "releaseSelected", getPendingSelectedRecords(), "", "pending"});
                    setPendingStartState();
                    getMySandBoxDisplay().getPendingGrid().invalidateCache();
                }
            }
        });
        getMySandBoxDisplay().getPendingGrid().addDataArrivedHandler(new DataArrivedHandler() {
            @Override
            public void onDataArrived(DataArrivedEvent event) {
                if (event.getEndRow() > event.getStartRow()) {
                    pendingEnable();
                }
            }
        });
    }
}
