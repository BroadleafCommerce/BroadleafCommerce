package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityColumnTreeDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

/**
 * Created by jfischer
 */
public interface StaticAssetsDisplay {
    DynamicEntityColumnTreeDisplay getListDisplay();

    DynamicFormDisplay getDynamicFormDisplay();

    ToolStripButton getAddPageButton();

    void setAddPageButton(ToolStripButton addPageButton);

    ToolStripButton getAddPageFolderButton();

    void setAddPageFolderButton(ToolStripButton addPageFolderButton);

    ComboBoxItem getCurrentLocale();

    void setCurrentLocale(ComboBoxItem currentLocale);
}
