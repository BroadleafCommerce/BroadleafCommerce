/**
 * CCAutoAuthReversalService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCAutoAuthReversalService  implements java.io.Serializable {
    private java.lang.String authPaymentServiceData;

    private java.lang.String reconciliationID;

    private java.lang.String authAmount;

    private java.lang.String commerceIndicator;

    private java.lang.String authRequestID;

    private java.lang.String billAmount;

    private java.lang.String authCode;

    private java.lang.String authType;

    private java.lang.String billPayment;

    private java.lang.String dateAdded;

    private java.lang.String run;  // attribute

    public CCAutoAuthReversalService() {
    }

    public CCAutoAuthReversalService(
           java.lang.String authPaymentServiceData,
           java.lang.String reconciliationID,
           java.lang.String authAmount,
           java.lang.String commerceIndicator,
           java.lang.String authRequestID,
           java.lang.String billAmount,
           java.lang.String authCode,
           java.lang.String authType,
           java.lang.String billPayment,
           java.lang.String dateAdded,
           java.lang.String run) {
           this.authPaymentServiceData = authPaymentServiceData;
           this.reconciliationID = reconciliationID;
           this.authAmount = authAmount;
           this.commerceIndicator = commerceIndicator;
           this.authRequestID = authRequestID;
           this.billAmount = billAmount;
           this.authCode = authCode;
           this.authType = authType;
           this.billPayment = billPayment;
           this.dateAdded = dateAdded;
           this.run = run;
    }


    /**
     * Gets the authPaymentServiceData value for this CCAutoAuthReversalService.
     * 
     * @return authPaymentServiceData
     */
    public java.lang.String getAuthPaymentServiceData() {
        return authPaymentServiceData;
    }


    /**
     * Sets the authPaymentServiceData value for this CCAutoAuthReversalService.
     * 
     * @param authPaymentServiceData
     */
    public void setAuthPaymentServiceData(java.lang.String authPaymentServiceData) {
        this.authPaymentServiceData = authPaymentServiceData;
    }


    /**
     * Gets the reconciliationID value for this CCAutoAuthReversalService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this CCAutoAuthReversalService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the authAmount value for this CCAutoAuthReversalService.
     * 
     * @return authAmount
     */
    public java.lang.String getAuthAmount() {
        return authAmount;
    }


    /**
     * Sets the authAmount value for this CCAutoAuthReversalService.
     * 
     * @param authAmount
     */
    public void setAuthAmount(java.lang.String authAmount) {
        this.authAmount = authAmount;
    }


    /**
     * Gets the commerceIndicator value for this CCAutoAuthReversalService.
     * 
     * @return commerceIndicator
     */
    public java.lang.String getCommerceIndicator() {
        return commerceIndicator;
    }


    /**
     * Sets the commerceIndicator value for this CCAutoAuthReversalService.
     * 
     * @param commerceIndicator
     */
    public void setCommerceIndicator(java.lang.String commerceIndicator) {
        this.commerceIndicator = commerceIndicator;
    }


    /**
     * Gets the authRequestID value for this CCAutoAuthReversalService.
     * 
     * @return authRequestID
     */
    public java.lang.String getAuthRequestID() {
        return authRequestID;
    }


    /**
     * Sets the authRequestID value for this CCAutoAuthReversalService.
     * 
     * @param authRequestID
     */
    public void setAuthRequestID(java.lang.String authRequestID) {
        this.authRequestID = authRequestID;
    }


    /**
     * Gets the billAmount value for this CCAutoAuthReversalService.
     * 
     * @return billAmount
     */
    public java.lang.String getBillAmount() {
        return billAmount;
    }


    /**
     * Sets the billAmount value for this CCAutoAuthReversalService.
     * 
     * @param billAmount
     */
    public void setBillAmount(java.lang.String billAmount) {
        this.billAmount = billAmount;
    }


    /**
     * Gets the authCode value for this CCAutoAuthReversalService.
     * 
     * @return authCode
     */
    public java.lang.String getAuthCode() {
        return authCode;
    }


    /**
     * Sets the authCode value for this CCAutoAuthReversalService.
     * 
     * @param authCode
     */
    public void setAuthCode(java.lang.String authCode) {
        this.authCode = authCode;
    }


    /**
     * Gets the authType value for this CCAutoAuthReversalService.
     * 
     * @return authType
     */
    public java.lang.String getAuthType() {
        return authType;
    }


    /**
     * Sets the authType value for this CCAutoAuthReversalService.
     * 
     * @param authType
     */
    public void setAuthType(java.lang.String authType) {
        this.authType = authType;
    }


    /**
     * Gets the billPayment value for this CCAutoAuthReversalService.
     * 
     * @return billPayment
     */
    public java.lang.String getBillPayment() {
        return billPayment;
    }


    /**
     * Sets the billPayment value for this CCAutoAuthReversalService.
     * 
     * @param billPayment
     */
    public void setBillPayment(java.lang.String billPayment) {
        this.billPayment = billPayment;
    }


    /**
     * Gets the dateAdded value for this CCAutoAuthReversalService.
     * 
     * @return dateAdded
     */
    public java.lang.String getDateAdded() {
        return dateAdded;
    }


    /**
     * Sets the dateAdded value for this CCAutoAuthReversalService.
     * 
     * @param dateAdded
     */
    public void setDateAdded(java.lang.String dateAdded) {
        this.dateAdded = dateAdded;
    }


    /**
     * Gets the run value for this CCAutoAuthReversalService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this CCAutoAuthReversalService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCAutoAuthReversalService)) return false;
        CCAutoAuthReversalService other = (CCAutoAuthReversalService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authPaymentServiceData==null && other.getAuthPaymentServiceData()==null) || 
             (this.authPaymentServiceData!=null &&
              this.authPaymentServiceData.equals(other.getAuthPaymentServiceData()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.authAmount==null && other.getAuthAmount()==null) || 
             (this.authAmount!=null &&
              this.authAmount.equals(other.getAuthAmount()))) &&
            ((this.commerceIndicator==null && other.getCommerceIndicator()==null) || 
             (this.commerceIndicator!=null &&
              this.commerceIndicator.equals(other.getCommerceIndicator()))) &&
            ((this.authRequestID==null && other.getAuthRequestID()==null) || 
             (this.authRequestID!=null &&
              this.authRequestID.equals(other.getAuthRequestID()))) &&
            ((this.billAmount==null && other.getBillAmount()==null) || 
             (this.billAmount!=null &&
              this.billAmount.equals(other.getBillAmount()))) &&
            ((this.authCode==null && other.getAuthCode()==null) || 
             (this.authCode!=null &&
              this.authCode.equals(other.getAuthCode()))) &&
            ((this.authType==null && other.getAuthType()==null) || 
             (this.authType!=null &&
              this.authType.equals(other.getAuthType()))) &&
            ((this.billPayment==null && other.getBillPayment()==null) || 
             (this.billPayment!=null &&
              this.billPayment.equals(other.getBillPayment()))) &&
            ((this.dateAdded==null && other.getDateAdded()==null) || 
             (this.dateAdded!=null &&
              this.dateAdded.equals(other.getDateAdded()))) &&
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
        if (getAuthPaymentServiceData() != null) {
            _hashCode += getAuthPaymentServiceData().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getAuthAmount() != null) {
            _hashCode += getAuthAmount().hashCode();
        }
        if (getCommerceIndicator() != null) {
            _hashCode += getCommerceIndicator().hashCode();
        }
        if (getAuthRequestID() != null) {
            _hashCode += getAuthRequestID().hashCode();
        }
        if (getBillAmount() != null) {
            _hashCode += getBillAmount().hashCode();
        }
        if (getAuthCode() != null) {
            _hashCode += getAuthCode().hashCode();
        }
        if (getAuthType() != null) {
            _hashCode += getAuthType().hashCode();
        }
        if (getBillPayment() != null) {
            _hashCode += getBillPayment().hashCode();
        }
        if (getDateAdded() != null) {
            _hashCode += getDateAdded().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCAutoAuthReversalService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAutoAuthReversalService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authPaymentServiceData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authPaymentServiceData"));
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
        elemField.setFieldName("authAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commerceIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "commerceIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "billAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "billPayment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateAdded");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "dateAdded"));
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
