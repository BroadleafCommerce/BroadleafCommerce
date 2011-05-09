package com.other.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.catalog.domain.ProductImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "OTHER_PRODUCT")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OtherProductImpl extends ProductImpl implements OtherProduct {

	private static final long serialVersionUID = 1L;

	@Column(name = "COMPANY_NUMBER")
	protected Long companyNumber;
	
	@Column(name = "RELEASE_DATE")
	protected Date releaseDate;
	
	public Long getCompanyNumber() {
		return companyNumber;
	}
	
	public void setCompanyNumber(Long companyNumber) {
		this.companyNumber = companyNumber;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	
}
