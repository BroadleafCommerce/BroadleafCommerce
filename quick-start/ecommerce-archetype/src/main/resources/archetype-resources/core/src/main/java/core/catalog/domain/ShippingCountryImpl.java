#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.catalog.domain;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.compass.annotations.Searchable;
import org.compass.annotations.SearchableId;
import org.compass.annotations.SupportUnmarshall;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "SHIPPING_COUNTRY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@Searchable(alias="shippingcountry", supportUnmarshall=SupportUnmarshall.FALSE)
public class ShippingCountryImpl implements ShippingCountry {
	
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(generator = "ShippingCountryId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ShippingCountryId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ShippingCountryImpl", allocationSize = 50)
    @Column(name = "SHIPPING_COUNTRY_ID")
    @SearchableId
    @AdminPresentation(friendlyName="Shipping Country ID", group="Primary Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "CURRENCY_CODE", nullable=false)
    @Index(name="CURRENCYCODE_INDEX", columnNames={"CURRENCY_CODE"})
    @AdminPresentation(friendlyName ="Currency Code", group ="Description", order =4)
    protected String currencyCode;
    
    @Column(name = "COUNTRY_ISO")
    @AdminPresentation(friendlyName ="Country Iso", group ="Description", order =8)
    protected String countryIso;
    
    @ManyToMany(targetEntity = MyCompanyProductImpl.class)
    @JoinTable(name = "PRODUCT_SHIPPING_COUNTRY_XREF", joinColumns = @JoinColumn(name = "SHIPPING_COUNTRY_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", nullable=true))
    @Cascade(value={org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<MyCompanyProduct> products = new ArrayList<MyCompanyProduct>();
  
    public Long getId(){
    	return id;
    }
	
	public void setId(Long id){
		this.id = id;
	}
	
	public String getCurrencyCode(){
		return currencyCode;
	}
	
	public void setCurrencyCode(String currencyCode){
		this.currencyCode = currencyCode;
	}
	
	public String getCountryISO(){
		return countryIso;
	}
	
	public void setCountryISO(String countryIso){
		this.countryIso = countryIso;
	}

	public List<MyCompanyProduct> getProducts() {
		return products;
	}

	public void setProducts(List<MyCompanyProduct> products) {
		this.products = products;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((countryIso == null) ? 0 : countryIso.hashCode());
		result = prime * result
				+ ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShippingCountryImpl other = (ShippingCountryImpl) obj;
		if (countryIso == null) {
			if (other.countryIso != null)
				return false;
		} else if (!countryIso.equals(other.countryIso))
			return false;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
    
}
