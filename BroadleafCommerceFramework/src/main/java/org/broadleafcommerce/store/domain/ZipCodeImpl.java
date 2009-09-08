package org.broadleafcommerce.store.domain;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Index;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ZIP_CODE")
public class ZipCodeImpl implements Serializable, ZipCode {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ZIP_CODE_ID", nullable = false)
    private String id;

    @Column(name = "ZIPCODE", insertable = false, updatable = false)
    @Index(name="ZIPCODE_ZIP_INDEX", columnNames={"ZIPCODE"})
    private Integer zipcode;

    @Column(name = "ZIP_STATE", insertable = false, updatable = false)
    @Index(name="ZIPCODE_STATE_INDEX", columnNames={"ZIP_STATE"})
    private String zipState;

    @Column(name = "ZIP_CITY")
    @Index(name="ZIPCODE_CITY_INDEX", columnNames={"ZIP_CITY"})
    private String zipCity;

    @Column(name = "ZIP_LONGITUDE")
    @Index(name="ZIPCODE_LONGITUDE_INDEX", columnNames={"ZIP_LONGITUDE"})
    private double zipLongitude;

    @Column(name = "ZIP_LATITUDE")
    @Index(name="ZIPCODE_LATITUDE_INDEX", columnNames={"ZIP_LATITUDE"})
    private double zipLatitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getZipcode() {
        return zipcode;
    }

    public void setZipcode(Integer zipcode) {
        this.zipcode = zipcode;
    }

    public String getZipState() {
        return zipState;
    }

    public void setZipState(String zipState) {
        this.zipState = zipState;
    }

    public String getZipCity() {
        return zipCity;
    }

    public void setZipCity(String zipCity) {
        this.zipCity = zipCity;
    }

    public double getZipLongitude() {
        return zipLongitude;
    }

    public void setZipLongitude(double zipLongitude) {
        this.zipLongitude = zipLongitude;
    }

    public double getZipLatitude() {
        return zipLatitude;
    }

    public void setZipLatitude(double zipLatitude) {
        this.zipLatitude = zipLatitude;
    }

}