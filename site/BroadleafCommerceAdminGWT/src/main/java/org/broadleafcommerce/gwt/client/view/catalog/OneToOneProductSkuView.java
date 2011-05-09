package org.broadleafcommerce.gwt.client.view.catalog;

import org.broadleafcommerce.gwt.client.reflection.Instantiable;
import org.broadleafcommerce.gwt.client.view.TabSet;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.form.DynamicFormView;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormOnlyView;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class OneToOneProductSkuView extends HLayout implements Instantiable, OneToOneProductSkuDisplay {
	
	protected DynamicFormView dynamicFormDisplay;
	protected GridStructureView crossSaleDisplay;
	protected GridStructureView upSaleDisplay;
	protected GridStructureView mediaDisplay;
	protected DynamicEntityListView listDisplay;
	protected GridStructureView attributesDisplay;
	protected GridStructureView allCategoriesDisplay;
    
	public OneToOneProductSkuView() {
		setHeight100();
		setWidth100();
	}
	
	public void build(final DataSource dataSource) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);
        
		listDisplay = new DynamicEntityListView("Products", dataSource);
        leftVerticalLayout.addMember(listDisplay);
        
        TabSet topTabSet = new TabSet();  
        topTabSet.setTabBarPosition(Side.TOP);  
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("60%");  
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);
        
        Tab detailsTab = new Tab("Details");
        
        dynamicFormDisplay = new DynamicFormView("Product Details", dataSource);
        attributesDisplay = new GridStructureView("Product Attributes", false, true);
        ((FormOnlyView) dynamicFormDisplay.getFormOnlyDisplay()).addMember(attributesDisplay);
        detailsTab.setPane(dynamicFormDisplay);
        
        Tab crossSaleTab = new Tab("Featured"); 
        
        VLayout crossLayout = new VLayout();
        crossLayout.setHeight100();
        crossLayout.setWidth100();
        crossLayout.setBackgroundColor("#eaeaea");
        crossLayout.setOverflow(Overflow.AUTO);
        
        crossSaleDisplay = new GridStructureView("Cross Sale Products", true, true);
        crossLayout.addMember(crossSaleDisplay);
        
        upSaleDisplay = new GridStructureView("UpSale Products", true, true);
        crossLayout.addMember(upSaleDisplay);
        
        crossSaleTab.setPane(crossLayout);
        
        Tab mediaTab = new Tab("Media"); 
        
        VLayout mediaLayout = new VLayout();
        mediaLayout.setHeight100();
        mediaLayout.setWidth100();
        mediaLayout.setBackgroundColor("#eaeaea");
        mediaLayout.setOverflow(Overflow.AUTO);
        
        mediaDisplay = new GridStructureView("Media", false, true);
        mediaLayout.addMember(mediaDisplay);
        
        mediaTab.setPane(mediaLayout);
        
        Tab categoriesTab = new Tab("Categories");
        
        VLayout categoriesLayout = new VLayout();
        categoriesLayout.setHeight100();
        categoriesLayout.setWidth100();
        categoriesLayout.setBackgroundColor("#eaeaea");
        categoriesLayout.setOverflow(Overflow.AUTO);
        
        allCategoriesDisplay = new GridStructureView("All Parent Categories", false, false);
        categoriesLayout.addMember(allCategoriesDisplay);
        
        categoriesTab.setPane(categoriesLayout);
        
        topTabSet.addTab(detailsTab);
        topTabSet.addTab(crossSaleTab);
        topTabSet.addTab(mediaTab);
        topTabSet.addTab(categoriesTab);
        
        addMember(leftVerticalLayout);
        addMember(topTabSet);
	}

	public Canvas asCanvas() {
		return this;
	}

	public GridStructureDisplay getCrossSaleDisplay() {
		return crossSaleDisplay;
	}

	public GridStructureDisplay getUpSaleDisplay() {
		return upSaleDisplay;
	}

	public GridStructureDisplay getMediaDisplay() {
		return mediaDisplay;
	}

	public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
	
	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}
	
	public GridStructureDisplay getAttributesDisplay() {
		return attributesDisplay;
	}

	public GridStructureDisplay getAllCategoriesDisplay() {
		return allCategoriesDisplay;
	}

}
