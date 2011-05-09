package com.other.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.broadleafcommerce.core.catalog.domain.ProductSkuImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "OTHER_PRODUCT")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OtherProductImpl extends ProductSkuImpl implements OtherProduct {

	private static final long serialVersionUID = 1L;

	@Column(name = "COMPANY_NUMBER")
	@AdminPresentation(friendlyName="Company Number", order=3, group="My Special Descriptions", prominent=false)
	protected Long companyNumber;
	
	@Column(name = "RELEASE_DATE")
	@AdminPresentation(friendlyName="Release Date", order=4, group="My Special Descriptions", prominent=false)
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
