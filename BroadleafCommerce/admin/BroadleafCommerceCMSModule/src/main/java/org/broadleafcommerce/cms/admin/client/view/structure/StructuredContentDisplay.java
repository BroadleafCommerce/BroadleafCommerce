package org.broadleafcommerce.cms.admin.client.view.structure;

import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StructuredContentDisplay extends DynamicEditDisplay {

    public ComboBoxItem getCurrentContentType();

    public ToolStripButton getClearButton();

}
