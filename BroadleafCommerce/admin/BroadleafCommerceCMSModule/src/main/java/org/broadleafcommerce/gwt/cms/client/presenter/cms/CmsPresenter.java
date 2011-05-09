package org.broadleafcommerce.gwt.cms.client.presenter.cms;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.AsyncCallbackAdapter;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.cms.client.datasource.cms.CmsListDataSourceFactory;
import org.broadleafcommerce.gwt.cms.client.view.cms.CmsDisplay;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

public class CmsPresenter extends DynamicEntityPresenter implements Instantiable {
	
	@Override
	protected void changeSelection(final Record selectedRecord) {
		//do nothing
	}
	
	@Override
	protected void addClicked() {
		Map<String, Object> initialValues = new HashMap<String, Object>();
		initialValues.put("_type", new String[]{((DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()});
		BLCMain.ENTITY_ADD.editNewRecord("Create New Content", (DynamicEntityDataSource) display.getListDisplay().getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				//Criteria myCriteria = new Criteria();
				//myCriteria.addCriteria("username", event.getRecord().getAttribute("username"));
				//display.getListDisplay().getGrid().fetchData(myCriteria);
			}
		}, "90%", null, null);
	}

	@Override
	public void bind() {
		super.bind();
	}

	@Override
	public void go(final Canvas container) {
		BLCMain.MODAL_PROGRESS.startProgress(new Timer() {
			public void run() {
				if (loaded) {
					CmsPresenter.super.go(container);
					return;
				}
				CmsListDataSourceFactory.createDataSource("cmsDS", new AsyncCallbackAdapter() {
					public void onSuccess(final DataSource top) {
						setupDisplayItems(top);
						((ListGridDataSource) top).setupGridFields(new String[]{"description", "keywords", "activeStartDate", "activeEndDate"}, new Boolean[]{true, true, true, true});
							
						CmsPresenter.super.go(container);
					}
				});
			}
		});
	}

	@Override
	public CmsDisplay getDisplay() {
		return (CmsDisplay) display;
	}
	
}
