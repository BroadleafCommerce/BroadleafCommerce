/**
 * DAVReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class DAVReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String addressType;

    private java.lang.String apartmentInfo;

    private java.lang.String barCode;

    private java.lang.String barCodeCheckDigit;

    private java.lang.String careOf;

    private java.lang.String cityInfo;

    private java.lang.String countryInfo;

    private java.lang.String directionalInfo;

    private java.lang.String lvrInfo;

    private java.math.BigInteger matchScore;

    private java.lang.String standardizedAddress1;

    private java.lang.String standardizedAddress2;

    private java.lang.String standardizedAddress3;

    private java.lang.String standardizedAddress4;

    private java.lang.String standardizedAddressNoApt;

    private java.lang.String standardizedCity;

    private java.lang.String standardizedCounty;

    private java.lang.String standardizedCSP;

    private java.lang.String standardizedState;

    private java.lang.String standardizedPostalCode;

    private java.lang.String standardizedCountry;

    private java.lang.String standardizedISOCountry;

    private java.lang.String stateInfo;

    private java.lang.String streetInfo;

    private java.lang.String suffixInfo;

    private java.lang.String postalCodeInfo;

    private java.lang.String overallInfo;

    private java.lang.String usInfo;

    private java.lang.String caInfo;

    private java.lang.String intlInfo;

    private java.lang.String usErrorInfo;

    private java.lang.String caErrorInfo;

    private java.lang.String intlErrorInfo;

    public DAVReply() {
    }

    public DAVReply(
           java.math.BigInteger reasonCode,
           java.lang.String addressType,
           java.lang.String apartmentInfo,
           java.lang.String barCode,
           java.lang.String barCodeCheckDigit,
           java.lang.String careOf,
           java.lang.String cityInfo,
           java.lang.String countryInfo,
           java.lang.String directionalInfo,
           java.lang.String lvrInfo,
           java.math.BigInteger matchScore,
           java.lang.String standardizedAddress1,
           java.lang.String standardizedAddress2,
           java.lang.String standardizedAddress3,
           java.lang.String standardizedAddress4,
           java.lang.String standardizedAddressNoApt,
           java.lang.String standardizedCity,
           java.lang.String standardizedCounty,
           java.lang.String standardizedCSP,
           java.lang.String standardizedState,
           java.lang.String standardizedPostalCode,
           java.lang.String standardizedCountry,
           java.lang.String standardizedISOCountry,
           java.lang.String stateInfo,
           java.lang.String streetInfo,
           java.lang.String suffixInfo,
           java.lang.String postalCodeInfo,
           java.lang.String overallInfo,
           java.lang.String usInfo,
           java.lang.String caInfo,
           java.lang.String intlInfo,
           java.lang.String usErrorInfo,
           java.lang.String caErrorInfo,
           java.lang.String intlErrorInfo) {
           this.reasonCode = reasonCode;
           this.addressType = addressType;
           this.apartmentInfo = apartmentInfo;
           this.barCode = barCode;
           this.barCodeCheckDigit = barCodeCheckDigit;
           this.careOf = careOf;
           this.cityInfo = cityInfo;
           this.countryInfo = countryInfo;
           this.directionalInfo = directionalInfo;
           this.lvrInfo = lvrInfo;
           this.matchScore = matchScore;
           this.standardizedAddress1 = standardizedAddress1;
           this.standardizedAddress2 = standardizedAddress2;
           this.standardizedAddress3 = standardizedAddress3;
           this.standardizedAddress4 = standardizedAddress4;
           this.standardizedAddressNoApt = standardizedAddressNoApt;
           this.standardizedCity = standardizedCity;
           this.standardizedCounty = standardizedCounty;
           this.standardizedCSP = standardizedCSP;
           this.standardizedState = standardizedState;
           this.standardizedPostalCode = standardizedPostalCode;
           this.standardizedCountry = standardizedCountry;
           this.standardizedISOCountry = standardizedISOCountry;
           this.stateInfo = stateInfo;
           this.streetInfo = streetInfo;
           this.suffixInfo = suffixInfo;
           this.postalCodeInfo = postalCodeInfo;
           this.overallInfo = overallInfo;
           this.usInfo = usInfo;
           this.caInfo = caInfo;
           this.intlInfo = intlInfo;
           this.usErrorInfo = usErrorInfo;
           this.caErrorInfo = caErrorInfo;
           this.intlErrorInfo = intlErrorInfo;
    }


    /**
     * Gets the reasonCode value for this DAVReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this DAVReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the addressType value for this DAVReply.
     * 
     * @return addressType
     */
    public java.lang.String getAddressType() {
        return addressType;
    }


    /**
     * Sets the addressType value for this DAVReply.
     * 
     * @param addressType
     */
    public void setAddressType(java.lang.String addressType) {
        this.addressType = addressType;
    }


    /**
     * Gets the apartmentInfo value for this DAVReply.
     * 
     * @return apartmentInfo
     */
    public java.lang.String getApartmentInfo() {
        return apartmentInfo;
    }


    /**
     * Sets the apartmentInfo value for this DAVReply.
     * 
     * @param apartmentInfo
     */
    public void setApartmentInfo(java.lang.String apartmentInfo) {
        this.apartmentInfo = apartmentInfo;
    }


    /**
     * Gets the barCode value for this DAVReply.
     * 
     * @return barCode
     */
    public java.lang.String getBarCode() {
        return barCode;
    }


    /**
     * Sets the barCode value for this DAVReply.
     * 
     * @param barCode
     */
    public void setBarCode(java.lang.String barCode) {
        this.barCode = barCode;
    }


    /**
     * Gets the barCodeCheckDigit value for this DAVReply.
     * 
     * @return barCodeCheckDigit
     */
    public java.lang.String getBarCodeCheckDigit() {
        return barCodeCheckDigit;
    }


    /**
     * Sets the barCodeCheckDigit value for this DAVReply.
     * 
     * @param barCodeCheckDigit
     */
    public void setBarCodeCheckDigit(java.lang.String barCodeCheckDigit) {
        this.barCodeCheckDigit = barCodeCheckDigit;
    }


    /**
     * Gets the careOf value for this DAVReply.
     * 
     * @return careOf
     */
    public java.lang.String getCareOf() {
        return careOf;
    }


    /**
     * Sets the careOf value for this DAVReply.
     * 
     * @param careOf
     */
    public void setCareOf(java.lang.String careOf) {
        this.careOf = careOf;
    }


    /**
     * Gets the cityInfo value for this DAVReply.
     * 
     * @return cityInfo
     */
    public java.lang.String getCityInfo() {
        return cityInfo;
    }


    /**
     * Sets the cityInfo value for this DAVReply.
     * 
     * @param cityInfo
     */
    public void setCityInfo(java.lang.String cityInfo) {
        this.cityInfo = cityInfo;
    }


    /**
     * Gets the countryInfo value for this DAVReply.
     * 
     * @return countryInfo
     */
    public java.lang.String getCountryInfo() {
        return countryInfo;
    }


    /**
     * Sets the countryInfo value for this DAVReply.
     * 
     * @param countryInfo
     */
    public void setCountryInfo(java.lang.String countryInfo) {
        this.countryInfo = countryInfo;
    }


    /**
     * Gets the directionalInfo value for this DAVReply.
     * 
     * @return directionalInfo
     */
    public java.lang.String getDirectionalInfo() {
        return directionalInfo;
    }


    /**
     * Sets the directionalInfo value for this DAVReply.
     * 
     * @param directionalInfo
     */
    public void setDirectionalInfo(java.lang.String directionalInfo) {
        this.directionalInfo = directionalInfo;
    }


    /**
     * Gets the lvrInfo value for this DAVReply.
     * 
     * @return lvrInfo
     */
    public java.lang.String getLvrInfo() {
        return lvrInfo;
    }


    /**
     * Sets the lvrInfo value for this DAVReply.
     * 
     * @param lvrInfo
     */
    public void setLvrInfo(java.lang.String lvrInfo) {
        this.lvrInfo = lvrInfo;
    }


    /**
     * Gets the matchScore value for this DAVReply.
     * 
     * @return matchScore
     */
    public java.math.BigInteger getMatchScore() {
        return matchScore;
    }


    /**
     * Sets the matchScore value for this DAVReply.
     * 
     * @param matchScore
     */
    public void setMatchScore(java.math.BigInteger matchScore) {
        this.matchScore = matchScore;
    }


    /**
     * Gets the standardizedAddress1 value for this DAVReply.
     * 
     * @return standardizedAddress1
     */
    public java.lang.String getStandardizedAddress1() {
        return standardizedAddress1;
    }


    /**
     * Sets the standardizedAddress1 value for this DAVReply.
     * 
     * @param standardizedAddress1
     */
    public void setStandardizedAddress1(java.lang.String standardizedAddress1) {
        this.standardizedAddress1 = standardizedAddress1;
    }


    /**
     * Gets the standardizedAddress2 value for this DAVReply.
     * 
     * @return standardizedAddress2
     */
    public java.lang.String getStandardizedAddress2() {
        return standardizedAddress2;
    }


    /**
     * Sets the standardizedAddress2 value for this DAVReply.
     * 
     * @param standardizedAddress2
     */
    public void setStandardizedAddress2(java.lang.String standardizedAddress2) {
        this.standardizedAddress2 = standardizedAddress2;
    }


    /**
     * Gets the standardizedAddress3 value for this DAVReply.
     * 
     * @return standardizedAddress3
     */
    public java.lang.String getStandardizedAddress3() {
        return standardizedAddress3;
    }


    /**
     * Sets the standardizedAddress3 value for this DAVReply.
     * 
     * @param standardizedAddress3
     */
    public void setStandardizedAddress3(java.lang.String standardizedAddress3) {
        this.standardizedAddress3 = standardizedAddress3;
    }


    /**
     * Gets the standardizedAddress4 value for this DAVReply.
     * 
     * @return standardizedAddress4
     */
    public java.lang.String getStandardizedAddress4() {
        return standardizedAddress4;
    }


    /**
     * Sets the standardizedAddress4 value for this DAVReply.
     * 
     * @param standardizedAddress4
     */
    public void setStandardizedAddress4(java.lang.String standardizedAddress4) {
        this.standardizedAddress4 = standardizedAddress4;
    }


    /**
     * Gets the standardizedAddressNoApt value for this DAVReply.
     * 
     * @return standardizedAddressNoApt
     */
    public java.lang.String getStandardizedAddressNoApt() {
        return standardizedAddressNoApt;
    }


    /**
     * Sets the standardizedAddressNoApt value for this DAVReply.
     * 
     * @param standardizedAddressNoApt
     */
    public void setStandardizedAddressNoApt(java.lang.String standardizedAddressNoApt) {
        this.standardizedAddressNoApt = standardizedAddressNoApt;
    }


    /**
     * Gets the standardizedCity value for this DAVReply.
     * 
     * @return standardizedCity
     */
    public java.lang.String getStandardizedCity() {
        return standardizedCity;
    }


    /**
     * Sets the standardizedCity value for this DAVReply.
     * 
     * @param standardizedCity
     */
    public void setStandardizedCity(java.lang.String standardizedCity) {
        this.standardizedCity = standardizedCity;
    }


    /**
     * Gets the standardizedCounty value for this DAVReply.
     * 
     * @return standardizedCounty
     */
    public java.lang.String getStandardizedCounty() {
        return standardizedCounty;
    }


    /**
     * Sets the standardizedCounty value for this DAVReply.
     * 
     * @param standardizedCounty
     */
    public void setStandardizedCounty(java.lang.String standardizedCounty) {
        this.standardizedCounty = standardizedCounty;
    }


    /**
     * Gets the standardizedCSP value for this DAVReply.
     * 
     * @return standardizedCSP
     */
    public java.lang.String getStandardizedCSP() {
        return standardizedCSP;
    }


    /**
     * Sets the standardizedCSP value for this DAVReply.
     * 
     * @param standardizedCSP
     */
    public void setStandardizedCSP(java.lang.String standardizedCSP) {
        this.standardizedCSP = standardizedCSP;
    }


    /**
     * Gets the standardizedState value for this DAVReply.
     * 
     * @return standardizedState
     */
    public java.lang.String getStandardizedState() {
        return standardizedState;
    }


    /**
     * Sets the standardizedState value for this DAVReply.
     * 
     * @param standardizedState
     */
    public void setStandardizedState(java.lang.String standardizedState) {
        this.standardizedState = standardizedState;
    }


    /**
     * Gets the standardizedPostalCode value for this DAVReply.
     * 
     * @return standardizedPostalCode
     */
    public java.lang.String getStandardizedPostalCode() {
        return standardizedPostalCode;
    }


    /**
     * Sets the standardizedPostalCode value for this DAVReply.
     * 
     * @param standardizedPostalCode
     */
    public void setStandardizedPostalCode(java.lang.String standardizedPostalCode) {
        this.standardizedPostalCode = standardizedPostalCode;
    }


    /**
     * Gets the standardizedCountry value for this DAVReply.
     * 
     * @return standardizedCountry
     */
    public java.lang.String getStandardizedCountry() {
        return standardizedCountry;
    }


    /**
     * Sets the standardizedCountry value for this DAVReply.
     * 
     * @param standardizedCountry
     */
    public void setStandardizedCountry(java.lang.String standardizedCountry) {
        this.standardizedCountry = standardizedCountry;
    }


    /**
     * Gets the standardizedISOCountry value for this DAVReply.
     * 
     * @return standardizedISOCountry
     */
    public java.lang.String getStandardizedISOCountry() {
        return standardizedISOCountry;
    }


    /**
     * Sets the standardizedISOCountry value for this DAVReply.
     * 
     * @param standardizedISOCountry
     */
    public void setStandardizedISOCountry(java.lang.String standardizedISOCountry) {
        this.standardizedISOCountry = standardizedISOCountry;
    }


    /**
     * Gets the stateInfo value for this DAVReply.
     * 
     * @return stateInfo
     */
    public java.lang.String getStateInfo() {
        return stateInfo;
    }


    /**
     * Sets the stateInfo value for this DAVReply.
     * 
     * @param stateInfo
     */
    public void setStateInfo(java.lang.String stateInfo) {
        this.stateInfo = stateInfo;
    }


    /**
     * Gets the streetInfo value for this DAVReply.
     * 
     * @return streetInfo
     */
    public java.lang.String getStreetInfo() {
        return streetInfo;
    }


    /**
     * Sets the streetInfo value for this DAVReply.
     * 
     * @param streetInfo
     */
    public void setStreetInfo(java.lang.String streetInfo) {
        this.streetInfo = streetInfo;
    }


    /**
     * Gets the suffixInfo value for this DAVReply.
     * 
     * @return suffixInfo
     */
    public java.lang.String getSuffixInfo() {
        return suffixInfo;
    }


    /**
     * Sets the suffixInfo value for this DAVReply.
     * 
     * @param suffixInfo
     */
    public void setSuffixInfo(java.lang.String suffixInfo) {
        this.suffixInfo = suffixInfo;
    }


    /**
     * Gets the postalCodeInfo value for this DAVReply.
     * 
     * @return postalCodeInfo
     */
    public java.lang.String getPostalCodeInfo() {
        return postalCodeInfo;
    }


    /**
     * Sets the postalCodeInfo value for this DAVReply.
     * 
     * @param postalCodeInfo
     */
    public void setPostalCodeInfo(java.lang.String postalCodeInfo) {
        this.postalCodeInfo = postalCodeInfo;
    }


    /**
     * Gets the overallInfo value for this DAVReply.
     * 
     * @return overallInfo
     */
    public java.lang.String getOverallInfo() {
        return overallInfo;
    }


    /**
     * Sets the overallInfo value for this DAVReply.
     * 
     * @param overallInfo
     */
    public void setOverallInfo(java.lang.String overallInfo) {
        this.overallInfo = overallInfo;
    }


    /**
     * Gets the usInfo value for this DAVReply.
     * 
     * @return usInfo
     */
    public java.lang.String getUsInfo() {
        return usInfo;
    }


    /**
     * Sets the usInfo value for this DAVReply.
     * 
     * @param usInfo
     */
    public void setUsInfo(java.lang.String usInfo) {
        this.usInfo = usInfo;
    }


    /**
     * Gets the caInfo value for this DAVReply.
     * 
     * @return caInfo
     */
    public java.lang.String getCaInfo() {
        return caInfo;
    }


    /**
     * Sets the caInfo value for this DAVReply.
     * 
     * @param caInfo
     */
    public void setCaInfo(java.lang.String caInfo) {
        this.caInfo = caInfo;
    }


    /**
     * Gets the intlInfo value for this DAVReply.
     * 
     * @return intlInfo
     */
    public java.lang.String getIntlInfo() {
        return intlInfo;
    }


    /**
     * Sets the intlInfo value for this DAVReply.
     * 
     * @param intlInfo
     */
    public void setIntlInfo(java.lang.String intlInfo) {
        this.intlInfo = intlInfo;
    }


    /**
     * Gets the usErrorInfo value for this DAVReply.
     * 
     * @return usErrorInfo
     */
    public java.lang.String getUsErrorInfo() {
        return usErrorInfo;
    }


    /**
     * Sets the usErrorInfo value for this DAVReply.
     * 
     * @param usErrorInfo
     */
    public void setUsErrorInfo(java.lang.String usErrorInfo) {
        this.usErrorInfo = usErrorInfo;
    }


    /**
     * Gets the caErrorInfo value for this DAVReply.
     * 
     * @return caErrorInfo
     */
    public java.lang.String getCaErrorInfo() {
        return caErrorInfo;
    }


    /**
     * Sets the caErrorInfo value for this DAVReply.
     * 
     * @param caErrorInfo
     */
    public void setCaErrorInfo(java.lang.String caErrorInfo) {
        this.caErrorInfo = caErrorInfo;
    }


    /**
     * Gets the intlErrorInfo value for this DAVReply.
     * 
     * @return intlErrorInfo
     */
    public java.lang.String getIntlErrorInfo() {
        return intlErrorInfo;
    }


    /**
     * Sets the intlErrorInfo value for this DAVReply.
     * 
     * @param intlErrorInfo
     */
    public void setIntlErrorInfo(java.lang.String intlErrorInfo) {
        this.intlErrorInfo = intlErrorInfo;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DAVReply)) return false;
        DAVReply other = (DAVReply) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.reasonCode==null && other.getReasonCode()==null) || 
             (this.reasonCode!=null &&
              this.reasonCode.equals(other.getReasonCode()))) &&
            ((this.addressType==null && other.getAddressType()==null) || 
             (this.addressType!=null &&
              this.addressType.equals(other.getAddressType()))) &&
            ((this.apartmentInfo==null && other.getApartmentInfo()==null) || 
             (this.apartmentInfo!=null &&
              this.apartmentInfo.equals(other.getApartmentInfo()))) &&
            ((this.barCode==null && other.getBarCode()==null) || 
             (this.barCode!=null &&
              this.barCode.equals(other.getBarCode()))) &&
            ((this.barCodeCheckDigit==null && other.getBarCodeCheckDigit()==null) || 
             (this.barCodeCheckDigit!=null &&
              this.barCodeCheckDigit.equals(other.getBarCodeCheckDigit()))) &&
            ((this.careOf==null && other.getCareOf()==null) || 
             (this.careOf!=null &&
              this.careOf.equals(other.getCareOf()))) &&
            ((this.cityInfo==null && other.getCityInfo()==null) || 
             (this.cityInfo!=null &&
              this.cityInfo.equals(other.getCityInfo()))) &&
            ((this.countryInfo==null && other.getCountryInfo()==null) || 
             (this.countryInfo!=null &&
              this.countryInfo.equals(other.getCountryInfo()))) &&
            ((this.directionalInfo==null && other.getDirectionalInfo()==null) || 
             (this.directionalInfo!=null &&
              this.directionalInfo.equals(other.getDirectionalInfo()))) &&
            ((this.lvrInfo==null && other.getLvrInfo()==null) || 
             (this.lvrInfo!=null &&
              this.lvrInfo.equals(other.getLvrInfo()))) &&
            ((this.matchScore==null && other.getMatchScore()==null) || 
             (this.matchScore!=null &&
              this.matchScore.equals(other.getMatchScore()))) &&
            ((this.standardizedAddress1==null && other.getStandardizedAddress1()==null) || 
             (this.standardizedAddress1!=null &&
              this.standardizedAddress1.equals(other.getStandardizedAddress1()))) &&
            ((this.standardizedAddress2==null && other.getStandardizedAddress2()==null) || 
             (this.standardizedAddress2!=null &&
              this.standardizedAddress2.equals(other.getStandardizedAddress2()))) &&
            ((this.standardizedAddress3==null && other.getStandardizedAddress3()==null) || 
             (this.standardizedAddress3!=null &&
              this.standardizedAddress3.equals(other.getStandardizedAddress3()))) &&
            ((this.standardizedAddress4==null && other.getStandardizedAddress4()==null) || 
             (this.standardizedAddress4!=null &&
              this.standardizedAddress4.equals(other.getStandardizedAddress4()))) &&
            ((this.standardizedAddressNoApt==null && other.getStandardizedAddressNoApt()==null) || 
             (this.standardizedAddressNoApt!=null &&
              this.standardizedAddressNoApt.equals(other.getStandardizedAddressNoApt()))) &&
            ((this.standardizedCity==null && other.getStandardizedCity()==null) || 
             (this.standardizedCity!=null &&
              this.standardizedCity.equals(other.getStandardizedCity()))) &&
            ((this.standardizedCounty==null && other.getStandardizedCounty()==null) || 
             (this.standardizedCounty!=null &&
              this.standardizedCounty.equals(other.getStandardizedCounty()))) &&
            ((this.standardizedCSP==null && other.getStandardizedCSP()==null) || 
             (this.standardizedCSP!=null &&
              this.standardizedCSP.equals(other.getStandardizedCSP()))) &&
            ((this.standardizedState==null && other.getStandardizedState()==null) || 
             (this.standardizedState!=null &&
              this.standardizedState.equals(other.getStandardizedState()))) &&
            ((this.standardizedPostalCode==null && other.getStandardizedPostalCode()==null) || 
             (this.standardizedPostalCode!=null &&
              this.standardizedPostalCode.equals(other.getStandardizedPostalCode()))) &&
            ((this.standardizedCountry==null && other.getStandardizedCountry()==null) || 
             (this.standardizedCountry!=null &&
              this.standardizedCountry.equals(other.getStandardizedCountry()))) &&
            ((this.standardizedISOCountry==null && other.getStandardizedISOCountry()==null) || 
             (this.standardizedISOCountry!=null &&
              this.standardizedISOCountry.equals(other.getStandardizedISOCountry()))) &&
            ((this.stateInfo==null && other.getStateInfo()==null) || 
             (this.stateInfo!=null &&
              this.stateInfo.equals(other.getStateInfo()))) &&
            ((this.streetInfo==null && other.getStreetInfo()==null) || 
             (this.streetInfo!=null &&
              this.streetInfo.equals(other.getStreetInfo()))) &&
            ((this.suffixInfo==null && other.getSuffixInfo()==null) || 
             (this.suffixInfo!=null &&
              this.suffixInfo.equals(other.getSuffixInfo()))) &&
            ((this.postalCodeInfo==null && other.getPostalCodeInfo()==null) || 
             (this.postalCodeInfo!=null &&
              this.postalCodeInfo.equals(other.getPostalCodeInfo()))) &&
            ((this.overallInfo==null && other.getOverallInfo()==null) || 
             (this.overallInfo!=null &&
              this.overallInfo.equals(other.getOverallInfo()))) &&
            ((this.usInfo==null && other.getUsInfo()==null) || 
             (this.usInfo!=null &&
              this.usInfo.equals(other.getUsInfo()))) &&
            ((this.caInfo==null && other.getCaInfo()==null) || 
             (this.caInfo!=null &&
              this.caInfo.equals(other.getCaInfo()))) &&
            ((this.intlInfo==null && other.getIntlInfo()==null) || 
             (this.intlInfo!=null &&
              this.intlInfo.equals(other.getIntlInfo()))) &&
            ((this.usErrorInfo==null && other.getUsErrorInfo()==null) || 
             (this.usErrorInfo!=null &&
              this.usErrorInfo.equals(other.getUsErrorInfo()))) &&
            ((this.caErrorInfo==null && other.getCaErrorInfo()==null) || 
             (this.caErrorInfo!=null &&
              this.caErrorInfo.equals(other.getCaErrorInfo()))) &&
            ((this.intlErrorInfo==null && other.getIntlErrorInfo()==null) || 
             (this.intlErrorInfo!=null &&
              this.intlErrorInfo.equals(other.getIntlErrorInfo())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getReasonCode() != null) {
            _hashCode += getReasonCode().hashCode();
        }
        if (getAddressType() != null) {
            _hashCode += getAddressType().hashCode();
        }
        if (getApartmentInfo() != null) {
            _hashCode += getApartmentInfo().hashCode();
        }
        if (getBarCode() != null) {
            _hashCode += getBarCode().hashCode();
        }
        if (getBarCodeCheckDigit() != null) {
            _hashCode += getBarCodeCheckDigit().hashCode();
        }
        if (getCareOf() != null) {
            _hashCode += getCareOf().hashCode();
        }
        if (getCityInfo() != null) {
            _hashCode += getCityInfo().hashCode();
        }
        if (getCountryInfo() != null) {
            _hashCode += getCountryInfo().hashCode();
        }
        if (getDirectionalInfo() != null) {
            _hashCode += getDirectionalInfo().hashCode();
        }
        if (getLvrInfo() != null) {
            _hashCode += getLvrInfo().hashCode();
        }
        if (getMatchScore() != null) {
            _hashCode += getMatchScore().hashCode();
        }
        if (getStandardizedAddress1() != null) {
            _hashCode += getStandardizedAddress1().hashCode();
        }
        if (getStandardizedAddress2() != null) {
            _hashCode += getStandardizedAddress2().hashCode();
        }
        if (getStandardizedAddress3() != null) {
            _hashCode += getStandardizedAddress3().hashCode();
        }
        if (getStandardizedAddress4() != null) {
            _hashCode += getStandardizedAddress4().hashCode();
        }
        if (getStandardizedAddressNoApt() != null) {
            _hashCode += getStandardizedAddressNoApt().hashCode();
        }
        if (getStandardizedCity() != null) {
            _hashCode += getStandardizedCity().hashCode();
        }
        if (getStandardizedCounty() != null) {
            _hashCode += getStandardizedCounty().hashCode();
        }
        if (getStandardizedCSP() != null) {
            _hashCode += getStandardizedCSP().hashCode();
        }
        if (getStandardizedState() != null) {
            _hashCode += getStandardizedState().hashCode();
        }
        if (getStandardizedPostalCode() != null) {
            _hashCode += getStandardizedPostalCode().hashCode();
        }
        if (getStandardizedCountry() != null) {
            _hashCode += getStandardizedCountry().hashCode();
        }
        if (getStandardizedISOCountry() != null) {
            _hashCode += getStandardizedISOCountry().hashCode();
        }
        if (getStateInfo() != null) {
            _hashCode += getStateInfo().hashCode();
        }
        if (getStreetInfo() != null) {
            _hashCode += getStreetInfo().hashCode();
        }
        if (getSuffixInfo() != null) {
            _hashCode += getSuffixInfo().hashCode();
        }
        if (getPostalCodeInfo() != null) {
            _hashCode += getPostalCodeInfo().hashCode();
        }
        if (getOverallInfo() != null) {
            _hashCode += getOverallInfo().hashCode();
        }
        if (getUsInfo() != null) {
            _hashCode += getUsInfo().hashCode();
        }
        if (getCaInfo() != null) {
            _hashCode += getCaInfo().hashCode();
        }
        if (getIntlInfo() != null) {
            _hashCode += getIntlInfo().hashCode();
        }
        if (getUsErrorInfo() != null) {
            _hashCode += getUsErrorInfo().hashCode();
        }
        if (getCaErrorInfo() != null) {
            _hashCode += getCaErrorInfo().hashCode();
        }
        if (getIntlErrorInfo() != null) {
            _hashCode += getIntlErrorInfo().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DAVReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DAVReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("apartmentInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "apartmentInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("barCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "barCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("barCodeCheckDigit");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "barCodeCheckDigit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("careOf");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "careOf"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cityInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cityInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "countryInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directionalInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directionalInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lvrInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "lvrInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("matchScore");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "matchScore"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedAddress1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedAddress1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedAddress2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedAddress2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedAddress3");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedAddress3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedAddress4");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedAddress4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedAddressNoApt");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedAddressNoApt"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedCity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedCity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedCounty");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedCounty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedCSP");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedCSP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedState");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedPostalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedPostalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("standardizedISOCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "standardizedISOCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stateInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "stateInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("streetInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "streetInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("suffixInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "suffixInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("postalCodeInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "postalCodeInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("overallInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "overallInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "usInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("caInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "caInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("intlInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "intlInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usErrorInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "usErrorInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("caErrorInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "caErrorInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("intlErrorInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "intlErrorInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
