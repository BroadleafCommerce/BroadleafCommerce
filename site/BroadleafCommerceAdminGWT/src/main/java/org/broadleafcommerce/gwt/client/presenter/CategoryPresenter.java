package org.broadleafcommerce.gwt.client.presenter;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.event.CategorySelectionChangedEvent;
import org.broadleafcommerce.gwt.client.view.DynamicListDisplay;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class CategoryPresenter extends DynamicListPresenter<TreeGrid> {
	
	public CategoryPresenter(HandlerManager eventBus, DynamicListDisplay<TreeGrid> view) {
		super(eventBus, view);
	}

	@Override
	protected void changeSelection() {
		eventBus.fireEvent(new CategorySelectionChangedEvent(lastSelectedRecord));
	}

	@Override
	protected ClickHandler getAddHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					Map<String, String> initialValues = new HashMap<String, String>();
					initialValues.put("defaultParentCategory", display.getGrid().getSelectedRecord().getAttribute("id"));
					initialValues.put("name", "Untitled");
					initialValues.put("type", ((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname());
					Main.ENTITY_ADD.editNewRecord((DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues);
				}
			}
        };
	}
	
}
