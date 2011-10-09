package org.broadleafcommerce.cms.admin.client.view.file;

import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplayWithoutForm;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

/**
 * Created by jfischer
 */
public interface StaticAssetsDisplay extends DynamicEditDisplayWithoutForm {

    public SubItemDisplay getListLeafDisplay();

    public GridStructureDisplay getAssetDescriptionDisplay();

}
