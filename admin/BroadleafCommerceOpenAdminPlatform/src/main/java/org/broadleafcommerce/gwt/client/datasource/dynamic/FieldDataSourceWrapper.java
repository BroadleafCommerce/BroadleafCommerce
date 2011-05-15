package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;

public class FieldDataSourceWrapper extends DataSource {
	
	protected DataSource delegate;

	public FieldDataSourceWrapper(DataSource delegate) {
		this.delegate = delegate;
		setClientOnly(true);
	}

	@Override
	protected Object transformRequest(DSRequest dsRequest) {
		Criteria criteria = dsRequest.getCriteria();
		String entered = (String) criteria.getValues().get("title");
        
		dsRequest.setUseSimpleHttp(true);
        String requestId = dsRequest.getRequestId ();
        DSResponse response = new DSResponse();
        response.setAttribute ("clientContext", dsRequest.getAttributeAsObject ("clientContext"));
        response.setStatus(0);
        List<Record> records = new ArrayList<Record>();
        for (DataSourceField field : delegate.getFields()) {
        	String title = field.getTitle();
        	if (title == null) {
        		title = field.getName();
        	}
        	if (entered == null || entered.equals("") || (title != null && title.toLowerCase().startsWith(entered.toLowerCase()))) {
	        	Record record = new Record();
	        	for (String attribute : field.getAttributes()) {
	        		record.setAttribute(attribute, field.getAttribute(attribute));
	        	}
	        	records.add(record);
        	}
        }
        Record[] recordArray = new Record[]{};
        recordArray = records.toArray(recordArray);
        response.setData(recordArray);
        response.setTotalRows(recordArray.length);
        
        processResponse(requestId, response);
        
        return dsRequest.getData();
	}
	
}
