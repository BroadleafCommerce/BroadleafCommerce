package org.broadleafcommerce.cms.admin.client.presenter.sandbox;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
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

    protected Record currentStructuredContentRecord;

	protected SandBoxDisplay display;
	protected ListGridRecord lastSelectedRecord = null;
	protected Boolean loaded = false;

	protected HandlerRegistration selectionChangedHandlerRegistration;
	protected HandlerRegistration revertAllClickHandlerRegistration;
    protected HandlerRegistration revertSelectionClickHandlerRegistration;
	protected HandlerRegistration promoteAllClickHandlerRegistration;
    protected HandlerRegistration promoteSelectionClickHandlerRegistration;
	protected PresenterSequenceSetupManager presenterSequenceSetupManager = new PresenterSequenceSetupManager(this);

	protected Boolean disabled = false;

	public void setStartState() {
        display.getPromoteAllButton().disable();
        display.getPromoteSelectionButton().disable();
        display.getRevertAllButton().disable();
        display.getRevertSelectionButton().disable();
        display.getRefreshButton().enable();
	}

	public void enable() {
		disabled = false;
		display.getPromoteAllButton().enable();
        display.getPromoteSelectionButton().disable();
        display.getRevertAllButton().enable();
        display.getRevertSelectionButton().disable();
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

	public void bind() {
        revertAllClickHandlerRegistration = display.getRefreshButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{"fetch", "", ""});
                    setStartState();
                    display.getGrid().invalidateCache();
                }
            }
        });
		revertAllClickHandlerRegistration = display.getRevertAllButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{"revertAll", "", ""});
                    setStartState();
                    display.getGrid().invalidateCache();
                }
            }
        });
		revertSelectionClickHandlerRegistration = display.getRevertSelectionButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{"revertSelected", getSelectedRecords(), ""});
                    setStartState();
                    display.getGrid().invalidateCache();
                }
            }
        });
		selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord selectedRecord = event.getSelectedRecord();
				if (event.getState() && selectedRecord != null) {
					if (!selectedRecord.equals(lastSelectedRecord)) {
						display.getRevertSelectionButton().enable();
                        display.getPromoteSelectionButton().enable();
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
                            ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{"promoteAll", "", comment});
                            setStartState();
                            display.getGrid().invalidateCache();
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
                            ((CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS")).setCustomCriteria(new String[]{"promoteSelected", getSelectedRecords(), comment});
                            setStartState();
                            display.getGrid().invalidateCache();
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
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("sandBoxItemDS", new SandBoxItemListDataSourceFactory(), null, new Object[]{}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                setupDisplayItems(dataSource);
                ((CustomCriteriaListGridDataSource) dataSource).setupGridFields(new String[]{"createdBy.name", "description", "sandBoxItemType", "sandboxOperationType", "lastUpdateDate"}, new Boolean[]{false, false, false, false, false});
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

    public HandlerRegistration getRevertAllClickHandlerRegistration() {
        return revertAllClickHandlerRegistration;
    }

    public HandlerRegistration getRevertSelectionClickHandlerRegistration() {
        return revertSelectionClickHandlerRegistration;
    }

    public HandlerRegistration getPromoteAllClickHandlerRegistration() {
        return promoteAllClickHandlerRegistration;
    }

    public HandlerRegistration getPromoteSelectionClickHandlerRegistration() {
        return promoteSelectionClickHandlerRegistration;
    }

    public PresenterSequenceSetupManager getPresenterSequenceSetupManager() {
		return presenterSequenceSetupManager;
	}

	public Boolean getLoaded() {
		return loaded;
	}
}
