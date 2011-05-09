package org.broadleafcommerce.gwt.client;

import org.broadleafcommerce.gwt.client.datasource.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.event.DataSourcePreparedEvent;
import org.broadleafcommerce.gwt.client.event.DataSourcePreparedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.CategoryPresenter;
import org.broadleafcommerce.gwt.client.presenter.Presenter;
import org.broadleafcommerce.gwt.client.presenter.ProductPresenter;
import org.broadleafcommerce.gwt.client.service.AppServices;
import org.broadleafcommerce.gwt.client.service.CeilingEntities;
import org.broadleafcommerce.gwt.client.view.AbstractDisplay;
import org.broadleafcommerce.gwt.client.view.ViewLibrary;
import org.broadleafcommerce.gwt.client.view.catalog.CategoryView;
import org.broadleafcommerce.gwt.client.view.catalog.ProductView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.smartgwt.client.widgets.layout.HLayout;

public class AppController implements Presenter, ValueChangeHandler<String> {

	private static AppController controller = null;

	public static AppController getInstance() {
		if (controller == null) {
			AppController.controller = new AppController();
		}
		return AppController.controller;
	}

	private final HandlerManager eventBus = new HandlerManager(null);
	private HLayout container;
	private ViewLibrary<AbstractDisplay> viewLibrary = new ViewLibrary<AbstractDisplay>();

	private AppController() {
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
		
		eventBus.addHandler(DataSourcePreparedEvent.TYPE, new DataSourcePreparedEventHandler() {
			public void onDataSourcePrepared(DataSourcePreparedEvent event) {
				if ("category".equals(event.getToken())) {
					Presenter presenter = new CategoryPresenter(eventBus, (CategoryView) viewLibrary.getView(event.getToken(), new CategoryView(event.getDataSource()), false));
					presenter.go(container);
					DynamicEntityDataSource productDataSource = new DynamicEntityDataSource(CeilingEntities.PRODUCT, eventBus, "product", AppServices.DYNAMIC_ENTITY, new String[]{"defaultCategory"});
					productDataSource.buildFields();
				}
				if ("product".equals(event.getToken())) {
					Presenter presenter2 = new ProductPresenter(eventBus, (ProductView) viewLibrary.getView(event.getToken(), new ProductView(event.getDataSource()), false));
					presenter2.go(container);
				}
			}
		});
	}

	public void go(final HLayout container) {
		this.container = container;

		if ("".equals(History.getToken())) {
			History.newItem("catalog");
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			if (token.equals("catalog") && !viewLibrary.equalsCurrentViewClass(CategoryView.class.getName())) {
				viewLibrary.clearCurrentView();
				if (viewLibrary.containsKey(token)) {
					Presenter presenter = new CategoryPresenter(eventBus, (CategoryView) viewLibrary.getView("category", null, false));
					presenter.go(container);
					Presenter presenter2 = new ProductPresenter(eventBus, (ProductView) viewLibrary.getView("product", null, false));
					presenter2.go(container);
				} else {
					DynamicEntityDataSource dataSource = new DynamicEntityDataSource(CeilingEntities.CATEGORY, eventBus, "category", AppServices.DYNAMIC_ENTITY, null);
					dataSource.buildFields();
				}
			}
		}
	}
}
