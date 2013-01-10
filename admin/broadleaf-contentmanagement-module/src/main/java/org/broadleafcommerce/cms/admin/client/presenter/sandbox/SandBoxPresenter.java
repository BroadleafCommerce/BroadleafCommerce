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
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import org.broadleafcommerce.cms.admin.client.datasource.sandbox.SandBoxItemListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.sandbox.CommentCallback;
import org.broadleafcommerce.cms.admin.client.view.sandbox.CommentDialog;
import org.broadleafcommerce.cms.admin.client.view.sandbox.SandBoxDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractEntityPresenter;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;
import org.broadleafcommerce.openadmin.client.view.Display;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/22/11
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class SandBoxPresenter extends AbstractEntityPresenter implements Instantiable {

    private static final CommentDialog COMMENT_DIALOG = new CommentDialog();

    protected SandBoxDisplay display;
    protected ListGridRecord lastSelectedRecord = null;
    protected Boolean loaded = false;

    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration revertRejectAllClickHandlerRegistration;
    protected HandlerRegistration revertRejectSelectionClickHandlerRegistration;
    protected HandlerRegistration promoteAllClickHandlerRegistration;
    protected HandlerRegistration promoteSelectionClickHandlerRegistration;
    protected HandlerRegistration refreshClickHandlerRegistration;
    protected HandlerRegistration previewClickHandlerRegistration;
    protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);

    protected Boolean disabled = false;

    public void setStartState() {
        display.getPromoteAllButton().disable();
        display.getPromoteSelectionButton().disable();
        display.getRevertRejectAllButton().disable();
        display.getRevertRejectSelectionButton().disable();
        display.getRefreshButton().enable();
        display.getPreviewButton().disable();
    }

    public void enable() {
        disabled = false;
        display.getPromoteAllButton().enable();
        display.getPromoteSelectionButton().disable();
        display.getRevertRejectAllButton().enable();
        display.getRevertRejectSelectionButton().disable();
        display.getPreviewButton().disable();
    }

    public void disable() {
        disabled = true;
        setStartState();
    }

    protected String getSelectedRecords() {
        ListGridRecord[] records = display.getGrid().getSelection();
        StringBuffer sb = new StringBuffer();
        for (int j=0;j<records.length;j++) {
            String id = getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS").getPrimaryKeyValue(records[j]);
            sb.append(id);
            if (j < records.length - 1) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    protected void previewSelection(ListGridRecord[] records) {
        String path = BLCMain.buildStoreFrontBaseUrl();
        if (records == null || (records != null && records.length > 1)) {
            path += "?blSandboxId=" + org.broadleafcommerce.openadmin.client.security.SecurityManager.USER.getCurrentSandBoxId();
        } else {
            String specificSandboxId = records[0].getAttribute("sandBox.id");
            String type = records[0].getAttribute("sandBoxItemType");
            if (type.equals("PAGE")) {
                path += records[0].getAttribute("description");
            }
            path += "?blSandboxId=" + specificSandboxId;
        }
        com.google.gwt.user.client.Window.open(path, "cmsPreview", null);
    }

    protected void invalidateMyCache() {
        ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"fetch", "", "", "standard"});
        setStartState();
        display.getGrid().invalidateCache();
    }

    public void bind() {
        previewClickHandlerRegistration = display.getPreviewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ListGridRecord[] records = display.getGrid().getSelection();
                    previewSelection(records);
                }
            }
        });
        refreshClickHandlerRegistration = display.getRefreshButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    invalidateMyCache();
                }
            }
        });
        revertRejectAllClickHandlerRegistration = display.getRevertRejectAllButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    if (BLCMain.currentViewKey.equals("userSandBox")) {
                        ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"revertRejectAll", "", "", "standard"});
                        setStartState();
                        display.getGrid().invalidateCache();
                    } else {
                        COMMENT_DIALOG.launch("Enter a rejection comment", new CommentCallback() {
                            @Override
                            public void comment(String comment) {
                                ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"revertRejectAll", "", comment, "standard"});
                                setStartState();
                                display.getGrid().invalidateCache();
                            }
                        });
                    }
                }
            }
        });
        revertRejectSelectionClickHandlerRegistration = display.getRevertRejectSelectionButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    if (BLCMain.currentViewKey.equals("userSandBox")) {
                        ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"revertRejectSelected", getSelectedRecords(), "", "standard"});
                        setStartState();
                        display.getGrid().invalidateCache();
                    } else {
                        COMMENT_DIALOG.launch("Enter a rejection comment", new CommentCallback() {
                            @Override
                            public void comment(String comment) {
                                ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"revertRejectSelected", getSelectedRecords(), comment, "standard"});
                                setStartState();
                                display.getGrid().invalidateCache();
                            }
                        });
                    }
                }
            }
        });
        selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            public void onSelectionChanged(SelectionEvent event) {
                ListGridRecord selectedRecord = event.getSelectedRecord();
                if (event.getState() && selectedRecord != null) {
                    if (!selectedRecord.equals(lastSelectedRecord)) {
                        display.getRevertRejectSelectionButton().enable();
                        display.getPromoteSelectionButton().enable();
                        display.getPreviewButton().enable();
                    }
                }
            }
        });
        promoteAllClickHandlerRegistration = display.getPromoteAllButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    COMMENT_DIALOG.launch("Enter a promotion comment", new CommentCallback() {
                        @Override
                        public void comment(String comment) {
                            ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"promoteAll", "", comment, "standard"});
                            setStartState();
                            display.getGrid().invalidateCache();
                            Timer timer = new Timer() {
                                public void run() {
                                    invalidateOtherCache();
                                }
                            };
                            timer.schedule(1000);
                        }
                    });
                }
            }
        });
        promoteSelectionClickHandlerRegistration = display.getPromoteSelectionButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    COMMENT_DIALOG.launch("Enter a promotion comment", new CommentCallback() {
                        @Override
                        public void comment(String comment) {
                            ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{BLCMain.currentViewKey,"promoteSelected", getSelectedRecords(), comment, "standard"});
                            setStartState();
                            display.getGrid().invalidateCache();
                            Timer timer = new Timer() {
                                public void run() {
                                    invalidateOtherCache();
                                }
                            };
                            timer.schedule(1000);
                        }
                    });
                }
            }
        });
        display.getGrid().addDataArrivedHandler(new DataArrivedHandler() {
            @Override
            public void onDataArrived(DataArrivedEvent event) {
                if (event.getEndRow() > event.getStartRow()) {
                    enable();
                }
            }
        });
    }

    public void postSetup(Canvas container) {
        BLCMain.ISNEW = false;
        if (containsDisplay(container)) {
            display.show();
        } else {
            bind();
            container.addChild(display.asCanvas());
            loaded = true;
        }
        if (BLCMain.MODAL_PROGRESS.isActive()) {
            BLCMain.MODAL_PROGRESS.stopProgress();
        }
        if (BLCMain.SPLASH_PROGRESS.isActive()) {
            BLCMain.SPLASH_PROGRESS.stopProgress();
        }
    }

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("sandBoxItemDS", new SandBoxItemListDataSourceFactory(), null, new Object[]{BLCMain.currentViewKey, "fetch", "", "", "standard"}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(dataSource);
                ((CustomCriteriaListGridDataSource) dataSource).setupGridFields(new String[]{"auditable.createdBy.name", "description", "sandBoxItemType", "sandboxOperationType", "auditable.dateCreated", "auditable.dateUpdated"});
            }
        }));
    }

    protected Boolean containsDisplay(Canvas container) {
        return container.contains(display.asCanvas());
    }

    public SandBoxDisplay getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = (SandBoxDisplay) display;
    }

    protected void setupDisplayItems(DataSource entityDataSource, DataSource... additionalDataSources) {
        getDisplay().build(entityDataSource, additionalDataSources);
        ((PresentationLayerAssociatedDataSource) entityDataSource).setAssociatedGrid(display.getGrid());
        setStartState();
    }

    public HandlerRegistration getSelectionChangedHandlerRegistration() {
        return selectionChangedHandlerRegistration;
    }

    public HandlerRegistration getRevertRejectAllClickHandlerRegistration() {
        return revertRejectAllClickHandlerRegistration;
    }

    public HandlerRegistration getRevertRejectSelectionClickHandlerRegistration() {
        return revertRejectSelectionClickHandlerRegistration;
    }

    public HandlerRegistration getPromoteAllClickHandlerRegistration() {
        return promoteAllClickHandlerRegistration;
    }

    public HandlerRegistration getPromoteSelectionClickHandlerRegistration() {
        return promoteSelectionClickHandlerRegistration;
    }

    public HandlerRegistration getRefreshClickHandlerRegistration() {
        return refreshClickHandlerRegistration;
    }

    public PresenterSequenceSetupManager getPresenterSequenceSetupManager() {
        return presenterSequenceSetupManager;
    }

    public Boolean getLoaded() {
        return loaded;
    }

    protected void invalidateOtherCache() {
        //do nothing
    }
}
