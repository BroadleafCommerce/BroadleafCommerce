package org.broadleafcommerce.order.web.model;

public class AddToCartItem {

    private long productId;
    private long categoryId;
    private long skuId;
    private int quantity;

    public long getSkuId() {
        return skuId;
    }
    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public long getProductId() {
        return productId;
    }
    public void setProductId(long productId) {
        this.productId = productId;
    }
    public long getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
