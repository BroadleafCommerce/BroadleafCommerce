package org.broadleafcommerce.openadmin.client.setup;

import com.smartgwt.client.data.DataSource;

/**
 * Created by jfischer
 */
public class NullAsyncCallbackAdapter extends AsyncCallbackAdapter {

    @Override
    public void onSetupSuccess(DataSource dataSource) {
        //do nothing
    }

}
