/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.catalog.service.dynamic;

import java.io.Serializable;

import org.broadleafcommerce.common.money.Money;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicSkuPrices implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Money retailPrice;
	protected Money salePrice;
	protected Money priceAdjustment;
	protected Money qualifyingOrderSubtotal;
	public Money getRetailPrice() {
		return retailPrice;
	}
	
	public void setRetailPrice(Money retailPrice) {
		this.retailPrice = retailPrice;
	}
	
	public Money getSalePrice() {
		return salePrice;
	}
	
	public void setSalePrice(Money salePrice) {
		this.salePrice = salePrice;
	}

        public Money getPriceAdjustment() {
                return priceAdjustment;
        }

        public void setPriceAdjustment(Money priceAdjustment) {
               this.priceAdjustment = priceAdjustment;
        }

        public Money getQualifyingOrderSubtotal() {
            return qualifyingOrderSubtotal;
        }

        public void setQualifyingOrderSubtotal(Money qualifyingOrderSubtotal) {
            this.qualifyingOrderSubtotal = qualifyingOrderSubtotal;
        }
}
