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
package org.broadleafcommerce.core.web.checkout.model;

import org.broadleafcommerce.core.order.service.call.OrderMultishipOptionDTO;

import java.io.Serializable;
import java.util.List;

/**
 * This form is used to bind multiship options in a way that doesn't require
 * the actual objects to be instantiated -- we handle that at the controller
 * level.
 * 
 * @see OrderMultishipOptionDTO
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class OrderMultishipOptionForm implements Serializable {

    private static final long serialVersionUID = -5989681894142759293L;
    
    protected List<OrderMultishipOptionDTO> options;

    public List<OrderMultishipOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OrderMultishipOptionDTO> options) {
        this.options = options;
    }
    
}
