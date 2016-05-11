/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.checkout.section;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class CheckoutSectionDTO {

    protected CheckoutSectionViewType view;
    protected CheckoutSectionStateType state;
    protected boolean populated;
    protected String helpMessage;

    public CheckoutSectionDTO(CheckoutSectionViewType view, boolean populated) {
        this.view = view;
        this.populated = populated;
        this.state = CheckoutSectionStateType.INACTIVE;
    }

    public CheckoutSectionViewType getView() {
        return view;
    }

    public void setView(CheckoutSectionViewType view) {
        this.view = view;
    }

    public CheckoutSectionStateType getState() {
        return state;
    }

    public void setState(CheckoutSectionStateType state) {
        this.state = state;
    }

    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    public String getHelpMessage() {
        return helpMessage;
    }

    public void setHelpMessage(String helpMessage) {
        this.helpMessage = helpMessage;
    }
}
