package org.broadleafcommerce.cms.admin.client.presenter.file;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresenter;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/14/11
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class LeafAssetPresenter extends SubPresenter {

    public LeafAssetPresenter(SubItemDisplay display) {
        super(display);
    }

    public LeafAssetPresenter(SubItemDisplay display, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
        super(display, showDisabledState, canEdit, showId);
    }

    @Override
    public void load(Record associatedRecord, AbstractDynamicDataSource dataSource, final DSCallback cb) {
        this.associatedRecord = associatedRecord;
		this.abstractDynamicDataSource = dataSource;
		String id = dataSource.getPrimaryKeyValue(associatedRecord);
        if (id == null) {
            id = "null";
        }
		((PresentationLayerAssociatedDataSource) display.getGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
            public void execute(DSResponse response, Object rawData, DSRequest request) {
                setStartState();
                if (cb != null) {
                    cb.execute(response, rawData, request);
                }
            }
        });
    }
}
