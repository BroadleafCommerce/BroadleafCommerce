package org.broadleafcommerce.gwt.client.presenter.catalog;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.event.CategorySelectionChangedEvent;
import org.broadleafcommerce.gwt.client.event.CategorySelectionChangedEventHandler;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.gwt.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.gwt.client.event.ProductSelectionChangedEvent;
import org.broadleafcommerce.gwt.client.presenter.dynamic.DynamicListPresenter;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ProductPresenter extends DynamicListPresenter {

	protected String categoryId;
	
	@Override
	protected void changeSelection(Record selectedRecord) {
		eventBus.fireEvent(new ProductSelectionChangedEvent((ListGridRecord) selectedRecord, display.getGrid().getDataSource()));
	}
	
	@Override
	public void bind() {
		super.bind();
		eventBus.addHandler(CategorySelectionChangedEvent.TYPE, new CategorySelectionChangedEventHandler() {
			public void onChangeSelection(CategorySelectionChangedEvent event) {
				categoryId = event.getRecord().getAttribute("id");
				Criteria criteria = new Criteria();
				criteria.addCriteria("defaultCategory", categoryId);
				ForeignKey currentForeignKey = new ForeignKey();
				currentForeignKey.setManyToField("defaultCategory");
				currentForeignKey.setCurrentValue(categoryId);
				//((DynamicEntityDataSource) display.getGrid().getDataSource()).setCurrentForeignKey(currentForeignKey);
				display.getGrid().clearCriteria();
				display.getGrid().fetchData(criteria);
				display.getAddButton().enable();
				display.getEntityType().enable();
			}
		});
	}
	
	@Override
	protected void addClicked() {
		Map<String, String> initialValues = new HashMap<String, String>();
		initialValues.put("defaultCategory", categoryId);
		initialValues.put("name", "Untitled");
		initialValues.put("type", ((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname());
		Main.ENTITY_ADD.editNewRecord((DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues, new NewItemCreatedEventHandler() {
			public void onNewItemCreated(NewItemCreatedEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	protected void removeClicked() {
		//do nothing
	}

}
