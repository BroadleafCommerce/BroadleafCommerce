package org.broadleafcommerce.gwt.client.presenter.catalog;

import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.event.ProductSelectionChangedEvent;
import org.broadleafcommerce.gwt.client.event.ProductSelectionChangedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.dynamic.DynamicListPresenter;
import org.broadleafcommerce.gwt.client.view.catalog.ProductView;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.tab.Tab;

public class SkuPresenter extends DynamicListPresenter {

	
	@Override
	public void bind() {
		super.bind();
		eventBus.addHandler(ProductSelectionChangedEvent.TYPE, new ProductSelectionChangedEventHandler() {
			public void onChangeSelection(ProductSelectionChangedEvent event) {
				String productId = event.getRecord().getAttribute("id");
				Criteria criteria = new Criteria();
				criteria.addCriteria("allParentProduct", productId);
				ForeignKey currentForeignKey = new ForeignKey();
				currentForeignKey.setManyToField("allParentProduct");
				currentForeignKey.setCurrentValue(productId);
				((DynamicEntityDataSource) display.getGrid().getDataSource()).setCurrentForeignKey(currentForeignKey);
				display.getGrid().clearCriteria();
				display.getGrid().fetchData(criteria);
				display.getAddButton().enable();
				display.getEntityType().enable();
			}
		});
	}
	
	@Override
	protected ClickHandler getAddHandler() {
		return new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					//do nothing
				}
			}
        };
	}

	@Override
	public void go(Canvas container) {
		super.go(container);
		if (((ProductView) container).getTopTabSet().getTab("sku") == null) {
			Tab skuTab = new Tab("Sku");
			((ProductView) container).getTopTabSet().addTab(skuTab);
	        skuTab.setPane(display.asCanvas());
		}
	}

	@Override
	protected void changeSelection() {
		//do nothing
	}

}
