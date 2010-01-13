/**
 * PayPalPreapprovedPaymentReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalPreapprovedPaymentReply  implements java.io.Serializable {
    private java.math.BigInteger reasonCode;

    private java.lang.String requestDateTime;

    private java.lang.String reconciliationID;

    private java.lang.String payerStatus;

    private java.lang.String payerName;

    private java.lang.String transactionType;

    private java.lang.String feeAmount;

    private java.lang.String payerCountry;

    private java.lang.String pendingReason;

    private java.lang.String paymentStatus;

    private java.lang.String mpStatus;

    private java.lang.String payer;

    private java.lang.String payerID;

    private java.lang.String payerBusiness;

    private java.lang.String transactionID;

    private java.lang.String desc;

    private java.lang.String mpMax;

    private java.lang.String paymentType;

    private java.lang.String paymentDate;

    private java.lang.String paymentGrossAmount;

    private java.lang.String settleAmount;

    private java.lang.String taxAmount;

    private java.lang.String exchangeRate;

    private java.lang.String paymentSourceID;

    public PayPalPreapprovedPaymentReply() {
    }

    public PayPalPreapprovedPaymentReply(
           java.math.BigInteger reasonCode,
           java.lang.String requestDateTime,
           java.lang.String reconciliationID,
           java.lang.String payerStatus,
           java.lang.String payerName,
           java.lang.String transactionType,
           java.lang.String feeAmount,
           java.lang.String payerCountry,
           java.lang.String pendingReason,
           java.lang.String paymentStatus,
           java.lang.String mpStatus,
           java.lang.String payer,
           java.lang.String payerID,
           java.lang.String payerBusiness,
           java.lang.String transactionID,
           java.lang.String desc,
           java.lang.String mpMax,
           java.lang.String paymentType,
           java.lang.String paymentDate,
           java.lang.String paymentGrossAmount,
           java.lang.String settleAmount,
           java.lang.String taxAmount,
           java.lang.String exchangeRate,
           java.lang.String paymentSourceID) {
           this.reasonCode = reasonCode;
           this.requestDateTime = requestDateTime;
           this.reconciliationID = reconciliationID;
           this.payerStatus = payerStatus;
           this.payerName = payerName;
           this.transactionType = transactionType;
           this.feeAmount = feeAmount;
           this.payerCountry = payerCountry;
           this.pendingReason = pendingReason;
           this.paymentStatus = paymentStatus;
           this.mpStatus = mpStatus;
           this.payer = payer;
           this.payerID = payerID;
           this.payerBusiness = payerBusiness;
           this.transactionID = transactionID;
           this.desc = desc;
           this.mpMax = mpMax;
           this.paymentType = paymentType;
           this.paymentDate = paymentDate;
           this.paymentGrossAmount = paymentGrossAmount;
           this.settleAmount = settleAmount;
           this.taxAmount = taxAmount;
           this.exchangeRate = exchangeRate;
           this.paymentSourceID = paymentSourceID;
    }


    /**
     * Gets the reasonCode value for this PayPalPreapprovedPaymentReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this PayPalPreapprovedPaymentReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the requestDateTime value for this PayPalPreapprovedPaymentReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this PayPalPreapprovedPaymentReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the reconciliationID value for this PayPalPreapprovedPaymentReply.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this PayPalPreapprovedPaymentReply.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the payerStatus value for this PayPalPreapprovedPaymentReply.
     * 
     * @return payerStatus
     */
    public java.lang.String getPayerStatus() {
        return payerStatus;
    }


    /**
     * Sets the payerStatus value for this PayPalPreapprovedPaymentReply.
     * 
     * @param payerStatus
     */
    public void setPayerStatus(java.lang.String payerStatus) {
        this.payerStatus = payerStatus;
    }


    /**
     * Gets the payerName value for this PayPalPreapprovedPaymentReply.
     * 
     * @return payerName
     */
    public java.lang.String getPayerName() {
        return payerName;
    }


    /**
     * Sets the payerName value for this PayPalPreapprovedPaymentReply.
     * 
     * @param payerName
     */
    public void setPayerName(java.lang.String payerName) {
        this.payerName = payerName;
    }


    /**
     * Gets the transactionType value for this PayPalPreapprovedPaymentReply.
     * 
     * @return transactionType
     */
    public java.lang.String getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this PayPalPreapprovedPaymentReply.
     * 
     * @param transactionType
     */
    public void setTransactionType(java.lang.String transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the feeAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @return feeAmount
     */
    public java.lang.String getFeeAmount() {
        return feeAmount;
    }


    /**
     * Sets the feeAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @param feeAmount
     */
    public void setFeeAmount(java.lang.String feeAmount) {
        this.feeAmount = feeAmount;
    }


    /**
     * Gets the payerCountry value for this PayPalPreapprovedPaymentReply.
     * 
     * @return payerCountry
     */
    public java.lang.String getPayerCountry() {
        return payerCountry;
    }


    /**
     * Sets the payerCountry value for this PayPalPreapprovedPaymentReply.
     * 
     * @param payerCountry
     */
    public void setPayerCountry(java.lang.String payerCountry) {
        this.payerCountry = payerCountry;
    }


    /**
     * Gets the pendingReason value for this PayPalPreapprovedPaymentReply.
     * 
     * @return pendingReason
     */
    public java.lang.String getPendingReason() {
        return pendingReason;
    }


    /**
     * Sets the pendingReason value for this PayPalPreapprovedPaymentReply.
     * 
     * @param pendingReason
     */
    public void setPendingReason(java.lang.String pendingReason) {
        this.pendingReason = pendingReason;
    }


    /**
     * Gets the paymentStatus value for this PayPalPreapprovedPaymentReply.
     * 
     * @return paymentStatus
     */
    public java.lang.String getPaymentStatus() {
        return paymentStatus;
    }


    /**
     * Sets the paymentStatus value for this PayPalPreapprovedPaymentReply.
     * 
     * @param paymentStatus
     */
    public void setPaymentStatus(java.lang.String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    /**
     * Gets the mpStatus value for this PayPalPreapprovedPaymentReply.
     * 
     * @return mpStatus
     */
    public java.lang.String getMpStatus() {
        return mpStatus;
    }


    /**
     * Sets the mpStatus value for this PayPalPreapprovedPaymentReply.
     * 
     * @param mpStatus
     */
    public void setMpStatus(java.lang.String mpStatus) {
        this.mpStatus = mpStatus;
    }


    /**
     * Gets the payer value for this PayPalPreapprovedPaymentReply.
     * 
     * @return payer
     */
    public java.lang.String getPayer() {
        return payer;
    }


    /**
     * Sets the payer value for this PayPalPreapprovedPaymentReply.
     * 
     * @param payer
     */
    public void setPayer(java.lang.String payer) {
        this.payer = payer;
    }


    /**
     * Gets the payerID value for this PayPalPreapprovedPaymentReply.
     * 
     * @return payerID
     */
    public java.lang.String getPayerID() {
        return payerID;
    }


    /**
     * Sets the payerID value for this PayPalPreapprovedPaymentReply.
     * 
     * @param payerID
     */
    public void setPayerID(java.lang.String payerID) {
        this.payerID = payerID;
    }


    /**
     * Gets the payerBusiness value for this PayPalPreapprovedPaymentReply.
     * 
     * @return payerBusiness
     */
    public java.lang.String getPayerBusiness() {
        return payerBusiness;
    }


    /**
     * Sets the payerBusiness value for this PayPalPreapprovedPaymentReply.
     * 
     * @param payerBusiness
     */
    public void setPayerBusiness(java.lang.String payerBusiness) {
        this.payerBusiness = payerBusiness;
    }


    /**
     * Gets the transactionID value for this PayPalPreapprovedPaymentReply.
     * 
     * @return transactionID
     */
    public java.lang.String getTransactionID() {
        return transactionID;
    }


    /**
     * Sets the transactionID value for this PayPalPreapprovedPaymentReply.
     * 
     * @param transactionID
     */
    public void setTransactionID(java.lang.String transactionID) {
        this.transactionID = transactionID;
    }


    /**
     * Gets the desc value for this PayPalPreapprovedPaymentReply.
     * 
     * @return desc
     */
    public java.lang.String getDesc() {
        return desc;
    }


    /**
     * Sets the desc value for this PayPalPreapprovedPaymentReply.
     * 
     * @param desc
     */
    public void setDesc(java.lang.String desc) {
        this.desc = desc;
    }


    /**
     * Gets the mpMax value for this PayPalPreapprovedPaymentReply.
     * 
     * @return mpMax
     */
    public java.lang.String getMpMax() {
        return mpMax;
    }


    /**
     * Sets the mpMax value for this PayPalPreapprovedPaymentReply.
     * 
     * @param mpMax
     */
    public void setMpMax(java.lang.String mpMax) {
        this.mpMax = mpMax;
    }


    /**
     * Gets the paymentType value for this PayPalPreapprovedPaymentReply.
     * 
     * @return paymentType
     */
    public java.lang.String getPaymentType() {
        return paymentType;
    }


    /**
     * Sets the paymentType value for this PayPalPreapprovedPaymentReply.
     * 
     * @param paymentType
     */
    public void setPaymentType(java.lang.String paymentType) {
        this.paymentType = paymentType;
    }


    /**
     * Gets the paymentDate value for this PayPalPreapprovedPaymentReply.
     * 
     * @return paymentDate
     */
    public java.lang.String getPaymentDate() {
        return paymentDate;
    }


    /**
     * Sets the paymentDate value for this PayPalPreapprovedPaymentReply.
     * 
     * @param paymentDate
     */
    public void setPaymentDate(java.lang.String paymentDate) {
        this.paymentDate = paymentDate;
    }


    /**
     * Gets the paymentGrossAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @return paymentGrossAmount
     */
    public java.lang.String getPaymentGrossAmount() {
        return paymentGrossAmount;
    }


    /**
     * Sets the paymentGrossAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @param paymentGrossAmount
     */
    public void setPaymentGrossAmount(java.lang.String paymentGrossAmount) {
        this.paymentGrossAmount = paymentGrossAmount;
    }


    /**
     * Gets the settleAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @return settleAmount
     */
    public java.lang.String getSettleAmount() {
        return settleAmount;
    }


    /**
     * Sets the settleAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @param settleAmount
     */
    public void setSettleAmount(java.lang.String settleAmount) {
        this.settleAmount = settleAmount;
    }


    /**
     * Gets the taxAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @return taxAmount
     */
    public java.lang.String getTaxAmount() {
        return taxAmount;
    }


    /**
     * Sets the taxAmount value for this PayPalPreapprovedPaymentReply.
     * 
     * @param taxAmount
     */
    public void setTaxAmount(java.lang.String taxAmount) {
        this.taxAmount = taxAmount;
    }


    /**
     * Gets the exchangeRate value for this PayPalPreapprovedPaymentReply.
     * 
     * @return exchangeRate
     */
    public java.lang.String getExchangeRate() {
        return exchangeRate;
    }


    /**
     * Sets the exchangeRate value for this PayPalPreapprovedPaymentReply.
     * 
     * @param exchangeRate
     */
    public void setExchangeRate(java.lang.String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }


    /**
     * Gets the paymentSourceID value for this PayPalPreapprovedPaymentReply.
     * 
     * @return paymentSourceID
     */
    public java.lang.String getPaymentSourceID() {
        return paymentSourceID;
    }


    /**
     * Sets the paymentSourceID value for this PayPalPreapprovedPaymentReply.
     * 
     * @param paymentSourceID
     */
    public void setPaymentSourceID(java.lang.String paymentSourceID) {
        this.paymentSourceID = paymentSourceID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalPreapprovedPaymentReply)) return false;
        PayPalPreapprovedPaymentReply other = (PayPalPreapprovedPaymentReply) obj;
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
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              this.transactionType.equals(other.getTransactionType()))) &&
            ((this.feeAmount==null && other.getFeeAmount()==null) || 
             (this.feeAmount!=null &&
              this.feeAmount.equals(other.getFeeAmount()))) &&
            ((this.payerCountry==null && other.getPayerCountry()==null) || 
             (this.payerCountry!=null &&
              this.payerCountry.equals(other.getPayerCountry()))) &&
            ((this.pendingReason==null && other.getPendingReason()==null) || 
             (this.pendingReason!=null &&
              this.pendingReason.equals(other.getPendingReason()))) &&
            ((this.paymentStatus==null && other.getPaymentStatus()==null) || 
             (this.paymentStatus!=null &&
              this.paymentStatus.equals(other.getPaymentStatus()))) &&
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
            ((this.transactionID==null && other.getTransactionID()==null) || 
             (this.transactionID!=null &&
              this.transactionID.equals(other.getTransactionID()))) &&
            ((this.desc==null && other.getDesc()==null) || 
             (this.desc!=null &&
              this.desc.equals(other.getDesc()))) &&
            ((this.mpMax==null && other.getMpMax()==null) || 
             (this.mpMax!=null &&
              this.mpMax.equals(other.getMpMax()))) &&
            ((this.paymentType==null && other.getPaymentType()==null) || 
             (this.paymentType!=null &&
              this.paymentType.equals(other.getPaymentType()))) &&
            ((this.paymentDate==null && other.getPaymentDate()==null) || 
             (this.paymentDate!=null &&
              this.paymentDate.equals(other.getPaymentDate()))) &&
            ((this.paymentGrossAmount==null && other.getPaymentGrossAmount()==null) || 
             (this.paymentGrossAmount!=null &&
              this.paymentGrossAmount.equals(other.getPaymentGrossAmount()))) &&
            ((this.settleAmount==null && other.getSettleAmount()==null) || 
             (this.settleAmount!=null &&
              this.settleAmount.equals(other.getSettleAmount()))) &&
            ((this.taxAmount==null && other.getTaxAmount()==null) || 
             (this.taxAmount!=null &&
              this.taxAmount.equals(other.getTaxAmount()))) &&
            ((this.exchangeRate==null && other.getExchangeRate()==null) || 
             (this.exchangeRate!=null &&
              this.exchangeRate.equals(other.getExchangeRate()))) &&
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
        if (getTransactionType() != null) {
            _hashCode += getTransactionType().hashCode();
        }
        if (getFeeAmount() != null) {
            _hashCode += getFeeAmount().hashCode();
        }
        if (getPayerCountry() != null) {
            _hashCode += getPayerCountry().hashCode();
        }
        if (getPendingReason() != null) {
            _hashCode += getPendingReason().hashCode();
        }
        if (getPaymentStatus() != null) {
            _hashCode += getPaymentStatus().hashCode();
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
        if (getTransactionID() != null) {
            _hashCode += getTransactionID().hashCode();
        }
        if (getDesc() != null) {
            _hashCode += getDesc().hashCode();
        }
        if (getMpMax() != null) {
            _hashCode += getMpMax().hashCode();
        }
        if (getPaymentType() != null) {
            _hashCode += getPaymentType().hashCode();
        }
        if (getPaymentDate() != null) {
            _hashCode += getPaymentDate().hashCode();
        }
        if (getPaymentGrossAmount() != null) {
            _hashCode += getPaymentGrossAmount().hashCode();
        }
        if (getSettleAmount() != null) {
            _hashCode += getSettleAmount().hashCode();
        }
        if (getTaxAmount() != null) {
            _hashCode += getTaxAmount().hashCode();
        }
        if (getExchangeRate() != null) {
            _hashCode += getExchangeRate().hashCode();
        }
        if (getPaymentSourceID() != null) {
            _hashCode += getPaymentSourceID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalPreapprovedPaymentReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedPaymentReply"));
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
        elemField.setFieldName("transactionType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "transactionType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("feeAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "feeAmount"));
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
        elemField.setFieldName("pendingReason");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pendingReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentStatus"));
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
        elemField.setFieldName("transactionID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "transactionID"));
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
        elemField.setFieldName("paymentType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentGrossAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentGrossAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settleAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "settleAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "taxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangeRate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "exchangeRate"));
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
