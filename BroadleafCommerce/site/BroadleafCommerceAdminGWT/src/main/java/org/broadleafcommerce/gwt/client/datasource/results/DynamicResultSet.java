package org.broadleafcommerce.gwt.client.datasource.results;

import java.io.Serializable;

public class DynamicResultSet implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	private ClassMetadata classMetaData;
	private Entity[] records;
    private Integer totalRecords;
    
    public DynamicResultSet() {
    	//do nothing
    }
    
    public DynamicResultSet(ClassMetadata classMetaData, Entity[] records, Integer totalRecords) {
        this.records = records;
        this.classMetaData = classMetaData;
        this.totalRecords = totalRecords;
    }

	public ClassMetadata getClassMetaData() {
		return classMetaData;
	}

	public void setClassMetaData(ClassMetadata classMetaData) {
		this.classMetaData = classMetaData;
	}

	public Integer getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	public Entity[] getRecords() {
		return records;
	}

	public void setRecords(Entity[] records) {
		this.records = records;
	}
    
}
