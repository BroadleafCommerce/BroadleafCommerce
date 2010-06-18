/**
 * PayPalUpdateAgreementReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalUpdateAgreementReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String paypalBillingAgreementId;

    private java.lang.String paypalBillingAgreementDesc;

    private java.lang.String paypalBillingAgreementCustom;

    private java.lang.String paypalBillingAgreementStatus;

    private java.lang.String payer;

    private java.lang.String payerId;

    private java.lang.String payerStatus;

    private java.lang.String payerCountry;

    private java.lang.String payerBusiness;

    private java.lang.String payerSalutation;

    private java.lang.String payerFirstname;

    private java.lang.String payerMiddlename;

    private java.lang.String payerLastname;

    private java.lang.String payerSuffix;

    private java.lang.String addressStatus;

    private java.lang.String errorCode;

    private java.lang.String correlationID;

    public PayPalUpdateAgreementReply() {
    }

    public PayPalUpdateAgreementReply(
           java.math.BigInteger reasonCode,
           java.lang.String paypalBillingAgreementId,
           java.lang.String paypalBillingAgreementDesc,
           java.lang.String paypalBillingAgreementCustom,
           java.lang.String paypalBillingAgreementStatus,
           java.lang.String payer,
           java.lang.String payerId,
           java.lang.String payerStatus,
           java.lang.String payerCountry,
           java.lang.String payerBusiness,
           java.lang.String payerSalutation,
           java.lang.String payerFirstname,
           java.lang.String payerMiddlename,
           java.lang.String payerLastname,
           java.lang.String payerSuffix,
           java.lang.String addressStatus,
           java.lang.String errorCode,
           java.lang.String correlationID) {
           this.reasonCode = reasonCode;
           this.paypalBillingAgreementId = paypalBillingAgreementId;
           this.paypalBillingAgreementDesc = paypalBillingAgreementDesc;
           this.paypalBillingAgreementCustom = paypalBillingAgreementCustom;
           this.paypalBillingAgreementStatus = paypalBillingAgreementStatus;
           this.payer = payer;
           this.payerId = payerId;
           this.payerStatus = payerStatus;
           this.payerCountry = payerCountry;
           this.payerBusiness = payerBusiness;
           this.payerSalutation = payerSalutation;
           this.payerFirstname = payerFirstname;
           this.payerMiddlename = payerMiddlename;
           this.payerLastname = payerLastname;
           this.payerSuffix = payerSuffix;
           this.addressStatus = addressStatus;
           this.errorCode = errorCode;
           this.correlationID = correlationID;
    }


    /**
     * Gets the reasonCode value for this PayPalUpdateAgreementReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalUpdateAgreementReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the paypalBillingAgreementId value for this PayPalUpdateAgreementReply.
     * 
     * @return paypalBillingAgreementId
     */
    public java.lang.String getPaypalBillingAgreementId() {
        return paypalBillingAgreementId;
    }


    /**
     * Sets the paypalBillingAgreementId value for this PayPalUpdateAgreementReply.
     * 
     * @param paypalBillingAgreementId
     */
    public void setPaypalBillingAgreementId(java.lang.String paypalBillingAgreementId) {
        this.paypalBillingAgreementId = paypalBillingAgreementId;
    }


    /**
     * Gets the paypalBillingAgreementDesc value for this PayPalUpdateAgreementReply.
     * 
     * @return paypalBillingAgreementDesc
     */
    public java.lang.String getPaypalBillingAgreementDesc() {
        return paypalBillingAgreementDesc;
    }


    /**
     * Sets the paypalBillingAgreementDesc value for this PayPalUpdateAgreementReply.
     * 
     * @param paypalBillingAgreementDesc
     */
    public void setPaypalBillingAgreementDesc(java.lang.String paypalBillingAgreementDesc) {
        this.paypalBillingAgreementDesc = paypalBillingAgreementDesc;
    }


    /**
     * Gets the paypalBillingAgreementCustom value for this PayPalUpdateAgreementReply.
     * 
     * @return paypalBillingAgreementCustom
     */
    public java.lang.String getPaypalBillingAgreementCustom() {
        return paypalBillingAgreementCustom;
    }


    /**
     * Sets the paypalBillingAgreementCustom value for this PayPalUpdateAgreementReply.
     * 
     * @param paypalBillingAgreementCustom
     */
    public void setPaypalBillingAgreementCustom(java.lang.String paypalBillingAgreementCustom) {
        this.paypalBillingAgreementCustom = paypalBillingAgreementCustom;
    }


    /**
     * Gets the paypalBillingAgreementStatus value for this PayPalUpdateAgreementReply.
     * 
     * @return paypalBillingAgreementStatus
     */
    public java.lang.String getPaypalBillingAgreementStatus() {
        return paypalBillingAgreementStatus;
    }


    /**
     * Sets the paypalBillingAgreementStatus value for this PayPalUpdateAgreementReply.
     * 
     * @param paypalBillingAgreementStatus
     */
    public void setPaypalBillingAgreementStatus(java.lang.String paypalBillingAgreementStatus) {
        this.paypalBillingAgreementStatus = paypalBillingAgreementStatus;
    }


    /**
     * Gets the payer value for this PayPalUpdateAgreementReply.
     * 
     * @return payer
     */
    public java.lang.String getPayer() {
        return payer;
    }


    /**
     * Sets the payer value for this PayPalUpdateAgreementReply.
     * 
     * @param payer
     */
    public void setPayer(java.lang.String payer) {
        this.payer = payer;
    }


    /**
     * Gets the payerId value for this PayPalUpdateAgreementReply.
     * 
     * @return payerId
     */
    public java.lang.String getPayerId() {
        return payerId;
    }


    /**
     * Sets the payerId value for this PayPalUpdateAgreementReply.
     * 
     * @param payerId
     */
    public void setPayerId(java.lang.String payerId) {
        this.payerId = payerId;
    }


    /**
     * Gets the payerStatus value for this PayPalUpdateAgreementReply.
     * 
     * @return payerStatus
     */
    public java.lang.String getPayerStatus() {
        return payerStatus;
    }


    /**
     * Sets the payerStatus value for this PayPalUpdateAgreementReply.
     * 
     * @param payerStatus
     */
    public void setPayerStatus(java.lang.String payerStatus) {
        this.payerStatus = payerStatus;
    }


    /**
     * Gets the payerCountry value for this PayPalUpdateAgreementReply.
     * 
     * @return payerCountry
     */
    public java.lang.String getPayerCountry() {
        return payerCountry;
    }


    /**
     * Sets the payerCountry value for this PayPalUpdateAgreementReply.
     * 
     * @param payerCountry
     */
    public void setPayerCountry(java.lang.String payerCountry) {
        this.payerCountry = payerCountry;
    }


    /**
     * Gets the payerBusiness value for this PayPalUpdateAgreementReply.
     * 
     * @return payerBusiness
     */
    public java.lang.String getPayerBusiness() {
        return payerBusiness;
    }


    /**
     * Sets the payerBusiness value for this PayPalUpdateAgreementReply.
     * 
     * @param payerBusiness
     */
    public void setPayerBusiness(java.lang.String payerBusiness) {
        this.payerBusiness = payerBusiness;
    }


    /**
     * Gets the payerSalutation value for this PayPalUpdateAgreementReply.
     * 
     * @return payerSalutation
     */
    public java.lang.String getPayerSalutation() {
        return payerSalutation;
    }


    /**
     * Sets the payerSalutation value for this PayPalUpdateAgreementReply.
     * 
     * @param payerSalutation
     */
    public void setPayerSalutation(java.lang.String payerSalutation) {
        this.payerSalutation = payerSalutation;
    }


    /**
     * Gets the payerFirstname value for this PayPalUpdateAgreementReply.
     * 
     * @return payerFirstname
     */
    public java.lang.String getPayerFirstname() {
        return payerFirstname;
    }


    /**
     * Sets the payerFirstname value for this PayPalUpdateAgreementReply.
     * 
     * @param payerFirstname
     */
    public void setPayerFirstname(java.lang.String payerFirstname) {
        this.payerFirstname = payerFirstname;
    }


    /**
     * Gets the payerMiddlename value for this PayPalUpdateAgreementReply.
     * 
     * @return payerMiddlename
     */
    public java.lang.String getPayerMiddlename() {
        return payerMiddlename;
    }


    /**
     * Sets the payerMiddlename value for this PayPalUpdateAgreementReply.
     * 
     * @param payerMiddlename
     */
    public void setPayerMiddlename(java.lang.String payerMiddlename) {
        this.payerMiddlename = payerMiddlename;
    }


    /**
     * Gets the payerLastname value for this PayPalUpdateAgreementReply.
     * 
     * @return payerLastname
     */
    public java.lang.String getPayerLastname() {
        return payerLastname;
    }


    /**
     * Sets the payerLastname value for this PayPalUpdateAgreementReply.
     * 
     * @param payerLastname
     */
    public void setPayerLastname(java.lang.String payerLastname) {
        this.payerLastname = payerLastname;
    }


    /**
     * Gets the payerSuffix value for this PayPalUpdateAgreementReply.
     * 
     * @return payerSuffix
     */
    public java.lang.String getPayerSuffix() {
        return payerSuffix;
    }


    /**
     * Sets the payerSuffix value for this PayPalUpdateAgreementReply.
     * 
     * @param payerSuffix
     */
    public void setPayerSuffix(java.lang.String payerSuffix) {
        this.payerSuffix = payerSuffix;
    }


    /**
     * Gets the addressStatus value for this PayPalUpdateAgreementReply.
     * 
     * @return addressStatus
     */
    public java.lang.String getAddressStatus() {
        return addressStatus;
    }


    /**
     * Sets the addressStatus value for this PayPalUpdateAgreementReply.
     * 
     * @param addressStatus
     */
    public void setAddressStatus(java.lang.String addressStatus) {
        this.addressStatus = addressStatus;
    }


    /**
     * Gets the errorCode value for this PayPalUpdateAgreementReply.
     * 
     * @return errorCode
     */
    public java.lang.String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets the errorCode value for this PayPalUpdateAgreementReply.
     * 
     * @param errorCode
     */
    public void setErrorCode(java.lang.String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Gets the correlationID value for this PayPalUpdateAgreementReply.
     * 
     * @return correlationID
     */
    public java.lang.String getCorrelationID() {
        return correlationID;
    }


    /**
     * Sets the correlationID value for this PayPalUpdateAgreementReply.
     * 
     * @param correlationID
     */
    public void setCorrelationID(java.lang.String correlationID) {
        this.correlationID = correlationID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalUpdateAgreementReply)) return false;
        PayPalUpdateAgreementReply other = (PayPalUpdateAgreementReply) obj;
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
            ((this.paypalBillingAgreementId==null && other.getPaypalBillingAgreementId()==null) || 
             (this.paypalBillingAgreementId!=null &&
              this.paypalBillingAgreementId.equals(other.getPaypalBillingAgreementId()))) &&
            ((this.paypalBillingAgreementDesc==null && other.getPaypalBillingAgreementDesc()==null) || 
             (this.paypalBillingAgreementDesc!=null &&
              this.paypalBillingAgreementDesc.equals(other.getPaypalBillingAgreementDesc()))) &&
            ((this.paypalBillingAgreementCustom==null && other.getPaypalBillingAgreementCustom()==null) || 
             (this.paypalBillingAgreementCustom!=null &&
              this.paypalBillingAgreementCustom.equals(other.getPaypalBillingAgreementCustom()))) &&
            ((this.paypalBillingAgreementStatus==null && other.getPaypalBillingAgreementStatus()==null) || 
             (this.paypalBillingAgreementStatus!=null &&
              this.paypalBillingAgreementStatus.equals(other.getPaypalBillingAgreementStatus()))) &&
            ((this.payer==null && other.getPayer()==null) || 
             (this.payer!=null &&
              this.payer.equals(other.getPayer()))) &&
            ((this.payerId==null && other.getPayerId()==null) || 
             (this.payerId!=null &&
              this.payerId.equals(other.getPayerId()))) &&
            ((this.payerStatus==null && other.getPayerStatus()==null) || 
             (this.payerStatus!=null &&
              this.payerStatus.equals(other.getPayerStatus()))) &&
            ((this.payerCountry==null && other.getPayerCountry()==null) || 
             (this.payerCountry!=null &&
              this.payerCountry.equals(other.getPayerCountry()))) &&
            ((this.payerBusiness==null && other.getPayerBusiness()==null) || 
             (this.payerBusiness!=null &&
              this.payerBusiness.equals(other.getPayerBusiness()))) &&
            ((this.payerSalutation==null && other.getPayerSalutation()==null) || 
             (this.payerSalutation!=null &&
              this.payerSalutation.equals(other.getPayerSalutation()))) &&
            ((this.payerFirstname==null && other.getPayerFirstname()==null) || 
             (this.payerFirstname!=null &&
              this.payerFirstname.equals(other.getPayerFirstname()))) &&
            ((this.payerMiddlename==null && other.getPayerMiddlename()==null) || 
             (this.payerMiddlename!=null &&
              this.payerMiddlename.equals(other.getPayerMiddlename()))) &&
            ((this.payerLastname==null && other.getPayerLastname()==null) || 
             (this.payerLastname!=null &&
              this.payerLastname.equals(other.getPayerLastname()))) &&
            ((this.payerSuffix==null && other.getPayerSuffix()==null) || 
             (this.payerSuffix!=null &&
              this.payerSuffix.equals(other.getPayerSuffix()))) &&
            ((this.addressStatus==null && other.getAddressStatus()==null) || 
             (this.addressStatus!=null &&
              this.addressStatus.equals(other.getAddressStatus()))) &&
            ((this.errorCode==null && other.getErrorCode()==null) || 
             (this.errorCode!=null &&
              this.errorCode.equals(other.getErrorCode()))) &&
            ((this.correlationID==null && other.getCorrelationID()==null) || 
             (this.correlationID!=null &&
              this.correlationID.equals(other.getCorrelationID())));
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
        if (getPaypalBillingAgreementId() != null) {
            _hashCode += getPaypalBillingAgreementId().hashCode();
        }
        if (getPaypalBillingAgreementDesc() != null) {
            _hashCode += getPaypalBillingAgreementDesc().hashCode();
        }
        if (getPaypalBillingAgreementCustom() != null) {
            _hashCode += getPaypalBillingAgreementCustom().hashCode();
        }
        if (getPaypalBillingAgreementStatus() != null) {
            _hashCode += getPaypalBillingAgreementStatus().hashCode();
        }
        if (getPayer() != null) {
            _hashCode += getPayer().hashCode();
        }
        if (getPayerId() != null) {
            _hashCode += getPayerId().hashCode();
        }
        if (getPayerStatus() != null) {
            _hashCode += getPayerStatus().hashCode();
        }
        if (getPayerCountry() != null) {
            _hashCode += getPayerCountry().hashCode();
        }
        if (getPayerBusiness() != null) {
            _hashCode += getPayerBusiness().hashCode();
        }
        if (getPayerSalutation() != null) {
            _hashCode += getPayerSalutation().hashCode();
        }
        if (getPayerFirstname() != null) {
            _hashCode += getPayerFirstname().hashCode();
        }
        if (getPayerMiddlename() != null) {
            _hashCode += getPayerMiddlename().hashCode();
        }
        if (getPayerLastname() != null) {
            _hashCode += getPayerLastname().hashCode();
        }
        if (getPayerSuffix() != null) {
            _hashCode += getPayerSuffix().hashCode();
        }
        if (getAddressStatus() != null) {
            _hashCode += getAddressStatus().hashCode();
        }
        if (getErrorCode() != null) {
            _hashCode += getErrorCode().hashCode();
        }
        if (getCorrelationID() != null) {
            _hashCode += getCorrelationID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalUpdateAgreementReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalUpdateAgreementReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementCustom");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementCustom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payer");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerCountry");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerCountry"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerBusiness");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerBusiness"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerSalutation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerSalutation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerFirstname");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerFirstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerMiddlename");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerMiddlename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerLastname");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerLastname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerSuffix");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerSuffix"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("correlationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "correlationID"));
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
