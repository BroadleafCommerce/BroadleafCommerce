package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditColumnTreeDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityColumnTreeDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

/**
 * Created by jfischer
 */
public interface StaticAssetsDisplay extends DynamicEditColumnTreeDisplay  {

    DynamicEntityColumnTreeDisplay getListDisplay();

    DynamicFormDisplay getDynamicFormDisplay();

    ToolStripButton getAddPageButton();

    void setAddPageButton(ToolStripButton addPageButton);

    ToolStripButton getAddPageFolderButton();

    void setAddPageFolderButton(ToolStripButton addPageFolderButton);

}
