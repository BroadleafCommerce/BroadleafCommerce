/**
 * PayPalPreapprovedUpdateReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalPreapprovedUpdateReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String requestDateTime;

    private java.lang.String reconciliationID;

    private java.lang.String payerStatus;

    private java.lang.String payerName;

    private java.lang.String payerCountry;

    private java.lang.String mpStatus;

    private java.lang.String payer;

    private java.lang.String payerID;

    private java.lang.String payerBusiness;

    private java.lang.String desc;

    private java.lang.String mpMax;

    private java.lang.String paymentSourceID;

    public PayPalPreapprovedUpdateReply() {
    }

    public PayPalPreapprovedUpdateReply(
           java.math.BigInteger reasonCode,
           java.lang.String requestDateTime,
           java.lang.String reconciliationID,
           java.lang.String payerStatus,
           java.lang.String payerName,
           java.lang.String payerCountry,
           java.lang.String mpStatus,
           java.lang.String payer,
           java.lang.String payerID,
           java.lang.String payerBusiness,
           java.lang.String desc,
           java.lang.String mpMax,
           java.lang.String paymentSourceID) {
           this.reasonCode = reasonCode;
           this.requestDateTime = requestDateTime;
           this.reconciliationID = reconciliationID;
           this.payerStatus = payerStatus;
           this.payerName = payerName;
           this.payerCountry = payerCountry;
           this.mpStatus = mpStatus;
           this.payer = payer;
           this.payerID = payerID;
           this.payerBusiness = payerBusiness;
           this.desc = desc;
           this.mpMax = mpMax;
           this.paymentSourceID = paymentSourceID;
    }


    /**
     * Gets the reasonCode value for this PayPalPreapprovedUpdateReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalPreapprovedUpdateReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the requestDateTime value for this PayPalPreapprovedUpdateReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this PayPalPreapprovedUpdateReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the reconciliationID value for this PayPalPreapprovedUpdateReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this PayPalPreapprovedUpdateReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the payerStatus value for this PayPalPreapprovedUpdateReply.
     * 
     * @return payerStatus
     */
    public java.lang.String getPayerStatus() {
        return payerStatus;
    }


    /**
     * Sets the payerStatus value for this PayPalPreapprovedUpdateReply.
     * 
     * @param payerStatus
     */
    public void setPayerStatus(java.lang.String payerStatus) {
        this.payerStatus = payerStatus;
    }


    /**
     * Gets the payerName value for this PayPalPreapprovedUpdateReply.
     * 
     * @return payerName
     */
    public java.lang.String getPayerName() {
        return payerName;
    }


    /**
     * Sets the payerName value for this PayPalPreapprovedUpdateReply.
     * 
     * @param payerName
     */
    public void setPayerName(java.lang.String payerName) {
        this.payerName = payerName;
    }


    /**
     * Gets the payerCountry value for this PayPalPreapprovedUpdateReply.
     * 
     * @return payerCountry
     */
    public java.lang.String getPayerCountry() {
        return payerCountry;
    }


    /**
     * Sets the payerCountry value for this PayPalPreapprovedUpdateReply.
     * 
     * @param payerCountry
     */
    public void setPayerCountry(java.lang.String payerCountry) {
        this.payerCountry = payerCountry;
    }


    /**
     * Gets the mpStatus value for this PayPalPreapprovedUpdateReply.
     * 
     * @return mpStatus
     */
    public java.lang.String getMpStatus() {
        return mpStatus;
    }


    /**
     * Sets the mpStatus value for this PayPalPreapprovedUpdateReply.
     * 
     * @param mpStatus
     */
    public void setMpStatus(java.lang.String mpStatus) {
        this.mpStatus = mpStatus;
    }


    /**
     * Gets the payer value for this PayPalPreapprovedUpdateReply.
     * 
     * @return payer
     */
    public java.lang.String getPayer() {
        return payer;
    }


    /**
     * Sets the payer value for this PayPalPreapprovedUpdateReply.
     * 
     * @param payer
     */
    public void setPayer(java.lang.String payer) {
        this.payer = payer;
    }


    /**
     * Gets the payerID value for this PayPalPreapprovedUpdateReply.
     * 
     * @return payerID
     */
    public java.lang.String getPayerID() {
        return payerID;
    }


    /**
     * Sets the payerID value for this PayPalPreapprovedUpdateReply.
     * 
     * @param payerID
     */
    public void setPayerID(java.lang.String payerID) {
        this.payerID = payerID;
    }


    /**
     * Gets the payerBusiness value for this PayPalPreapprovedUpdateReply.
     * 
     * @return payerBusiness
     */
    public java.lang.String getPayerBusiness() {
        return payerBusiness;
    }


    /**
     * Sets the payerBusiness value for this PayPalPreapprovedUpdateReply.
     * 
     * @param payerBusiness
     */
    public void setPayerBusiness(java.lang.String payerBusiness) {
        this.payerBusiness = payerBusiness;
    }


    /**
     * Gets the desc value for this PayPalPreapprovedUpdateReply.
     * 
     * @return desc
     */
    public java.lang.String getDesc() {
        return desc;
    }


    /**
     * Sets the desc value for this PayPalPreapprovedUpdateReply.
     * 
     * @param desc
     */
    public void setDesc(java.lang.String desc) {
        this.desc = desc;
    }


    /**
     * Gets the mpMax value for this PayPalPreapprovedUpdateReply.
     * 
     * @return mpMax
     */
    public java.lang.String getMpMax() {
        return mpMax;
    }


    /**
     * Sets the mpMax value for this PayPalPreapprovedUpdateReply.
     * 
     * @param mpMax
     */
    public void setMpMax(java.lang.String mpMax) {
        this.mpMax = mpMax;
    }


    /**
     * Gets the paymentSourceID value for this PayPalPreapprovedUpdateReply.
     * 
     * @return paymentSourceID
     */
    public java.lang.String getPaymentSourceID() {
        return paymentSourceID;
    }


    /**
     * Sets the paymentSourceID value for this PayPalPreapprovedUpdateReply.
     * 
     * @param paymentSourceID
     */
    public void setPaymentSourceID(java.lang.String paymentSourceID) {
        this.paymentSourceID = paymentSourceID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalPreapprovedUpdateReply)) return false;
        PayPalPreapprovedUpdateReply other = (PayPalPreapprovedUpdateReply) obj;
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
            ((this.requestDateTime==null && other.getRequestDateTime()==null) || 
             (this.requestDateTime!=null &&
              this.requestDateTime.equals(other.getRequestDateTime()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.payerStatus==null && other.getPayerStatus()==null) || 
             (this.payerStatus!=null &&
              this.payerStatus.equals(other.getPayerStatus()))) &&
            ((this.payerName==null && other.getPayerName()==null) || 
             (this.payerName!=null &&
              this.payerName.equals(other.getPayerName()))) &&
            ((this.payerCountry==null && other.getPayerCountry()==null) || 
             (this.payerCountry!=null &&
              this.payerCountry.equals(other.getPayerCountry()))) &&
            ((this.mpStatus==null && other.getMpStatus()==null) || 
             (this.mpStatus!=null &&
              this.mpStatus.equals(other.getMpStatus()))) &&
            ((this.payer==null && other.getPayer()==null) || 
             (this.payer!=null &&
              this.payer.equals(other.getPayer()))) &&
            ((this.payerID==null && other.getPayerID()==null) || 
             (this.payerID!=null &&
              this.payerID.equals(other.getPayerID()))) &&
            ((this.payerBusiness==null && other.getPayerBusiness()==null) || 
             (this.payerBusiness!=null &&
              this.payerBusiness.equals(other.getPayerBusiness()))) &&
            ((this.desc==null && other.getDesc()==null) || 
             (this.desc!=null &&
              this.desc.equals(other.getDesc()))) &&
            ((this.mpMax==null && other.getMpMax()==null) || 
             (this.mpMax!=null &&
              this.mpMax.equals(other.getMpMax()))) &&
            ((this.paymentSourceID==null && other.getPaymentSourceID()==null) || 
             (this.paymentSourceID!=null &&
              this.paymentSourceID.equals(other.getPaymentSourceID())));
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
        if (getRequestDateTime() != null) {
            _hashCode += getRequestDateTime().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getPayerStatus() != null) {
            _hashCode += getPayerStatus().hashCode();
        }
        if (getPayerName() != null) {
            _hashCode += getPayerName().hashCode();
        }
        if (getPayerCountry() != null) {
            _hashCode += getPayerCountry().hashCode();
        }
        if (getMpStatus() != null) {
            _hashCode += getMpStatus().hashCode();
        }
        if (getPayer() != null) {
            _hashCode += getPayer().hashCode();
        }
        if (getPayerID() != null) {
            _hashCode += getPayerID().hashCode();
        }
        if (getPayerBusiness() != null) {
            _hashCode += getPayerBusiness().hashCode();
        }
        if (getDesc() != null) {
            _hashCode += getDesc().hashCode();
        }
        if (getMpMax() != null) {
            _hashCode += getMpMax().hashCode();
        }
        if (getPaymentSourceID() != null) {
            _hashCode += getPaymentSourceID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalPreapprovedUpdateReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedUpdateReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "requestDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reconciliationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reconciliationID"));
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
        elemField.setFieldName("payerName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerName"));
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
        elemField.setFieldName("mpStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "mpStatus"));
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
        elemField.setFieldName("payerID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerID"));
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
        elemField.setFieldName("desc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "desc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mpMax");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "mpMax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentSourceID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentSourceID"));
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
