package com.other.domain;

import java.util.Date;

import org.broadleafcommerce.core.catalog.domain.ProductSku;

public interface OtherProduct extends ProductSku {
	
	Long getCompanyNumber();
	void setCompanyNumber(Long companyNumber);
	Date getReleaseDate();
	void setReleaseDate(Date releaseDate);

}
