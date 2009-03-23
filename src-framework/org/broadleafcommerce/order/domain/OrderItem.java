package org.broadleafcommerce.order.domain;

import java.util.List;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.domain.Sku;
import org.broadleafcommerce.offer.domain.ItemOffer;
import org.broadleafcommerce.util.money.Money;

public interface OrderItem {

    public Long getId();

    public void setId(Long id);

    public Sku getSku();

    public void setSku(Sku sku);

    public Order getOrder();

    public void setOrder(Order order);

    public Money getRetailPrice();

    public void setRetailPrice(Money retailPrice);

    public Money getSalePrice();

    public void setSalePrice(Money salePrice);

    public Money getPrice();

    public void setPrice(Money price);

    public int getQuantity();

    public void setQuantity(int quantity);

    public Product getProduct();

	public void setProduct(Product product);

	public Category getCategory();

	public void setCategory(Category category);

    public List<ItemOffer> getCandidateItemOffers();

    public void setCandidateItemOffers(List<ItemOffer> candidateOffers);

    public List<ItemOffer> addCandidateItemOffer(ItemOffer candidateOffer);

    public void removeAllOffers();

}
