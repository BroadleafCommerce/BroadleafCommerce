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
