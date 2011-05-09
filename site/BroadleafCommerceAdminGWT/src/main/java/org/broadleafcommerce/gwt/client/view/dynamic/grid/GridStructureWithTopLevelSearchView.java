package org.broadleafcommerce.gwt.client.view.dynamic.grid;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

public class GridStructureWithTopLevelSearchView extends GridStructureView implements GridStructureWithTopLevelSearchDisplay {

	protected TextItem textItem;
	protected ToolStripButton textItemButton;
	
	/**
	 * @param title
	 * @param canReorder
	 * @param canEdit
	 */
	public GridStructureWithTopLevelSearchView(String title, Boolean canReorder, Boolean canEdit, String textItemTitle) {
		super(title, canReorder, canEdit);
		Label textLabel = new Label();
		textLabel.setContents(textItemTitle);
		textLabel.setWrap(false);
        toolBar.addMember(textLabel);
        toolBar.addSpacer(6);
		textItem = new TextItem();
		textItem.setShowTitle(false);
		textItem.setWrapTitle(false);
		textItem.setDisabled(true);
		textItem.setHeight(18);
        toolBar.addFormItem(textItem);
        textItemButton = new ToolStripButton();  
        textItemButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/plus.png");
        textItemButton.setDisabled(true);
        toolBar.addButton(textItemButton);
        toolBar.addSpacer(6);
	}

	public TextItem getTextItem() {
		return textItem;
	}

	public ToolStripButton getTextItemButton() {
		return textItemButton;
	}

}
