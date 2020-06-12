/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.dto;

import org.broadleafcommerce.openadmin.web.form.entity.Tab;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.broadleafcommerce.openadmin.server.service.type.FetchType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
@JsonAutoDetect
public class DynamicResultSet implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private ClassMetadata classMetaData;

    @JsonProperty
    private Entity[] records;

    @JsonProperty
    private Integer pageSize;

    @JsonProperty
    private Integer startIndex;

    @JsonProperty
    private Integer totalRecords;

    @JsonProperty
    private Integer batchId;

    @JsonProperty
    private Long firstId;

    @JsonProperty
    private Long lastId;

    @JsonProperty
    private Integer upperCount;

    @JsonProperty
    private Integer lowerCount;

    @JsonProperty
    private FetchType fetchType;

    @JsonProperty
    private Boolean totalCountLessThanPageSize;

    @JsonProperty
    private Boolean promptSearch;

    @JsonIgnore
    private Map<String, Tab> unselectedTabMetadata = new HashMap<String, Tab>();

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
        return totalRecords == null ? 0 : totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Entity[] getRecords() {
        return records == null ? new Entity[0] : records;
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
        return startIndex == null ? 0 : startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize == null ? 0 : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public Map<String, Tab> getUnselectedTabMetadata() {
        return unselectedTabMetadata;
    }


    public void setUnselectedTabMetadata(Map<String, Tab> unselectedTabMetadata) {
        this.unselectedTabMetadata = unselectedTabMetadata;
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
