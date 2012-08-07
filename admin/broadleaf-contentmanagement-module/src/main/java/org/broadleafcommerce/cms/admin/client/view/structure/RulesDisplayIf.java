package org.broadleafcommerce.cms.admin.client.view.structure;

import java.util.List;

import org.broadleafcommerce.openadmin.client.view.dynamic.ItemBuilderDisplay;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public interface RulesDisplayIf {

	public abstract List<ItemBuilderDisplay> getItemBuilderViews();

	public abstract void setItemBuilderViews(
			List<ItemBuilderDisplay> itemBuilderViews);

	public abstract VLayout getItemBuilderContainerLayout();

	public abstract ItemBuilderDisplay addItemBuilder(
			DataSource orderItemDataSource);

	public abstract void removeItemBuilder(ItemBuilderDisplay itemBuilder);

	public abstract void removeAllItemBuilders();
   
	public ToolStripButton getRulesSaveButton();
	
	public ToolStripButton getRulesRefreshButton(); 

}