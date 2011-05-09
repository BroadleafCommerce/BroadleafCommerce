package org.broadleafcommerce.gwt.server;

import java.io.Serializable;
import java.util.List;

public class CustomFetchResponse {
	
	private List<Serializable> records;
	private Integer totalRecords;
	
	public List<Serializable> getRecords() {
		return records;
	}
	
	public void setRecords(List<Serializable> records) {
		this.records = records;
	}
	
	public Integer getTotalRecords() {
		return totalRecords;
	}
	
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

}
