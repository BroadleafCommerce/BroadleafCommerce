/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.setup;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;

/**
 * 
 * @author jfischer
 *
 */
public abstract class AsyncCallbackAdapter implements AsyncCallback<DataSource> {
	
	private PresenterSequenceSetupManager manager;
	
	protected void registerDataSourceSetupManager(PresenterSequenceSetupManager manager) {
		this.manager = manager;
	}

	@Override
    public void onFailure(Throwable arg0) {
		//do nothing - let the framework handle the exception
		//override to custom handle failures
	}

    public final PresenterSequenceSetupManager getDataSourceSetupManager() {
        return manager;
    }

    public final void onBatchSuccess(DataSource dataSource) {
		onSetupSuccess(dataSource);
        if (manager != null) {
            if (dataSource != null) {
                manager.addDataSource((DynamicEntityDataSource) dataSource);
            }
        }
	}

    @Override
    public final void onSuccess(DataSource dataSource) {
        onSetupSuccess(dataSource);
        if (manager != null) {
            manager.addDataSource((DynamicEntityDataSource) dataSource);
            notifyManager();
        }
    }

    public final void notifyManager() {
        if (manager != null) {
            manager.next();
        }
    }
	
	public abstract void onSetupSuccess(DataSource dataSource);
	
}
