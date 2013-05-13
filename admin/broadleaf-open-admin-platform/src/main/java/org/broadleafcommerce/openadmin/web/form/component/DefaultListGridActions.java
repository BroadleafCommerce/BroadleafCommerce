/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.form.component;


public class DefaultListGridActions {
    
    // Actions for the main list grid toolbar
    public static final ListGridAction ADD = new ListGridAction()
        .withButtonClass("sub-list-grid-add")
        .withUrlPostfix("/add")
        .withIconClass("icon-plus")
        .withDisplayText("Add");
    
    public static final ListGridAction REORDER = new ListGridAction()
        .withButtonClass("sub-list-grid-reorder")
        .withUrlPostfix("/update")
        .withIconClass("icon-move")
        .withDisplayText("Reorder");

    // Actions for row-level
    public static final ListGridAction REMOVE = new ListGridAction()
        .withButtonClass("sub-list-grid-remove")
        .withUrlPostfix("/delete")
        .withIconClass("icon-remove")
        .withDisplayText("Delete");
    
    public static final ListGridAction UPDATE = new ListGridAction()
        .withButtonClass("sub-list-grid-update")
        .withIconClass("icon-pencil")
        .withDisplayText("Edit");

    public static final ListGridAction VIEW = new ListGridAction()
        .withButtonClass("sub-list-grid-view")
        .withIconClass("icon-book")
        .withDisplayText("View")
        .withForListGridReadOnly(true);
    
}
