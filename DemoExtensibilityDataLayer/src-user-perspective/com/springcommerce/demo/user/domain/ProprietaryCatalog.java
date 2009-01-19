package com.springcommerce.demo.user.domain;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.springcommerce.demo.framework.domain.AbstractCatalog;

@Entity
@Table(name = "ProprietaryCatalog")
@AttributeOverrides({
	@AttributeOverride(name="itemNumber", column=@Column(name="sku")),
	@AttributeOverride(name="color", column=@Column(name="item_hue")),
	@AttributeOverride(name="style", column=@Column(name="item_style"))
})
public class ProprietaryCatalog extends AbstractCatalog {
	
	@Column(name = "item_popularity")
	private String popularity;

	/**
	 * @return the popularity
	 */
	public String getPopularity() {
		return popularity;
	}

	/**
	 * @param popularity the popularity to set
	 */
	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}

}
