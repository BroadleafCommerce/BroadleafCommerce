package com.springcommerce.demo.user.domain;

import com.springcommerce.demo.framework.domain.Catalog;

public class ProprietaryCatalog extends Catalog {
	
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
