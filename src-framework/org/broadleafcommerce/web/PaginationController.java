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

import java.util.Map;

/**
 * Pagination Controller's default implementation assumes that successView points to a JSP that contains
 * a div whose sole contents are a <jsp:include/> that includes the same JSP as ajaxView. Inside ajaxView
 * is the <sc:paginatedList/>
 * 
 * The most basic implementation only needs to implement populatePaginatedList, setting the list on the
 * default PaginationCommandObject. For more advanced pagination scenarios, such as search strings,
 * filters, or sorting, PaginationControllerObject should be subclassed and formBackingObject overriden.
 * 
 * To populate additional elements of the model, override populateStandard and call super.populateStandard.
 * 
 * @author dmclain
 *
 */
public abstract class PaginationController extends AjaxFormController {

    private String paginationObjectName = "paginationObject";

    public String getPaginationObjectName() {
        return paginationObjectName;
    }

    /**
     * The name by which the PaginationCommandObject will be named for the jsp.
     * @param paginationObjectName
     */
    public void setPaginationObjectName(String paginationObjectName) {
        this.paginationObjectName = paginationObjectName;
    }

    /**
     * populatePaginatedList is where the logic to retrieve the list items is implemented. There are two
     * strategies, pulling all items or only pulling the displayed subset. The easiest to implement is
     * to retrieve all items that are in the list and call PaginationCommandObject.setFullList, and the
     * paginatedList will determine what items to display. For expensive retrievals of large lists,
     * only pull the PaginationCommandObject.getPageSize() subset starting at
     * PaginationCommandObject.getStartIndex(). If using setDisplayList, the next button will not display
     * unless PaginationCommandObject.setListSize is called with a number greater then or equal to
     * PaginationCommandObject.getEndIndex()
     * @param model
     * @param object
     */
    protected abstract void populatePaginatedList(Map<String, Object> model, PaginationCommandObject object);

    //    @Override
    //    protected Object formBackingObject(HttpServletRequest request)throws ServletException {
    //        PaginationCommandObject object = new PaginationCommandObject();
    //        object.setPageNumber(0);
    //        object.setPageSize(25);
    //        String requestURI = request.getRequestURI();
    //        object.setDataSource(requestURI.substring(request.getContextPath().length()));
    //        object.setContainerId("listContainer");
    //        return object;
    //    }

    @Override
    protected void populateAjax(Map<String, Object> model, Object object) {
        populatePaginatedList(model, (PaginationCommandObject) object);
        model.put(getPaginationObjectName(), object);
    }

    @Override
    protected void populateStandard(Map<String, Object> model, Object object) {
        populatePaginatedList(model, (PaginationCommandObject) object);
        model.put(getPaginationObjectName(), object);
    }

}
