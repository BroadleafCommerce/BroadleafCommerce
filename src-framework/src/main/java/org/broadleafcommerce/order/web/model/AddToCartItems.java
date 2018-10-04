package org.broadleafcommerce.order.web.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

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
