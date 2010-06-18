/**
 * PayPalDoRefTransactionService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalDoRefTransactionService  implements java.io.Serializable {
    private java.lang.String paypalBillingAgreementId;

    private java.lang.String paypalPaymentType;

    private java.lang.String paypalReqconfirmshipping;

    private java.lang.String paypalReturnFmfDetails;

    private java.lang.String paypalSoftDescriptor;

    private java.lang.String paypalShippingdiscount;

    private java.lang.String paypalDesc;

    private java.lang.String invoiceNumber;

    private java.lang.String paypalEcNotifyUrl;

    private java.lang.String run;  // attribute

    public PayPalDoRefTransactionService() {
    }

    public PayPalDoRefTransactionService(
           java.lang.String paypalBillingAgreementId,
           java.lang.String paypalPaymentType,
           java.lang.String paypalReqconfirmshipping,
           java.lang.String paypalReturnFmfDetails,
           java.lang.String paypalSoftDescriptor,
           java.lang.String paypalShippingdiscount,
           java.lang.String paypalDesc,
           java.lang.String invoiceNumber,
           java.lang.String paypalEcNotifyUrl,
           java.lang.String run) {
           this.paypalBillingAgreementId = paypalBillingAgreementId;
           this.paypalPaymentType = paypalPaymentType;
           this.paypalReqconfirmshipping = paypalReqconfirmshipping;
           this.paypalReturnFmfDetails = paypalReturnFmfDetails;
           this.paypalSoftDescriptor = paypalSoftDescriptor;
           this.paypalShippingdiscount = paypalShippingdiscount;
           this.paypalDesc = paypalDesc;
           this.invoiceNumber = invoiceNumber;
           this.paypalEcNotifyUrl = paypalEcNotifyUrl;
           this.run = run;
    }


    /**
     * Gets the paypalBillingAgreementId value for this PayPalDoRefTransactionService.
     * 
     * @return paypalBillingAgreementId
     */
    public java.lang.String getPaypalBillingAgreementId() {
        return paypalBillingAgreementId;
    }


    /**
     * Sets the paypalBillingAgreementId value for this PayPalDoRefTransactionService.
     * 
     * @param paypalBillingAgreementId
     */
    public void setPaypalBillingAgreementId(java.lang.String paypalBillingAgreementId) {
        this.paypalBillingAgreementId = paypalBillingAgreementId;
    }


    /**
     * Gets the paypalPaymentType value for this PayPalDoRefTransactionService.
     * 
     * @return paypalPaymentType
     */
    public java.lang.String getPaypalPaymentType() {
        return paypalPaymentType;
    }


    /**
     * Sets the paypalPaymentType value for this PayPalDoRefTransactionService.
     * 
     * @param paypalPaymentType
     */
    public void setPaypalPaymentType(java.lang.String paypalPaymentType) {
        this.paypalPaymentType = paypalPaymentType;
    }


    /**
     * Gets the paypalReqconfirmshipping value for this PayPalDoRefTransactionService.
     * 
     * @return paypalReqconfirmshipping
     */
    public java.lang.String getPaypalReqconfirmshipping() {
        return paypalReqconfirmshipping;
    }


    /**
     * Sets the paypalReqconfirmshipping value for this PayPalDoRefTransactionService.
     * 
     * @param paypalReqconfirmshipping
     */
    public void setPaypalReqconfirmshipping(java.lang.String paypalReqconfirmshipping) {
        this.paypalReqconfirmshipping = paypalReqconfirmshipping;
    }


    /**
     * Gets the paypalReturnFmfDetails value for this PayPalDoRefTransactionService.
     * 
     * @return paypalReturnFmfDetails
     */
    public java.lang.String getPaypalReturnFmfDetails() {
        return paypalReturnFmfDetails;
    }


    /**
     * Sets the paypalReturnFmfDetails value for this PayPalDoRefTransactionService.
     * 
     * @param paypalReturnFmfDetails
     */
    public void setPaypalReturnFmfDetails(java.lang.String paypalReturnFmfDetails) {
        this.paypalReturnFmfDetails = paypalReturnFmfDetails;
    }


    /**
     * Gets the paypalSoftDescriptor value for this PayPalDoRefTransactionService.
     * 
     * @return paypalSoftDescriptor
     */
    public java.lang.String getPaypalSoftDescriptor() {
        return paypalSoftDescriptor;
    }


    /**
     * Sets the paypalSoftDescriptor value for this PayPalDoRefTransactionService.
     * 
     * @param paypalSoftDescriptor
     */
    public void setPaypalSoftDescriptor(java.lang.String paypalSoftDescriptor) {
        this.paypalSoftDescriptor = paypalSoftDescriptor;
    }


    /**
     * Gets the paypalShippingdiscount value for this PayPalDoRefTransactionService.
     * 
     * @return paypalShippingdiscount
     */
    public java.lang.String getPaypalShippingdiscount() {
        return paypalShippingdiscount;
    }


    /**
     * Sets the paypalShippingdiscount value for this PayPalDoRefTransactionService.
     * 
     * @param paypalShippingdiscount
     */
    public void setPaypalShippingdiscount(java.lang.String paypalShippingdiscount) {
        this.paypalShippingdiscount = paypalShippingdiscount;
    }


    /**
     * Gets the paypalDesc value for this PayPalDoRefTransactionService.
     * 
     * @return paypalDesc
     */
    public java.lang.String getPaypalDesc() {
        return paypalDesc;
    }


    /**
     * Sets the paypalDesc value for this PayPalDoRefTransactionService.
     * 
     * @param paypalDesc
     */
    public void setPaypalDesc(java.lang.String paypalDesc) {
        this.paypalDesc = paypalDesc;
    }


    /**
     * Gets the invoiceNumber value for this PayPalDoRefTransactionService.
     * 
     * @return invoiceNumber
     */
    public java.lang.String getInvoiceNumber() {
        return invoiceNumber;
    }


    /**
     * Sets the invoiceNumber value for this PayPalDoRefTransactionService.
     * 
     * @param invoiceNumber
     */
    public void setInvoiceNumber(java.lang.String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    /**
     * Gets the paypalEcNotifyUrl value for this PayPalDoRefTransactionService.
     * 
     * @return paypalEcNotifyUrl
     */
    public java.lang.String getPaypalEcNotifyUrl() {
        return paypalEcNotifyUrl;
    }


    /**
     * Sets the paypalEcNotifyUrl value for this PayPalDoRefTransactionService.
     * 
     * @param paypalEcNotifyUrl
     */
    public void setPaypalEcNotifyUrl(java.lang.String paypalEcNotifyUrl) {
        this.paypalEcNotifyUrl = paypalEcNotifyUrl;
    }


    /**
     * Gets the run value for this PayPalDoRefTransactionService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalDoRefTransactionService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalDoRefTransactionService)) return false;
        PayPalDoRefTransactionService other = (PayPalDoRefTransactionService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paypalBillingAgreementId==null && other.getPaypalBillingAgreementId()==null) || 
             (this.paypalBillingAgreementId!=null &&
              this.paypalBillingAgreementId.equals(other.getPaypalBillingAgreementId()))) &&
            ((this.paypalPaymentType==null && other.getPaypalPaymentType()==null) || 
             (this.paypalPaymentType!=null &&
              this.paypalPaymentType.equals(other.getPaypalPaymentType()))) &&
            ((this.paypalReqconfirmshipping==null && other.getPaypalReqconfirmshipping()==null) || 
             (this.paypalReqconfirmshipping!=null &&
              this.paypalReqconfirmshipping.equals(other.getPaypalReqconfirmshipping()))) &&
            ((this.paypalReturnFmfDetails==null && other.getPaypalReturnFmfDetails()==null) || 
             (this.paypalReturnFmfDetails!=null &&
              this.paypalReturnFmfDetails.equals(other.getPaypalReturnFmfDetails()))) &&
            ((this.paypalSoftDescriptor==null && other.getPaypalSoftDescriptor()==null) || 
             (this.paypalSoftDescriptor!=null &&
              this.paypalSoftDescriptor.equals(other.getPaypalSoftDescriptor()))) &&
            ((this.paypalShippingdiscount==null && other.getPaypalShippingdiscount()==null) || 
             (this.paypalShippingdiscount!=null &&
              this.paypalShippingdiscount.equals(other.getPaypalShippingdiscount()))) &&
            ((this.paypalDesc==null && other.getPaypalDesc()==null) || 
             (this.paypalDesc!=null &&
              this.paypalDesc.equals(other.getPaypalDesc()))) &&
            ((this.invoiceNumber==null && other.getInvoiceNumber()==null) || 
             (this.invoiceNumber!=null &&
              this.invoiceNumber.equals(other.getInvoiceNumber()))) &&
            ((this.paypalEcNotifyUrl==null && other.getPaypalEcNotifyUrl()==null) || 
             (this.paypalEcNotifyUrl!=null &&
              this.paypalEcNotifyUrl.equals(other.getPaypalEcNotifyUrl()))) &&
            ((this.run==null && other.getRun()==null) || 
             (this.run!=null &&
              this.run.equals(other.getRun())));
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
        if (getPaypalBillingAgreementId() != null) {
            _hashCode += getPaypalBillingAgreementId().hashCode();
        }
        if (getPaypalPaymentType() != null) {
            _hashCode += getPaypalPaymentType().hashCode();
        }
        if (getPaypalReqconfirmshipping() != null) {
            _hashCode += getPaypalReqconfirmshipping().hashCode();
        }
        if (getPaypalReturnFmfDetails() != null) {
            _hashCode += getPaypalReturnFmfDetails().hashCode();
        }
        if (getPaypalSoftDescriptor() != null) {
            _hashCode += getPaypalSoftDescriptor().hashCode();
        }
        if (getPaypalShippingdiscount() != null) {
            _hashCode += getPaypalShippingdiscount().hashCode();
        }
        if (getPaypalDesc() != null) {
            _hashCode += getPaypalDesc().hashCode();
        }
        if (getInvoiceNumber() != null) {
            _hashCode += getInvoiceNumber().hashCode();
        }
        if (getPaypalEcNotifyUrl() != null) {
            _hashCode += getPaypalEcNotifyUrl().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalDoRefTransactionService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoRefTransactionService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalBillingAgreementId");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalPaymentType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalPaymentType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalReqconfirmshipping");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalReqconfirmshipping"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalReturnFmfDetails");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalReturnFmfDetails"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalSoftDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalSoftDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalShippingdiscount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalShippingdiscount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalDesc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalDesc"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invoiceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "invoiceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypalEcNotifyUrl");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalEcNotifyUrl"));
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
