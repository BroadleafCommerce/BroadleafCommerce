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
package org.broadleafcommerce.catalog.web;

import org.broadleafcommerce.catalog.domain.Product;

public class DisplayProduct {

    private Product product;
    private String promoMessage;

    public DisplayProduct () {
        this.promoMessage = "";
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public String getPromoMessage() {
        return promoMessage;
    }
    public void setPromoMessage(String promoMessage) {
        this.promoMessage = promoMessage;
    }

}
