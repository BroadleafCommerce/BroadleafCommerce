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
package org.broadleafcommerce.openadmin.web.form.component;


import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.util.TypedPredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Grouping of buttons to display on the frontend for a list grid. These will be displayed at the top of the {@link ListGrid} that they
 * are related to.
 * 
 * @author Chris Kittrell (ckittrell)
 * @see {@link ListGrid#addRowActionGroup(ListGridActionGroup)}
 * @see {@link ListGrid#addToolbarActionGroup(ListGridActionGroup)}
 */
public class ListGridActionGroup implements Cloneable {

    protected String name = "";
    protected String actionGroupClass = "";
    protected List<ListGridAction> listGridActions = new ArrayList<ListGridAction>();
    
    /**
     * @see {@link #setName(String)}
     */
    public ListGridActionGroup withName(String name) {
        setName(name);
        return this;
    }
    
    /**
     * @see {@link #setActionGroupClass(String)}
     */
    public ListGridActionGroup withActionGroupClass(String actionGroupClass) {
        setActionGroupClass(actionGroupClass);
        return this;
    }

    /**
     * Grabs a filtered list of actions filtered by whether or not they match the same readonly state as the listgrid
     * and are thus shown on the screen
     */
    @SuppressWarnings("unchecked")
    public List<ListGridAction> getActiveActions(final boolean listGridIsReadOnly) {
        return (List<ListGridAction>) CollectionUtils.select(getListGridActions(), new TypedPredicate<ListGridAction>() {

            @Override
            public boolean eval(ListGridAction action) {
                return action.getForListGridReadOnly().equals(listGridIsReadOnly);
            }
        });
    }

    public ListGridAction findAction(String actionId) {
        for (ListGridAction action : getListGridActions()) {
            if (action.getActionId().equals(actionId)) {
                return action;
            }
        }
        return null;
    }

    public void addAction(ListGridAction action) {
        getListGridActions().add(action);
    }

    public void removeAllActions() {
        getListGridActions().clear();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActionGroupClass() {
        return actionGroupClass;
    }

    public void setActionGroupClass(String actionGroupClass) {
        this.actionGroupClass = actionGroupClass;
    }

    public List<ListGridAction> getListGridActions() {
        return listGridActions;
    }

    public void setListGridActions(List<ListGridAction> listGridActions) {
        this.listGridActions = listGridActions;
    }
    
    @Override
    public ListGridActionGroup clone() {
        ListGridActionGroup cloned = new ListGridActionGroup();
        cloned.name = name;
        cloned.actionGroupClass = actionGroupClass;
        cloned.listGridActions = listGridActions;
        return cloned;
    }
}
