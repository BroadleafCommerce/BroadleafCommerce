package org.springcommerce.util;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.catalog.domain.SellableItem;

public class CreateSellableItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	@Transient
	private final Log logger = LogFactory.getLog(getClass());
	
	private SellableItem sellableItem;
	
	private String price;
	
	public SellableItem getSellableItem() {
		return sellableItem;
	}
	public void setSellableItem(SellableItem sellableItem) {
		this.sellableItem = sellableItem;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Log getLogger() {
		return logger;
	}
	
	
	

}
