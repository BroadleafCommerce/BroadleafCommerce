package org.broadleafcommerce.gwt.client.datasource.dynamic.operation;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public abstract class AsyncCallbackAdapter implements AsyncCallback<DataSource> {

	public void onFailure(Throwable arg0) {
		//do nothing - let the framework handle the exception
		//override to custom handle failures
	}

}
