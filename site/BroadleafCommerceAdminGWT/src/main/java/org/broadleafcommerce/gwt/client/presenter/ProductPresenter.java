package org.broadleafcommerce.gwt.client.presenter;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.event.CategorySelectionChangedEvent;
import org.broadleafcommerce.gwt.client.event.CategorySelectionChangedEventHandler;
import org.broadleafcommerce.gwt.client.view.DynamicListDisplay;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;

public class ProductPresenter extends DynamicListPresenter<ListGrid> {

	protected String categoryId;
	
	public ProductPresenter(HandlerManager eventBus, DynamicListDisplay<ListGrid> view) {
		super(eventBus, view);
	}
	
	@Override
	public void bind() {
		super.bind();
		eventBus.addHandler(CategorySelectionChangedEvent.TYPE, new CategorySelectionChangedEventHandler() {
			public void onChangeSelection(CategorySelectionChangedEvent event) {
				categoryId = event.getRecord().getAttribute("id");
				Criteria criteria = new Criteria();
				criteria.addCriteria("defaultCategory", categoryId);
				display.getGrid().fetchData(criteria);
				display.getAddButton().enable();
			}
		});
	}
	
	@Override
	protected ClickHandler getAddHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					Map<String, String> initialValues = new HashMap<String, String>();
					initialValues.put("defaultCategory", categoryId);
					initialValues.put("name", "Untitled");
					initialValues.put("type", ((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname());
					Main.ENTITY_ADD.editNewRecord((DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues);
				}
			}
        };
	}

}
