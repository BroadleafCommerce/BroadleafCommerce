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

package org.broadleafcommerce.core.web.order.model;

public class WishlistRequest {
    private Long addProductId;
    private Long addCategoryId;
    private Long addSkuId;
    private Integer quantity;
    private String wishlistName;

    public Long getAddProductId() {
        return addProductId;
    }
    public void setAddProductId(Long addProductId) {
        this.addProductId = addProductId;
    }
    public Long getAddCategoryId() {
        return addCategoryId;
    }
    public void setAddCategoryId(Long addCategoryId) {
        this.addCategoryId = addCategoryId;
    }
    public Long getAddSkuId() {
        return addSkuId;
    }
    public void setAddSkuId(Long addSkuId) {
        this.addSkuId = addSkuId;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public String getWishlistName() {
        return wishlistName;
    }
    public void setWishlistName(String wishlistName) {
        this.wishlistName = wishlistName;
    }
}