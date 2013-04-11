/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.datasource.dynamic.module;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.common.presentation.client.OperationType;

/**
 * 
 * @author jfischer
 *
 */
public interface DataSourceModule {
    
    public boolean isCompatible(OperationType operationType);
    
    public void buildFields(final String[] customCriteria, Boolean overrideFieldSort, final AsyncCallback<DataSource> cb);
    
    public void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
    
    public void executeAdd(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
    
    public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
    
    public void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
    
    public String getLinkedValue();

    public void setLinkedValue(String linkedValue);
    
    public Entity buildEntity(Record record, DSRequest request);
    
    public CriteriaTransferObject getCto(DSRequest request);
    
    public Record updateRecord(Entity entity, Record record, Boolean updateId);
    
    public Record buildRecord(Entity entity, Boolean updateId);
    
    public TreeNode[] buildRecords(DynamicResultSet result, String[] filterOutIds);
    
    public void setDataSource(AbstractDynamicDataSource dataSource);
    
    public String getCeilingEntityFullyQualifiedClassname();
    
}
