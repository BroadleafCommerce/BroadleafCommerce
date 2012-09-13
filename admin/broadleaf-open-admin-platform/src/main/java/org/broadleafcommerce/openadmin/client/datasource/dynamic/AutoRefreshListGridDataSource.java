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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;

/**
 * 
 * @author jfischer
 *
 */
public class AutoRefreshListGridDataSource extends ListGridDataSource {

	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public AutoRefreshListGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request, final DSResponse response, String[] customCriteria, final AsyncCallback<DataSource> cb) {
		super.executeAdd(requestId, request, response, customCriteria, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource arg0) {
				response.setInvalidateCache(true);
				if (cb != null) {
					cb.onSuccess(arg0);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				if (cb != null) {
					cb.onFailure(arg0);
				}
			}
			
		});
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request, final DSResponse response, String[] customCriteria, final AsyncCallback<DataSource> cb) {
		super.executeUpdate(requestId, request, response, customCriteria, new AsyncCallbackAdapter() {
			public void onSetupSuccess(DataSource arg0) {
				response.setInvalidateCache(true);
				if (cb != null) {
					cb.onSuccess(arg0);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				if (cb != null) {
					cb.onFailure(arg0);
				}
			}
			
		});
	}
	
	
}
