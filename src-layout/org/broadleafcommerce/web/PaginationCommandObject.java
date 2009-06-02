/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.web;

import java.util.List;

public class PaginationCommandObject extends AjaxFormCommandObject{
    private int pageNumber = 0;
    private int pageSize = 1;
    private int listSize = -1;
    private List<?> displayList = null;
    private List<?> fullList = null;
    private String dataSource;
    private String containerId;
    private String previousLinkText = "&lt;- Previous";
    private String nextLinkText = "Next -&gt;";
    
    public String getPreviousLinkText() {
        return previousLinkText;
    }
    /**
     * The text to render for the previous link, HTML formatted
     * @param previousLinkText
     */
    public void setPreviousLinkText(String previousLinkText) {
        this.previousLinkText = previousLinkText;
    }
    public String getNextLinkText() {
        return nextLinkText;
    }
    /**
     * The test to render for the next link, HTML formatted
     * @param nextLinkText
     */
    public void setNextLinkText(String nextLinkText) {
        this.nextLinkText = nextLinkText;
    }
    public String getContainerId() {
        return containerId;
    }
    /**
     * The id of a div surrounding the paginated list tag. This is used when rendering the javascript
     * to enable AJAX page loads
     * @param containerId
     */
    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
    public String getDataSource() {
        return dataSource;
    }
    /**
     * The url to submit to for next and previous. Generally set to request.getRequestURI().toString()
     * @param dataSource
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * One of the two methods for populating the data in the list. Use this method when you have all
     * the items that will be in this list. The PaginationCommandObject will automatically return the
     * correct items in getDisplayList
     * @param fullList
     */
    public void setFullList(List<?> fullList) {
        this.fullList = fullList;
    }
    public List<?> getDisplayList() {
        if (displayList == null && fullList != null) {
            return fullList.subList(getStartIndex(), getEndIndex());
        }
        return displayList;
    }
    /**
     * One of the two methods for populating the data in the list. Use this method when getting all
     * list items is expensive. The PaginationCommandObject will display exactly these items regardless
     * of page size eg. if page size is 25 and a list of size 50 is passed in, 50 items will be
     * displayed. When using this method it is important to set listSize, or the isShowNext() logic will
     * always return false
     * @param displayList
     */
    public void setDisplayList(List<?> displayList) {
        this.displayList = displayList;
    }
    public int getPageNumber() {
        return pageNumber;
    }
    /**
     * The page number to display. Page 0 is the first page. Defaults to 0.
     * @param pageNumber
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public int getPageSize() {
        return pageSize;
    }
    /**
     * The number of items to display on a page.
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    /**
     * The index of the first item to be displayed based on pageSize and pageNumber. If listSize has
     * been set (either through setListSize or setFullList) and an otherwise invalid startIndex would
     * be returned, the greater of 0 or (listSize - pageSize) is returned
     * @return
     */
    public int getStartIndex() {
        int result = getPageNumber() * getPageSize();
        if (getListSize() >= 0 && result >= getListSize()) {
            result = getListSize() - getPageSize();
        }
        return Math.max(0,result);
    }
    /**
     * One past the index of the last item that will be displayed on this page.
     * @return
     */
    public int getEndIndex() {
        if (getListSize() < 0) return getStartIndex() + getPageSize();
        return Math.min(getListSize(), getStartIndex() + getPageSize());
    }
    /**
     * Either the size of fullList or the value earlier passed to setListSize. Defaults to -1.
     * @return
     */
    public int getListSize() {
        if (listSize == -1 && fullList != null) {
            return fullList.size();
        } 
        return listSize;
    }
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
    /**
     * Returns true unless pageNumber is 0
     * @return
     */
    public boolean isShowPrevious() {
        return getPageNumber() > 0;
    }
    /**
     * Returns true if there are additional items to display. 
     * @return
     */
    public boolean isShowNext() {
        return (getPageNumber() + 1) * getPageSize() < getListSize() ;
    }
}
