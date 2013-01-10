/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.vendor.cybersource.service.message;

public class CyberSourceBillingRequest  implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private java.lang.String title;
    private java.lang.String firstName;
    private java.lang.String middleName;
    private java.lang.String lastName;
    private java.lang.String suffix;
    private java.lang.String street1;
    private java.lang.String street2;
    private java.lang.String street3;
    private java.lang.String street4;
    private java.lang.String city;
    private java.lang.String county;
    private java.lang.String state;
    private java.lang.String postalCode;
    private java.lang.String country;
    private java.lang.String company;
    private java.lang.String companyTaxID;
    private java.lang.String phoneNumber;
    private java.lang.String email;
    private java.lang.String ipAddress;
    private java.lang.String ipNetworkAddress;
    private java.lang.String dateOfBirth;
    private java.lang.String driversLicenseNumber;
    private java.lang.String driversLicenseState;
    private java.lang.String ssn;

    /**
     * Gets the title value for this BillTo.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }

    /**
     * Sets the title value for this BillTo.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    /**
     * Gets the firstName value for this BillTo.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName value for this BillTo.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middleName value for this BillTo.
     * 
     * @return middleName
     */
    public java.lang.String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middleName value for this BillTo.
     * 
     * @param middleName
     */
    public void setMiddleName(java.lang.String middleName) {
        this.middleName = middleName;
    }

    /**
     * Gets the lastName value for this BillTo.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName value for this BillTo.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the suffix value for this BillTo.
     * 
     * @return suffix
     */
    public java.lang.String getSuffix() {
        return suffix;
    }

    /**
     * Sets the suffix value for this BillTo.
     * 
     * @param suffix
     */
    public void setSuffix(java.lang.String suffix) {
        this.suffix = suffix;
    }

    /**
     * Gets the street1 value for this BillTo.
     * 
     * @return street1
     */
    public java.lang.String getStreet1() {
        return street1;
    }

    /**
     * Sets the street1 value for this BillTo.
     * 
     * @param street1
     */
    public void setStreet1(java.lang.String street1) {
        this.street1 = street1;
    }

    /**
     * Gets the street2 value for this BillTo.
     * 
     * @return street2
     */
    public java.lang.String getStreet2() {
        return street2;
    }

    /**
     * Sets the street2 value for this BillTo.
     * 
     * @param street2
     */
    public void setStreet2(java.lang.String street2) {
        this.street2 = street2;
    }

    /**
     * Gets the street3 value for this BillTo.
     * 
     * @return street3
     */
    public java.lang.String getStreet3() {
        return street3;
    }

    /**
     * Sets the street3 value for this BillTo.
     * 
     * @param street3
     */
    public void setStreet3(java.lang.String street3) {
        this.street3 = street3;
    }

    /**
     * Gets the street4 value for this BillTo.
     * 
     * @return street4
     */
    public java.lang.String getStreet4() {
        return street4;
    }

    /**
     * Sets the street4 value for this BillTo.
     * 
     * @param street4
     */
    public void setStreet4(java.lang.String street4) {
        this.street4 = street4;
    }

    /**
     * Gets the city value for this BillTo.
     * 
     * @return city
     */
    public java.lang.String getCity() {
        return city;
    }

    /**
     * Sets the city value for this BillTo.
     * 
     * @param city
     */
    public void setCity(java.lang.String city) {
        this.city = city;
    }

    /**
     * Gets the county value for this BillTo.
     * 
     * @return county
     */
    public java.lang.String getCounty() {
        return county;
    }

    /**
     * Sets the county value for this BillTo.
     * 
     * @param county
     */
    public void setCounty(java.lang.String county) {
        this.county = county;
    }

    /**
     * Gets the state value for this BillTo.
     * 
     * @return state
     */
    public java.lang.String getState() {
        return state;
    }

    /**
     * Sets the state value for this BillTo.
     * 
     * @param state
     */
    public void setState(java.lang.String state) {
        this.state = state;
    }

    /**
     * Gets the postalCode value for this BillTo.
     * 
     * @return postalCode
     */
    public java.lang.String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postalCode value for this BillTo.
     * 
     * @param postalCode
     */
    public void setPostalCode(java.lang.String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the country value for this BillTo.
     * 
     * @return country
     */
    public java.lang.String getCountry() {
        return country;
    }

    /**
     * Sets the country value for this BillTo.
     * 
     * @param country
     */
    public void setCountry(java.lang.String country) {
        this.country = country;
    }

    /**
     * Gets the company value for this BillTo.
     * 
     * @return company
     */
    public java.lang.String getCompany() {
        return company;
    }

    /**
     * Sets the company value for this BillTo.
     * 
     * @param company
     */
    public void setCompany(java.lang.String company) {
        this.company = company;
    }

    /**
     * Gets the companyTaxID value for this BillTo.
     * 
     * @return companyTaxID
     */
    public java.lang.String getCompanyTaxID() {
        return companyTaxID;
    }

    /**
     * Sets the companyTaxID value for this BillTo.
     * 
     * @param companyTaxID
     */
    public void setCompanyTaxID(java.lang.String companyTaxID) {
        this.companyTaxID = companyTaxID;
    }

    /**
     * Gets the phoneNumber value for this BillTo.
     * 
     * @return phoneNumber
     */
    public java.lang.String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phoneNumber value for this BillTo.
     * 
     * @param phoneNumber
     */
    public void setPhoneNumber(java.lang.String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email value for this BillTo.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }

    /**
     * Sets the email value for this BillTo.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    /**
     * Gets the ipAddress value for this BillTo.
     * 
     * @return ipAddress
     */
    public java.lang.String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the ipAddress value for this BillTo.
     * 
     * @param ipAddress
     */
    public void setIpAddress(java.lang.String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Gets the ipNetworkAddress value for this BillTo.
     * 
     * @return ipNetworkAddress
     */
    public java.lang.String getIpNetworkAddress() {
        return ipNetworkAddress;
    }

    /**
     * Sets the ipNetworkAddress value for this BillTo.
     * 
     * @param ipNetworkAddress
     */
    public void setIpNetworkAddress(java.lang.String ipNetworkAddress) {
        this.ipNetworkAddress = ipNetworkAddress;
    }

    /**
     * Gets the dateOfBirth value for this BillTo.
     * 
     * @return dateOfBirth
     */
    public java.lang.String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the dateOfBirth value for this BillTo.
     * 
     * @param dateOfBirth
     */
    public void setDateOfBirth(java.lang.String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the driversLicenseNumber value for this BillTo.
     * 
     * @return driversLicenseNumber
     */
    public java.lang.String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    /**
     * Sets the driversLicenseNumber value for this BillTo.
     * 
     * @param driversLicenseNumber
     */
    public void setDriversLicenseNumber(java.lang.String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    /**
     * Gets the driversLicenseState value for this BillTo.
     * 
     * @return driversLicenseState
     */
    public java.lang.String getDriversLicenseState() {
        return driversLicenseState;
    }

    /**
     * Sets the driversLicenseState value for this BillTo.
     * 
     * @param driversLicenseState
     */
    public void setDriversLicenseState(java.lang.String driversLicenseState) {
        this.driversLicenseState = driversLicenseState;
    }

    /**
     * Gets the ssn value for this BillTo.
     * 
     * @return ssn
     */
    public java.lang.String getSsn() {
        return ssn;
    }

    /**
     * Sets the ssn value for this BillTo.
     * 
     * @param ssn
     */
    public void setSsn(java.lang.String ssn) {
        this.ssn = ssn;
    }

}
