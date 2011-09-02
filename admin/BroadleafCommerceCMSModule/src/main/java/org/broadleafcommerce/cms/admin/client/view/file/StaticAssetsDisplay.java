package org.broadleafcommerce.cms.admin.client.view.file;

import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

/**
 * Created by jfischer
 */
public interface StaticAssetsDisplay extends DynamicEditDisplay {

    public DynamicEntityListDisplay getListLeafDisplay();

    public DynamicFormDisplay getDynamicFormLeafDisplay();

}
