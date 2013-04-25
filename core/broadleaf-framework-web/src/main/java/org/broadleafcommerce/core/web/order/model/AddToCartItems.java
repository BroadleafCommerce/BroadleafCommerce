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

package org.broadleafcommerce.core.web.order.model;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import java.util.ArrayList;
import java.util.List;

public class AddToCartItems {

    @SuppressWarnings("unchecked")

    //TOOD: this should probably be refactored to be called "rows" like in other model objects
    private List<AddToCartItem> addToCartItems =   LazyList.decorate(
            new ArrayList<AddToCartItem>(),
            FactoryUtils.instantiateFactory(AddToCartItem.class));

    private long productId;
    private long categoryId;

    public void setProductId(long productId) {
        this.productId = productId;
        for(AddToCartItem addToCartItem : addToCartItems) {
            addToCartItem.setProductId(productId);
        }
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
        for(AddToCartItem addToCartItem : addToCartItems) {
            addToCartItem.setCategoryId(categoryId);
        }
    }

    public List<AddToCartItem> getAddToCartItems() {
        return addToCartItems;
    }

    public void setAddToCartItem(List<AddToCartItem> addToCartItems) {
        this.addToCartItems = addToCartItems;
    }

    public long getProductId() {
        return productId;
    }
    public long getCategoryId() {
        return categoryId;
    }

}
