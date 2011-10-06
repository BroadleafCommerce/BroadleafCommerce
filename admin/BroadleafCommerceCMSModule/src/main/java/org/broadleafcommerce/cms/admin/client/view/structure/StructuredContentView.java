package org.broadleafcommerce.cms.admin.client.view.structure;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredContentView extends HLayout implements Instantiable, StructuredContentDisplay {

    protected DynamicEntityListView listDisplay;
    protected DynamicFormView dynamicFormDisplay;
    protected ComboBoxItem currentContentType = new ComboBoxItem();
    protected ToolStripButton clearButton;

    public StructuredContentView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("structureLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);
		listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource);
        listDisplay.getGrid().setHoverMoveWithMouse(true);
        listDisplay.getGrid().setCanHover(true);
        listDisplay.getGrid().setShowHover(true);
        listDisplay.getGrid().setHoverOpacity(75);
        listDisplay.getGrid().setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record.getAttributeAsBoolean("lockedFlag")) {
                    return "Last Updated By <B>" + record.getAttribute("auditable.updatedBy.name") + "</B> On <B>" + record.getAttribute("auditable.dateUpdated") + "</B>.";
                }
                return null;
            }
        });
        
        Label label = new Label();
        label.setContents(BLCMain.getMessageManager().getString("contentTypeFilterTitle"));
        label.setWrap(false);
        listDisplay.getToolBar().addMember(label);
        clearButton = new ToolStripButton();  
        clearButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Enterprise/images/headerIcons/close.png");
        listDisplay.getToolBar().addButton(clearButton);
        currentContentType.setShowTitle(false);
        currentContentType.setWidth(120);
        currentContentType.setOptionDataSource(additionalDataSources[0]);
        currentContentType.setDisplayField("name");
        currentContentType.setValueField("id");
        currentContentType.setDefaultToFirstOption(false);
        listDisplay.getToolBar().addFormItem(currentContentType);

        leftVerticalLayout.addMember(listDisplay);
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), entityDataSource);

        addMember(leftVerticalLayout);
        addMember(dynamicFormDisplay);
	}

    public Canvas asCanvas() {
		return this;
	}

	public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

    public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}

    public ComboBoxItem getCurrentContentType() {
        return currentContentType;
    }

    public ToolStripButton getClearButton() {
        return clearButton;
    }
}
