/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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


/**
 * Buttons to display on the frontend for a list grid. These will be displayed at the top of the {@link ListGrid} that they
 * are related to.
 * 
 * @author Andre Azzolini (aazzolini)
 * @see {@link ListGrid#addRowAction(ListGridAction)}
 * @see {@link ListGrid#addToolbarAction(ListGridAction)}
 */
public class ListGridAction implements Cloneable {
    
    public static final String ADD = "ADD";
    public static final String GEN_SKUS = "GEN_SKUS";
    public static final String REMOVE = "REMOVE";
    public static final String UPDATE = "UPDATE";
    public static final String MANUAL_FETCH = "MANUAL_FETCH";
    public static final String REFRESH = "REFRESH";
    public static final String VIEW = "VIEW";
    public static final String EDIT = "EDIT";
    public static final String TREE_ADD = "TREE_ADD";
    public static final String TREE_JUMP_TO_CONTEXT = "TREE_JUMP_TO_CONTEXT";
    public static final String SINGLE_SELECT = "SINGLE_SELECT";
    public static final String MULTI_SELECT = "MULTI_SELECT";
    public static final String ASSET_ADD = "ASSET_ADD";

    protected String buttonClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    protected String actionId = "";
    protected Boolean forListGridReadOnly = false;
    protected String actionUrlOverride = null;
    protected Boolean allCapable = false;
    protected Boolean singleActionOnly = false;
    protected String actionTargetEntity = "";
    protected Boolean requiresNonEmptyGrid = false;
    protected Boolean isConfirmEnabled = Boolean.FALSE;
    protected String confirmEnabledText = "";

    public ListGridAction(String actionId) {
        this.actionId = actionId;
    }
    
    /**
     * @see {@link #setButtonClass(String)}
     */
    public ListGridAction withButtonClass(String buttonClass) {
        setButtonClass(buttonClass);
        return this;
    }
    
    /**
     * @see {@link #setUrlPostfix(String)}
     */
    public ListGridAction withUrlPostfix(String urlPostfix) {
        setUrlPostfix(urlPostfix);
        return this;
    }
    
    /**
     * @see {@link #setIconClass(String)}
     */
    public ListGridAction withIconClass(String iconClass) {
        setIconClass(iconClass);
        return this;
    }

    /**
     *
     * @see {@link #setRequiresNonEmptyGrid(Boolean)}
     */
    public ListGridAction withRequiresNonEmptyGrid(Boolean requiresNonEmptyGrid) {
        setRequiresNonEmptyGrid(requiresNonEmptyGrid);
        return this;
    }
    
    /**
     * @see {@link #setDisplayText(String)}
     */
    public ListGridAction withDisplayText(String displayText) {
        setDisplayText(displayText);
        return this;
    }

    public ListGridAction withForListGridReadOnly(Boolean forListGridReadOnly) {
        setForListGridReadOnly(forListGridReadOnly);
        return this;
    }
    
    /**
     * @see {@link #setActionUrlOverride(String)}
     */
    public ListGridAction withActionUrlOverride(String actionUrlOverride) {
        setActionUrlOverride(actionUrlOverride);
        return this;
    }

    /**
     * @see {@link #setAllCapable(Boolean)}
     */
    public ListGridAction withAllCapable(Boolean allCapable) {
        setAllCapable(allCapable);
        return this;
    }

    /**
     * @see {@link #setSingleActionOnly(Boolean)}
     */
    public ListGridAction withSingleActionOnly(Boolean singleActionOnly) {
        setSingleActionOnly(singleActionOnly);
        return this;
    }

    /**
     * @see {@link #setActionTargetEntity(String)}
     */
    public ListGridAction withActionTargetEntity(String actionTargetEntity) {
        setActionTargetEntity(actionTargetEntity);
        return this;
    }

    public ListGridAction withIsConfirmEnabled(Boolean isConfirmEnabled) {
        setIsConfirmEnabled(isConfirmEnabled);
        return this;
    }

    public ListGridAction withConfirmEnabledText(String confirmEnabledText) {
        setConfirmEnabledText(confirmEnabledText);
        return this;
    }

    public String getButtonClass() {
        return buttonClass + (allCapable ? " all-capable" : "") + (singleActionOnly ? " single-action-only" : "");
    }

    public Boolean getForListGridReadOnly() {
        return forListGridReadOnly;
    }

    /**
     * Main intent is for the button class to be used in a JQuery selector for giving this button a click action. You could
     * technically also apply additional styling to this anchor but is not usually recommended.
     * <p>
     * An example JQuery selector would look like:
     *   $('body').on('click', 'button.some-class', function() {
     *       doSomeFunction()
     *   });
     * </p>
     * @param buttonClass
     */
    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }
    
    public String getUrlPostfix() {
        return urlPostfix;
    }
    
    /**
     * This means different things depending on where this action is on the list grid.
     * <ul>
     *  <li>If this is a toolbar action: this postfix will be appended onto the end of {@link ListGrid#getPath()} and 
     *  presented as a 'data-actionurl' attribute for the button</li>
     *  <li>This postfix will also be presented as a 'data-urlpostfix' attribute on the button</li>
     * </ul>
     * @param urlPostfix
     */
    public void setUrlPostfix(String urlPostfix) {
        this.urlPostfix = urlPostfix;
    }
    
    public String getIconClass() {
        return iconClass;
    }
    
    /**
     * Icon classes are displayed next to the {@link #getDisplayText()}. These can technically be whatever you like and
     * you can use css selectors to style them accordingly. That said, Broadleaf uses the FontAwesome set of icons out of
     * the box, and it is intended that <b>iconClass</b> is an icon from the FontAwesome framework. To see the icons that
     * are included, check out http://fortawesome.github.com/Font-Awesome/#icons-new
     * 
     * @param iconClass
     */
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
    
    public String getDisplayText() {
        return displayText;
    }
    
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public void setForListGridReadOnly(Boolean forListGridReadOnly) {
        this.forListGridReadOnly = forListGridReadOnly;
    }
    
    /**
     * Gets the manual override for the data-actionurl attribute on an action.
     * 
     * @return
     */
    public String getActionUrlOverride() {
        return actionUrlOverride;
    }
    
    /**
     * This is a manual override for the data-actionurl attribute for an listgrid action. The data-actionurl attribute on a
     * button is normally automatically computed by appending the postfix URL to the path of the list grid
     * 
     * @param actionUrlOverride
     */
    public void setActionUrlOverride(String actionUrlOverride) {
        this.actionUrlOverride = actionUrlOverride;
    }

    /**
     * Returns an Id that controllers can use to manipulate this action.   For example, if a
     * Controller wanted to not show the "Add" button that the system shows by default, they
     * could remove the action with an id of "ADD".   
     * 
     * @return
     */
    public String getActionId() {
        return actionId;
    }
    
    /**
     * @return whether or not the given list grid action is capable of acting on rows even when none are selected
     */
    public Boolean getAllCapable() {
        return allCapable;
    }
    
    /**
     * Sets whether or not the given list grid action is capable of acting on rows even when none are selected
     * 
     * @param allCapable
     */
    public void setAllCapable(Boolean allCapable) {
        this.allCapable = allCapable;
    }
    
    /**
     * @return whether or not this action can be performed only on a single item, regardless of whether the list grid
     * is multi-select capable or not.
     */
    public Boolean getSingleActionOnly() {
        return singleActionOnly;
    }

    /**
     * Sets where or not this action can only be performed on a single item. This is used to override multi-select
     * list grids for certain actions.
     * 
     * @param singleActionOnly
     */
    public void setSingleActionOnly(Boolean singleActionOnly) {
        this.singleActionOnly = singleActionOnly;
    }

    /**
     * Returns a fully qualified ceiling entity that this listgrid action targets
     *
     * @return
     */
    public String getActionTargetEntity() {
        return actionTargetEntity;
    }

    /**
     * This is used to specify a target entityType for use in listgrid actions
     *
     * @param actionTargetEntity
     */
    public void setActionTargetEntity(String actionTargetEntity) {
        this.actionTargetEntity = actionTargetEntity;
    }

    /**
     * If a list grid action should be disabled unless there are one or more rows in the grid, this property should be true
     *
     * @return
     */
    public Boolean getRequiresNonEmptyGrid() {
        return requiresNonEmptyGrid;
    }

    public void setRequiresNonEmptyGrid(Boolean requiresNonEmptyGrid) {
        this.requiresNonEmptyGrid = requiresNonEmptyGrid;
    }

    public Boolean getIsConfirmEnabled() {
        return isConfirmEnabled == null ? Boolean.FALSE : isConfirmEnabled;
    }

    public void setIsConfirmEnabled(Boolean confirmEnabled) {
        isConfirmEnabled = confirmEnabled;
    }

    public String getConfirmEnabledText() {
        return confirmEnabledText;
    }

    public void setConfirmEnabledText(String confirmEnabledText) {
        this.confirmEnabledText = confirmEnabledText;
    }

    @Override
    public ListGridAction clone() {
        ListGridAction cloned = new ListGridAction(actionId);
        cloned.buttonClass = buttonClass;
        cloned.displayText = displayText;
        cloned.iconClass = iconClass;
        cloned.urlPostfix = urlPostfix;
        cloned.forListGridReadOnly = forListGridReadOnly;
        cloned.allCapable = allCapable;
        cloned.actionId = actionId;
        cloned.actionUrlOverride = actionUrlOverride;
        cloned.singleActionOnly = singleActionOnly;
        cloned.requiresNonEmptyGrid = requiresNonEmptyGrid;
        return cloned;
    }
}
