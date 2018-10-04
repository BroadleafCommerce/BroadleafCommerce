package org.broadleafcommerce.store.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_STORE")
@Inheritance(strategy = InheritanceType.JOINED)
public class StoreImpl implements Store {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "STORE_ID", nullable = false)
    private String id;

    @Column(name = "STORE_NAME")
    private String name;

    @Column(name = "ADDRESS_1")
    private String address1;

    @Column(name = "ADDRESS_2")
    private String address2;

    @Column(name = "STORE_CITY")
    private String city;

    @Column(name = "STORE_STATE")
    private String state;

    @Column(name = "STORE_ZIP")
    private String zip;

    @Column(name = "STORE_COUNTRY")
    private String country;

    @Column(name = "STORE_PHONE")
    private String phone;

    @Column(name = "LATITUDE")
    private Float latitude;

    @Column(name = "LONGITUDE")
    private Float longitude;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setId(java.lang.String)
     */
    public void setId(String id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getAddress1()
     */
    public String getAddress1() {
        return address1;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setAddress1(java.lang.String)
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getAddress2()
     */
    public String getAddress2() {
        return address2;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setAddress2(java.lang.String)
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getCity()
     */
    public String getCity() {
        return city;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setCity(java.lang.String)
     */
    public void setCity(String city) {
        this.city = city;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getZip()
     */
    public String getZip() {
        return zip;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setZip(java.lang.String)
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getCountry()
     */
    public String getCountry() {
        return country;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setCountry(java.lang.String)
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getPhone()
     */
    public String getPhone() {
        return phone;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setPhone(java.lang.String)
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getLongitude()
     */
    public Float getLongitude() {
        return longitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setLongitude(java.lang.Float)
     */
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getLatitude()
     */
    public Float getLatitude() {
        return latitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setLatitude(java.lang.Float)
     */
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#setState(java.lang.String)
     */
    public void setState(String state) {
        this.state = state;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.store.domain.Store#getState()
     */
    public String getState() {
        return state;
    }

}
