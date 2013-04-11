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

package org.broadleafcommerce.admin.client.presenter.order;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntityEditDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureView;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * 
 * @author priyesh patel
 *
 */
public class OrderItemPriceDetailDialog extends EntityEditDialog {

    protected DynamicForm commentsForm;
    private GridStructureView orderItemPriceDetailAdjustmentDisplay;
    public OrderItemPriceDetailDialog() {
        super();

    }

    @Override
    public void addOtherItems(Layout layout) {
        super.addOtherItems(layout);
        setOrderItemPriceDetailAdjustmentDisplay(new GridStructureView(BLCMain.getMessageManager().getString("orderItemPriceDetailAjustmentListTitle"), false, false));
        getOrderItemPriceDetailAdjustmentDisplay().getAddButton().setVisible(false);
        getOrderItemPriceDetailAdjustmentDisplay().getRemoveButton().setVisible(false);

        layout.addMember(getOrderItemPriceDetailAdjustmentDisplay());

    }

    public DynamicForm getCommentsDynamicForm() {
        return commentsForm;
    }

    GridStructureView getOrderItemPriceDetailAdjustmentDisplay() {
        return orderItemPriceDetailAdjustmentDisplay;
    }

    void setOrderItemPriceDetailAdjustmentDisplay(GridStructureView orderItemPriceDetailAdjustmentDisplay) {
        this.orderItemPriceDetailAdjustmentDisplay = orderItemPriceDetailAdjustmentDisplay;
    }
}
