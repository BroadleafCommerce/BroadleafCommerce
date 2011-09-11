package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;

/**
 * Created by jfischer
 */
public interface StaticAssetsDisplay extends DynamicEditDisplay {

    public SubItemDisplay getListLeafDisplay();
    public Img getPreviewImg();
    public Canvas getPreviewContainer();

}
