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

package org.broadleafcommerce.admin.client.view.order;

import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEditDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

/**
 * 
 * @author jfischer
 *
 */
public interface OrderDisplay extends DynamicEditDisplay {
    
    public DynamicFormDisplay getDynamicFormDisplay();

    public DynamicEntityListDisplay getListDisplay();

    public OrderItemDisplay getOrderItemsDisplay();
    
    public SubItemDisplay getFulfillmentGroupDisplay();
    
    public SubItemDisplay getPaymentInfoDisplay();
    
    public GridStructureDisplay getAdditionalAttributesDisplay();
    
    public SubItemDisplay getOfferCodeDisplay();
    
    public GridStructureDisplay getOrderAdjustmentDisplay();
    
    public GridStructureDisplay getOrderItemAdjustmentDisplay();
    
    public GridStructureDisplay getFulfillmentGroupAdjustmentDisplay();
    
    public GridStructureView getOrderItemFeeDisplay();
    
}