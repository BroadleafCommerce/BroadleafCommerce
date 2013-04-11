
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

package org.broadleafcommerce.openadmin.client.view.dynamic.grid;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.FilterEditorSubmitEvent;
import com.smartgwt.client.widgets.grid.events.FilterEditorSubmitHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GridHelper {

    protected List<HandlerRegistration> extendedFetchDataHandlerRegistration;

    protected void clearMembers(Canvas grid, Canvas pane) {
        if (pane == grid) {
            return;
        }

        if (pane instanceof TabSet) {
            for (Tab t : ((TabSet) pane).getTabs()) {
                clearMembers(grid, t.getPane());
            }
        }

        if (pane instanceof com.smartgwt.client.widgets.layout.Layout) {
            for (Canvas c : ((com.smartgwt.client.widgets.layout.Layout) pane).getMembers()) {
                clearMembers(grid, c);
            }
        }

        if (pane instanceof com.smartgwt.client.widgets.grid.ListGrid) {
            ((com.smartgwt.client.widgets.grid.ListGrid) pane).setData(new ListGridRecord[] {});
        }

        if (pane instanceof DynamicForm) {
            ((DynamicForm) pane).clearValues();
        }
    }

    public void addUpdateHandlers(final ListGrid grid, final TabSet topTabSet) {
        addUpdateHandlers(grid, topTabSet.getParentElement());
    }

    public void addUpdateHandlers(final ListGrid grid, final Canvas topTabSet) {
        grid.addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                clearMembers(grid, topTabSet);
            }
        });
        
        grid.addFilterEditorSubmitHandler(new FilterEditorSubmitHandler() {
            @Override
            public void onFilterEditorSubmit(FilterEditorSubmitEvent event) {
                clearMembers(grid, topTabSet);
            }
        });
        
        grid.addFetchDataHandler(new FetchDataHandler() {
            @Override
            public void onFilterData(com.smartgwt.client.widgets.events.FetchDataEvent event) {
                clearMembers(grid, topTabSet);
            }
        });
    }

    public void addSubPresentableHandlers(final ListGrid grid, final SubPresentable... permissionsPresenters) {
        for (final SubPresentable permissionsPresenter : permissionsPresenters) {
            HandlerRegistration extendedFetchDataHandlerRegistration = grid.addFetchDataHandler(new FetchDataHandler() {
                @Override
                public void onFilterData(FetchDataEvent event) {
                    
                    permissionsPresenter.disable();
                }
            });
            add(extendedFetchDataHandlerRegistration);

            extendedFetchDataHandlerRegistration = grid.addSelectionChangedHandler(new SelectionChangedHandler() {
                @Override
                public void onSelectionChanged(SelectionEvent event) {
                    if(event.getState()) {
                        permissionsPresenter.enable();  
                    }
                }
            });
            add(extendedFetchDataHandlerRegistration);
            
            extendedFetchDataHandlerRegistration = grid.addFilterEditorSubmitHandler(new FilterEditorSubmitHandler() {
                @Override
                public void onFilterEditorSubmit(FilterEditorSubmitEvent event) {
                    permissionsPresenter.disable();
                }
            });
            add(extendedFetchDataHandlerRegistration);
        }
    }

    protected void add(HandlerRegistration handlerRegistration) {
        if (extendedFetchDataHandlerRegistration == null) {
            extendedFetchDataHandlerRegistration = new ArrayList<HandlerRegistration>();
        }
        extendedFetchDataHandlerRegistration.add(handlerRegistration);
    }

    public void traverseTreeAndAddHandlers(ListGrid grid) {
        Canvas parent = grid.getParentElement();
        parent = parent.getParentElement();
        if (parent.getParentElement() != null) {
            // make sure the parent id is there or it wont know how to treverse down the right tree.
            // (must set canvas.setParentElement() to the parent in the view.)
            parent = parent.getParentElement();
        } else {
            java.util.logging.Logger.getLogger(BLCMain.class.getName()).log(Level.WARNING,"Parent element not set, please use setParentElement() so that the grid can enable/disable elements on the right hand side.");
            return;
        }
        addUpdateHandlers(grid, parent);
    }
}
