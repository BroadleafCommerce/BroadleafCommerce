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

package org.broadleafcommerce.admin.client.datasource.promotion;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;

/**
 * 
 * @author jfischer
 *
 */
public class OfferListDataSource extends ListGridDataSource {

	/**
	 * @param name
	 * @param persistencePerspective
	 * @param service
	 * @param modules
	 */
	public OfferListDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name, persistencePerspective, service, modules);
	}

	@Override
	public void buildFields(String[] customCriteria, Boolean overrideFieldSort, AsyncCallback<DataSource> cb) {
		super.buildFields(new String[]{"Offer"}, overrideFieldSort, cb);
	}

	@Override
	protected void executeFetch(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeFetch(requestId, request, response, new String[]{"Offer"}, cb);
	}

	@Override
	protected void executeAdd(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeAdd(requestId, request, response, new String[]{"Offer"}, cb);
	}

	@Override
	protected void executeUpdate(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeUpdate(requestId, request, response, new String[]{"Offer"}, cb);
	}

	@Override
	protected void executeRemove(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
		super.executeRemove(requestId, request, response, new String[]{"Offer"}, cb);
	}

	
}
