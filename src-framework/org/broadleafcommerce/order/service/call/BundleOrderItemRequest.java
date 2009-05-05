package org.broadleafcommerce.order.service.call;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;

public class BundleOrderItemRequest {

    private String name;
    private Category category;
    private int quantity;
    private List<DiscreteOrderItemRequest> discreteOrderItems = new ArrayList<DiscreteOrderItemRequest>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<DiscreteOrderItemRequest> getDiscreteOrderItems() {
        return discreteOrderItems;
    }

    public void setDiscreteOrderItems(List<DiscreteOrderItemRequest> discreteOrderItems) {
        this.discreteOrderItems = discreteOrderItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BundleOrderItemRequest)) return false;

        BundleOrderItemRequest that = (BundleOrderItemRequest) o;

        if (!category.equals(that.category)) return false;
        if (!name.equals(that.name)) return false;
        if (quantity != that.quantity) return false;
        if (discreteOrderItems != that.discreteOrderItems) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (discreteOrderItems != null ? discreteOrderItems.hashCode() : 0);
        result = 31 * result + quantity;
        return result;
    }
}
