/**
 * ECCreditService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ECCreditService  implements java.io.Serializable {
    private java.lang.String referenceNumber;

    private java.lang.String settlementMethod;

    private java.lang.String transactionToken;

    private java.lang.String debitRequestID;

    private java.lang.String partialPaymentID;

    private java.lang.String commerceIndicator;

    private java.lang.String debitRequestToken;

    private java.lang.String run;  // attribute

    public ECCreditService() {
    }

    public ECCreditService(
           java.lang.String referenceNumber,
           java.lang.String settlementMethod,
           java.lang.String transactionToken,
           java.lang.String debitRequestID,
           java.lang.String partialPaymentID,
           java.lang.String commerceIndicator,
           java.lang.String debitRequestToken,
           java.lang.String run) {
           this.referenceNumber = referenceNumber;
           this.settlementMethod = settlementMethod;
           this.transactionToken = transactionToken;
           this.debitRequestID = debitRequestID;
           this.partialPaymentID = partialPaymentID;
           this.commerceIndicator = commerceIndicator;
           this.debitRequestToken = debitRequestToken;
           this.run = run;
    }


    /**
     * Gets the referenceNumber value for this ECCreditService.
     * 
     * @return referenceNumber
     */
    public java.lang.String getReferenceNumber() {
        return referenceNumber;
    }


    /**
     * Sets the referenceNumber value for this ECCreditService.
     * 
     * @param referenceNumber
     */
    public void setReferenceNumber(java.lang.String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }


    /**
     * Gets the settlementMethod value for this ECCreditService.
     * 
     * @return settlementMethod
     */
    public java.lang.String getSettlementMethod() {
        return settlementMethod;
    }


    /**
     * Sets the settlementMethod value for this ECCreditService.
     * 
     * @param settlementMethod
     */
    public void setSettlementMethod(java.lang.String settlementMethod) {
        this.settlementMethod = settlementMethod;
    }


    /**
     * Gets the transactionToken value for this ECCreditService.
     * 
     * @return transactionToken
     */
    public java.lang.String getTransactionToken() {
        return transactionToken;
    }


    /**
     * Sets the transactionToken value for this ECCreditService.
     * 
     * @param transactionToken
     */
    public void setTransactionToken(java.lang.String transactionToken) {
        this.transactionToken = transactionToken;
    }


    /**
     * Gets the debitRequestID value for this ECCreditService.
     * 
     * @return debitRequestID
     */
    public java.lang.String getDebitRequestID() {
        return debitRequestID;
    }


    /**
     * Sets the debitRequestID value for this ECCreditService.
     * 
     * @param debitRequestID
     */
    public void setDebitRequestID(java.lang.String debitRequestID) {
        this.debitRequestID = debitRequestID;
    }


    /**
     * Gets the partialPaymentID value for this ECCreditService.
     * 
     * @return partialPaymentID
     */
    public java.lang.String getPartialPaymentID() {
        return partialPaymentID;
    }


    /**
     * Sets the partialPaymentID value for this ECCreditService.
     * 
     * @param partialPaymentID
     */
    public void setPartialPaymentID(java.lang.String partialPaymentID) {
        this.partialPaymentID = partialPaymentID;
    }


    /**
     * Gets the commerceIndicator value for this ECCreditService.
     * 
     * @return commerceIndicator
     */
    public java.lang.String getCommerceIndicator() {
        return commerceIndicator;
    }


    /**
     * Sets the commerceIndicator value for this ECCreditService.
     * 
     * @param commerceIndicator
     */
    public void setCommerceIndicator(java.lang.String commerceIndicator) {
        this.commerceIndicator = commerceIndicator;
    }


    /**
     * Gets the debitRequestToken value for this ECCreditService.
     * 
     * @return debitRequestToken
     */
    public java.lang.String getDebitRequestToken() {
        return debitRequestToken;
    }


    /**
     * Sets the debitRequestToken value for this ECCreditService.
     * 
     * @param debitRequestToken
     */
    public void setDebitRequestToken(java.lang.String debitRequestToken) {
        this.debitRequestToken = debitRequestToken;
    }


    /**
     * Gets the run value for this ECCreditService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this ECCreditService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ECCreditService)) return false;
        ECCreditService other = (ECCreditService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.referenceNumber==null && other.getReferenceNumber()==null) || 
             (this.referenceNumber!=null &&
              this.referenceNumber.equals(other.getReferenceNumber()))) &&
            ((this.settlementMethod==null && other.getSettlementMethod()==null) || 
             (this.settlementMethod!=null &&
              this.settlementMethod.equals(other.getSettlementMethod()))) &&
            ((this.transactionToken==null && other.getTransactionToken()==null) || 
             (this.transactionToken!=null &&
              this.transactionToken.equals(other.getTransactionToken()))) &&
            ((this.debitRequestID==null && other.getDebitRequestID()==null) || 
             (this.debitRequestID!=null &&
              this.debitRequestID.equals(other.getDebitRequestID()))) &&
            ((this.partialPaymentID==null && other.getPartialPaymentID()==null) || 
             (this.partialPaymentID!=null &&
              this.partialPaymentID.equals(other.getPartialPaymentID()))) &&
            ((this.commerceIndicator==null && other.getCommerceIndicator()==null) || 
             (this.commerceIndicator!=null &&
              this.commerceIndicator.equals(other.getCommerceIndicator()))) &&
            ((this.debitRequestToken==null && other.getDebitRequestToken()==null) || 
             (this.debitRequestToken!=null &&
              this.debitRequestToken.equals(other.getDebitRequestToken()))) &&
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
        if (getReferenceNumber() != null) {
            _hashCode += getReferenceNumber().hashCode();
        }
        if (getSettlementMethod() != null) {
            _hashCode += getSettlementMethod().hashCode();
        }
        if (getTransactionToken() != null) {
            _hashCode += getTransactionToken().hashCode();
        }
        if (getDebitRequestID() != null) {
            _hashCode += getDebitRequestID().hashCode();
        }
        if (getPartialPaymentID() != null) {
            _hashCode += getPartialPaymentID().hashCode();
        }
        if (getCommerceIndicator() != null) {
            _hashCode += getCommerceIndicator().hashCode();
        }
        if (getDebitRequestToken() != null) {
            _hashCode += getDebitRequestToken().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ECCreditService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECCreditService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("referenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "referenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("settlementMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "settlementMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "transactionToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("debitRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "debitRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("partialPaymentID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "partialPaymentID"));
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
        elemField.setFieldName("debitRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "debitRequestToken"));
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
