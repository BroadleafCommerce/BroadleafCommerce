/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.domain;

/**
 * Encapsulate params related to fetch paging
 *
 * @author Jeff Fischer
 */
public class FetchPageRequest {

    protected Integer startIndex;
    protected Integer maxIndex;
    protected Long lastId;
    protected Long firstId;
    protected Integer upperCount;
    protected Integer lowerCount;
    protected Integer pageSize;

    public FetchPageRequest withStartIndex(Integer startIndex) {
        setStartIndex(startIndex);
        return this;
    }

    public FetchPageRequest withMaxIndex(Integer maxIndex) {
        setMaxIndex(maxIndex);
        return this;
    }

    public FetchPageRequest withLastId(Long lastId) {
        setLastId(lastId);
        return this;
    }

    public FetchPageRequest withFirstId(Long firstId) {
        setFirstId(firstId);
        return this;
    }

    public FetchPageRequest withUpperCount(Integer upperCount) {
        setUpperCount(upperCount);
        return this;
    }

    public FetchPageRequest withLowerCount(Integer lowerCount) {
        setLowerCount(lowerCount);
        return this;
    }

    public FetchPageRequest withPageSize(Integer pageSize) {
        setPageSize(pageSize);
        return this;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(Integer maxIndex) {
        this.maxIndex = maxIndex;
    }

    public Long getLastId() {
        return lastId;
    }

    public void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public Long getFirstId() {
        return firstId;
    }

    public void setFirstId(Long firstId) {
        this.firstId = firstId;
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

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
