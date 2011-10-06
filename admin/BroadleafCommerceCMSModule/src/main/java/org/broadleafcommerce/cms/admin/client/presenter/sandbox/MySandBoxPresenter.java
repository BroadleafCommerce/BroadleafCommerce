package org.broadleafcommerce.cms.admin.client.presenter.sandbox;

import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.cms.admin.client.datasource.sandbox.SandBoxItemListDataSourceFactory;
import org.broadleafcommerce.cms.admin.client.view.sandbox.MySandBoxDisplay;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.NullAsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSetupItem;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 10/5/11
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySandBoxPresenter extends SandBoxPresenter implements Instantiable {

    @Override
    public void setup() {
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("sandBoxItemDS", new SandBoxItemListDataSourceFactory(), null, new Object[]{BLCMain.currentViewKey, "fetch", "", "", "standard"}, new NullAsyncCallbackAdapter()));
        getPresenterSequenceSetupManager().addOrReplaceItem(new PresenterSetupItem("pendingSandBoxItemDS", new SandBoxItemListDataSourceFactory(), null, new Object[]{BLCMain.currentViewKey, "fetch", "", "", "pending"}, new AsyncCallbackAdapter() {
            @Override
            public void onSetupSuccess(DataSource dataSource) {
                CustomCriteriaListGridDataSource sandBoxItemDS = (CustomCriteriaListGridDataSource) getPresenterSequenceSetupManager().getDataSource("sandBoxItemDS");
                setupDisplayItems(sandBoxItemDS, dataSource);
                sandBoxItemDS.setupGridFields(new String[]{"auditable.createdBy.name", "description", "sandBoxItemType", "sandboxOperationType", "auditable.dateCreated", "auditable.dateUpdated"});
                ((CustomCriteriaListGridDataSource) dataSource).setAssociatedGrid(((MySandBoxDisplay) getDisplay()).getPendingGrid());
                ((CustomCriteriaListGridDataSource) dataSource).setupGridFields(new String[]{"auditable.createdBy.name", "description", "sandBoxItemType", "sandboxOperationType", "auditable.dateCreated", "auditable.dateUpdated"});
            }
        }));
    }
}
