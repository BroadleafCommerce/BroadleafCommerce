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


/**
 * Buttons to display on the frontend for a list grid. These will be displayed at the top of the {@link ListGrid} that they
 * are related to.
 * 
 * @author Andre Azzolini (aazzolini)
 * @see {@link ListGrid#addRowAction(ListGridAction)}
 * @see {@link ListGrid#addToolbarAction(ListGridAction)}
 */
public class ListGridAction implements Cloneable {
    
    protected String buttonClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    protected Boolean forListGridReadOnly = false;
    
    public ListGridAction() {
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

    public String getButtonClass() {
        return buttonClass;
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
     *  <li>If this is a row action: this postfix will be presented as a 'data-urlpostfix' attribute on the button</li>
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

    @Override
    public ListGridAction clone() {
        ListGridAction cloned = new ListGridAction();
        cloned.buttonClass = buttonClass;
        cloned.displayText = displayText;
        cloned.iconClass = iconClass;
        cloned.urlPostfix = urlPostfix;
        cloned.forListGridReadOnly = forListGridReadOnly;
        return cloned;
    }
}
