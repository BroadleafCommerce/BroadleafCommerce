package org.broadleafcommerce.gwt.client.datasource.dynamic.module;

import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.tree.TreeNode;

public interface DataSourceModule {
	
	public boolean isCompatible(OperationType operationType);
	
	public void buildFields(final String[] customCriteria, Boolean overrideFieldSort, final AsyncCallback<DataSource> cb);
	
	public void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
	
	public void executeAdd(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
	
	public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
	
	public void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb);
	
	public String getLinkedValue();

	public void setLinkedValue(String linkedValue);
	
	public Entity buildEntity(Record record);
	
	public CriteriaTransferObject getCto(DSRequest request);
	
	public Record updateRecord(Entity entity, Record record, Boolean updateId);
	
	public Record buildRecord(Entity entity, Boolean updateId);
	
	public TreeNode[] buildRecords(DynamicResultSet result, String[] filterOutIds);
	
	public void setDataSource(AbstractDynamicDataSource dataSource);
	
	public String getCeilingEntityFullyQualifiedClassname();
	
}
