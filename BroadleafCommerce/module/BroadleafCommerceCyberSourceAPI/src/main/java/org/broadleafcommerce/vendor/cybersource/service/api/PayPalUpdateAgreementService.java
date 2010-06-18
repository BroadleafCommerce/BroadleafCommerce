/**
 * PayPalUpdateAgreementService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class PayPalUpdateAgreementService  implements java.io.Serializable {
    private java.lang.String paypalBillingAgreementId;

    private java.lang.String paypalBillingAgreementStatus;

    private java.lang.String paypalBillingAgreementDesc;

    private java.lang.String paypalBillingAgreementCustom;

    private java.lang.String run;  // attribute

    public PayPalUpdateAgreementService() {
    }

    public PayPalUpdateAgreementService(
           java.lang.String paypalBillingAgreementId,
           java.lang.String paypalBillingAgreementStatus,
           java.lang.String paypalBillingAgreementDesc,
           java.lang.String paypalBillingAgreementCustom,
           java.lang.String run) {
           this.paypalBillingAgreementId = paypalBillingAgreementId;
           this.paypalBillingAgreementStatus = paypalBillingAgreementStatus;
           this.paypalBillingAgreementDesc = paypalBillingAgreementDesc;
           this.paypalBillingAgreementCustom = paypalBillingAgreementCustom;
           this.run = run;
    }


    /**
     * Gets the paypalBillingAgreementId value for this PayPalUpdateAgreementService.
     * 
     * @return paypalBillingAgreementId
     */
    public java.lang.String getPaypalBillingAgreementId() {
        return paypalBillingAgreementId;
    }


    /**
     * Sets the paypalBillingAgreementId value for this PayPalUpdateAgreementService.
     * 
     * @param paypalBillingAgreementId
     */
    public void setPaypalBillingAgreementId(java.lang.String paypalBillingAgreementId) {
        this.paypalBillingAgreementId = paypalBillingAgreementId;
    }


    /**
     * Gets the paypalBillingAgreementStatus value for this PayPalUpdateAgreementService.
     * 
     * @return paypalBillingAgreementStatus
     */
    public java.lang.String getPaypalBillingAgreementStatus() {
        return paypalBillingAgreementStatus;
    }


    /**
     * Sets the paypalBillingAgreementStatus value for this PayPalUpdateAgreementService.
     * 
     * @param paypalBillingAgreementStatus
     */
    public void setPaypalBillingAgreementStatus(java.lang.String paypalBillingAgreementStatus) {
        this.paypalBillingAgreementStatus = paypalBillingAgreementStatus;
    }


    /**
     * Gets the paypalBillingAgreementDesc value for this PayPalUpdateAgreementService.
     * 
     * @return paypalBillingAgreementDesc
     */
    public java.lang.String getPaypalBillingAgreementDesc() {
        return paypalBillingAgreementDesc;
    }


    /**
     * Sets the paypalBillingAgreementDesc value for this PayPalUpdateAgreementService.
     * 
     * @param paypalBillingAgreementDesc
     */
    public void setPaypalBillingAgreementDesc(java.lang.String paypalBillingAgreementDesc) {
        this.paypalBillingAgreementDesc = paypalBillingAgreementDesc;
    }


    /**
     * Gets the paypalBillingAgreementCustom value for this PayPalUpdateAgreementService.
     * 
     * @return paypalBillingAgreementCustom
     */
    public java.lang.String getPaypalBillingAgreementCustom() {
        return paypalBillingAgreementCustom;
    }


    /**
     * Sets the paypalBillingAgreementCustom value for this PayPalUpdateAgreementService.
     * 
     * @param paypalBillingAgreementCustom
     */
    public void setPaypalBillingAgreementCustom(java.lang.String paypalBillingAgreementCustom) {
        this.paypalBillingAgreementCustom = paypalBillingAgreementCustom;
    }


    /**
     * Gets the run value for this PayPalUpdateAgreementService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this PayPalUpdateAgreementService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PayPalUpdateAgreementService)) return false;
        PayPalUpdateAgreementService other = (PayPalUpdateAgreementService) obj;
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
            ((this.paypalBillingAgreementStatus==null && other.getPaypalBillingAgreementStatus()==null) || 
             (this.paypalBillingAgreementStatus!=null &&
              this.paypalBillingAgreementStatus.equals(other.getPaypalBillingAgreementStatus()))) &&
            ((this.paypalBillingAgreementDesc==null && other.getPaypalBillingAgreementDesc()==null) || 
             (this.paypalBillingAgreementDesc!=null &&
              this.paypalBillingAgreementDesc.equals(other.getPaypalBillingAgreementDesc()))) &&
            ((this.paypalBillingAgreementCustom==null && other.getPaypalBillingAgreementCustom()==null) || 
             (this.paypalBillingAgreementCustom!=null &&
              this.paypalBillingAgreementCustom.equals(other.getPaypalBillingAgreementCustom()))) &&
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
        if (getPaypalBillingAgreementStatus() != null) {
            _hashCode += getPaypalBillingAgreementStatus().hashCode();
        }
        if (getPaypalBillingAgreementDesc() != null) {
            _hashCode += getPaypalBillingAgreementDesc().hashCode();
        }
        if (getPaypalBillingAgreementCustom() != null) {
            _hashCode += getPaypalBillingAgreementCustom().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PayPalUpdateAgreementService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalUpdateAgreementService"));
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
        elemField.setFieldName("paypalBillingAgreementStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypalBillingAgreementStatus"));
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
