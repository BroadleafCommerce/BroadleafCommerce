/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */

package org.broadleafcommerce.common.payment.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * Many Hosted solutions, (e.g. PayPal Express/Sagepay Form) allow you to pass in the contents of your
 * cart to be displayed on their hosted pages.
 *
 * The following DTO represent the usual parameters that you may wish to pass:
 *
 * name: a Name for this Line Item
 * description: a Description for this Line Item
 * category: a Category for this Line Item (PayPal Express uses this to differentiate between Digital vs Physical)
 * quantity: the Quantity for this Line Item
 * amount: the unit cost of the item without tax
 * tax: the tax applied to this unit item
 * itemTotal: the cost of the unit item with tax
 * total: the total cost of this line item (Quanity x Cost Including Tax)
 *
 */
public class LineItemDTO {

    protected PaymentRequestDTO parent;

    protected Map<String, Object> additionalFields;
    protected String name;
    protected String description;
    protected String shortDescription;
    protected String systemId;
    protected String category;
    protected String quantity;
    protected String amount;
    protected String tax;
    protected String itemTotal;
    protected String total;

    public PaymentRequestDTO done(){
        parent.lineItems.add(this);
        return parent;
    }

    public LineItemDTO(PaymentRequestDTO parent) {
        this.additionalFields = new HashMap<String, Object>();
        this.parent = parent;
    }

    public LineItemDTO additionalField(String key, Object value) {
        additionalFields.put(key, value);
        return this;
    }

    public LineItemDTO name(String name) {
        this.name = name;
        return this;
    }

    public LineItemDTO description(String description) {
        this.description = description;
        return this;
    }

    public LineItemDTO shortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public LineItemDTO systemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    public LineItemDTO category(String category) {
        this.category = category;
        return this;
    }

    public LineItemDTO quantity(String quantity) {
        this.quantity = quantity;
        return this;
    }

    public LineItemDTO amount(String amount) {
        this.amount = amount;
        return this;
    }

    public LineItemDTO tax(String tax) {
        this.tax = tax;
        return this;
    }

    public LineItemDTO itemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
        return this;
    }

    public LineItemDTO total(String total) {
        this.total = total;
        return this;
    }

    public Map<String, Object> getAdditionalFields() {
        return additionalFields;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getSystemId() {
        return systemId;
    }

    public String getCategory() {
        return category;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getAmount() {
        return amount;
    }

    public String getTax() {
        return tax;
    }

    public String getItemTotal() {
        return itemTotal;
    }

    public String getTotal() {
        return total;
    }
}
