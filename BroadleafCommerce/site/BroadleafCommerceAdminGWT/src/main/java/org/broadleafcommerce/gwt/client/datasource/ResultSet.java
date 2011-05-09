package org.broadleafcommerce.gwt.client.datasource;

import java.io.Serializable;
import java.util.List;

public class ResultSet<Item extends Serializable> implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private List<Item> resultList;
    private Integer totalRecords;
    
    // for serialization purposes
    protected ResultSet() {
    }
    
    public ResultSet(List<Item> resultList, Integer totalRecords) {
        this.resultList = resultList;
        this.totalRecords = totalRecords;
    }
    
    public List<Item> getResultList() {
        return resultList;
    }
    
    public Integer getTotalRecords() {
        return totalRecords;
    }

	public void setResultList(List<Item> resultList) {
		this.resultList = resultList;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}
    
}
