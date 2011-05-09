package com.other.domain;

import java.util.Date;

import org.broadleafcommerce.catalog.domain.Product;

public interface OtherProduct extends Product {
	
	Long getCompanyNumber();
	void setCompanyNumber(Long companyNumber);
	Date getReleaseDate();
	void setReleaseDate(Date releaseDate);

}
