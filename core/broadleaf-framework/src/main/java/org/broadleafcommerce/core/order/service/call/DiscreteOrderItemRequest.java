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

package org.broadleafcommerce.core.order.service.call;

import org.broadleafcommerce.core.order.domain.BundleOrderItem;
import org.broadleafcommerce.core.order.domain.DiscreteOrderItemFeePrice;

import java.util.ArrayList;
import java.util.List;
public class DiscreteOrderItemRequest extends AbstractOrderItemRequest {

    protected BundleOrderItem bundleOrderItem;
    
    protected List<DiscreteOrderItemFeePrice> discreteOrderItemFeePrices = new ArrayList<DiscreteOrderItemFeePrice>();

    public DiscreteOrderItemRequest() {
        super();
    }

    public DiscreteOrderItemRequest(AbstractOrderItemRequest request) {
        setCategory(request.getCategory());
        setItemAttributes(request.getItemAttributes());
        setPersonalMessage(request.getPersonalMessage());
        setProduct(request.getProduct());
        setQuantity(request.getQuantity());
        setSku(request.getSku());
        setOrder(request.getOrder());
        setSalePriceOverride(request.getSalePriceOverride());
        setRetailPriceOverride(request.getRetailPriceOverride());
    }


    @Override
    public DiscreteOrderItemRequest clone() {
        DiscreteOrderItemRequest returnRequest = new DiscreteOrderItemRequest();
        copyProperties(returnRequest);
        returnRequest.setDiscreteOrderItemFeePrices(discreteOrderItemFeePrices);
        return returnRequest;
    }

    public BundleOrderItem getBundleOrderItem() {
        return bundleOrderItem;
    }
    
    public void setBundleOrderItem(BundleOrderItem bundleOrderItem) {
        this.bundleOrderItem = bundleOrderItem;
    }

    public List<DiscreteOrderItemFeePrice> getDiscreteOrderItemFeePrices() {
        return discreteOrderItemFeePrices;
    }

    public void setDiscreteOrderItemFeePrices(
            List<DiscreteOrderItemFeePrice> discreteOrderItemFeePrices) {
        this.discreteOrderItemFeePrices = discreteOrderItemFeePrices;
    }
}
