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

package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author jfischer
 *
 */
public class DynamicResultSet implements IsSerializable, Serializable {
    
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

    public DynamicResultSet(Entity[] records, Integer totalRecords) {
        this.records = records;
        this.totalRecords = totalRecords;
    }

    public DynamicResultSet(ClassMetadata classMetaData) {
        this.classMetaData = classMetaData;
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
