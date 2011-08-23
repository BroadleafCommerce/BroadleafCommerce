package org.broadleafcommerce.cms.admin.client.view.pages;

import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PagesDisplay extends DynamicEditDisplay {

    public DynamicEntityListDisplay getListDisplay();

    public DynamicFormDisplay getDynamicFormDisplay();

}
