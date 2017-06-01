/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.openadmin.server.service.type.FetchType;

import java.io.Serializable;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicResultSet implements Serializable {

    private static final long serialVersionUID = 1L;

    private ClassMetadata classMetaData;
    private Entity[] records;
    private Integer pageSize;
    private Integer startIndex;
    private Integer totalRecords;
    private Integer batchId;
    private Long firstId;
    private Long lastId;
    private Integer upperCount;
    private Integer lowerCount;
    private FetchType fetchType;
    private Boolean totalCountLessThanPageSize;
    private Boolean promptSearch;

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

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getFirstId() {
        return firstId;
    }

    public void setFirstId(Long firstId) {
        this.firstId = firstId;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Integer getUpperCount() {
        return upperCount;
    }

    public void setUpperCount(Integer upperCount) {
        this.upperCount = upperCount;
    }

    public Integer getLowerCount() {
        return lowerCount;
    }

    public void setLowerCount(Integer lowerCount) {
        this.lowerCount = lowerCount;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    public Boolean getTotalCountLessThanPageSize() {
        return totalCountLessThanPageSize;
    }

    public void setTotalCountLessThanPageSize(Boolean totalCountLessThanPageSize) {
        this.totalCountLessThanPageSize = totalCountLessThanPageSize;
    }

    public Boolean getPromptSearch() {
        return promptSearch;
    }

    public void setPromptSearch(Boolean promptSearch) {
        this.promptSearch = promptSearch;
    }
}
