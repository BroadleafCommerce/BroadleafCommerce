package org.broadleafcommerce.gwt.client.setup;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public abstract class AsyncCallbackAdapter implements AsyncCallback<DataSource> {
	
	private PresenterSequenceSetupManager manager;
	
	protected void registerDataSourceSetupManager(PresenterSequenceSetupManager manager) {
		this.manager = manager;
	}

	public void onFailure(Throwable arg0) {
		//do nothing - let the framework handle the exception
		//override to custom handle failures
	}

	public void onSuccess(DataSource dataSource) {
		onSetupSuccess(dataSource);
		manager.next();
	}
	
	public abstract void onSetupSuccess(DataSource dataSource);
	
}
