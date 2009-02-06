package org.broadleafcommerce.layout.tags;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

import org.apache.log4j.Logger;
import org.broadleafcommerce.catalog.domain.CatalogItem;
import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.SellableItem;
import org.broadleafcommerce.order.domain.OrderItem;

public class ItemLayoutTag extends BodyTagSupport {
    
    private Logger log = Logger.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private SellableItem sellableItem;
    private CatalogItem catalogItem;
    private OrderItem orderItem;
    private Category category;
    private String layout;
    private String itemName = "item";
    private ItemType itemType;
    public enum ItemType {
        SELLABLE_ITEM,
        CATALOG_ITEM,
        CATEGORY,
        ORDER_ITEM
    }
    @Override
    public int doStartTag() throws JspException {
        try {
            switch(itemType) {
            case SELLABLE_ITEM:
	            pageContext.setAttribute(itemName, sellableItem, PageContext.REQUEST_SCOPE);
	            break;
    		case CATALOG_ITEM:
	            pageContext.setAttribute(itemName, catalogItem, PageContext.REQUEST_SCOPE);
	            break;
    		case CATEGORY:
	            pageContext.setAttribute(itemName, category, PageContext.REQUEST_SCOPE);
	            break;
    		case ORDER_ITEM:
	            pageContext.setAttribute(itemName, orderItem, PageContext.REQUEST_SCOPE);
	            break;
            }
	    	pageContext.include("/WEB-INF/jsp/itemLayout/" + layout + ".jsp");
        } catch (IOException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
        } catch (ServletException e) {
            log.error(e);
            return Tag.SKIP_PAGE;
        }
        return super.doStartTag();
    }
    public SellableItem getSellableItem() {
        return sellableItem;
    }
    public void setSellableItem(SellableItem sellableItem) {
        this.setItemType(ItemType.SELLABLE_ITEM);
        this.sellableItem = sellableItem;
    }
    public CatalogItem getCatalogItem() {
        return catalogItem;
    }
    public void setCatalogItem(CatalogItem catalogItem) {
        setItemType(ItemType.CATALOG_ITEM);
        this.catalogItem = catalogItem;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        setItemType(ItemType.CATEGORY);
        this.category = category;
    }
    public ItemType getItemType() {
        return itemType;
    }
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    public String getLayout() {
        return layout;
    }
    public void setLayout(String layout) {
        this.layout = layout;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public OrderItem getOrderItem() {
        return orderItem;
    }
    public void setOrderItem(OrderItem orderItem) {
        this.setItemType(ItemType.ORDER_ITEM);
        this.orderItem = orderItem;
    }
}
