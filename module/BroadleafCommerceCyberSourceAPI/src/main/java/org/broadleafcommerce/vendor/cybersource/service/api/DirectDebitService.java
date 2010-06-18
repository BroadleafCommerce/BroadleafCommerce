/**
 * DirectDebitService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class DirectDebitService  implements java.io.Serializable {
    private java.lang.String dateCollect;

    private java.lang.String directDebitText;

    private java.lang.String authorizationID;

    private java.lang.String transactionType;

    private java.lang.String directDebitType;

    private java.lang.String validateRequestID;

    private java.lang.String recurringType;

    private java.lang.String mandateID;

    private java.lang.String validateRequestToken;

    private java.lang.String reconciliationID;

    private java.lang.String run;  // attribute

    public DirectDebitService() {
    }

    public DirectDebitService(
           java.lang.String dateCollect,
           java.lang.String directDebitText,
           java.lang.String authorizationID,
           java.lang.String transactionType,
           java.lang.String directDebitType,
           java.lang.String validateRequestID,
           java.lang.String recurringType,
           java.lang.String mandateID,
           java.lang.String validateRequestToken,
           java.lang.String reconciliationID,
           java.lang.String run) {
           this.dateCollect = dateCollect;
           this.directDebitText = directDebitText;
           this.authorizationID = authorizationID;
           this.transactionType = transactionType;
           this.directDebitType = directDebitType;
           this.validateRequestID = validateRequestID;
           this.recurringType = recurringType;
           this.mandateID = mandateID;
           this.validateRequestToken = validateRequestToken;
           this.reconciliationID = reconciliationID;
           this.run = run;
    }


    /**
     * Gets the dateCollect value for this DirectDebitService.
     * 
     * @return dateCollect
     */
    public java.lang.String getDateCollect() {
        return dateCollect;
    }


    /**
     * Sets the dateCollect value for this DirectDebitService.
     * 
     * @param dateCollect
     */
    public void setDateCollect(java.lang.String dateCollect) {
        this.dateCollect = dateCollect;
    }


    /**
     * Gets the directDebitText value for this DirectDebitService.
     * 
     * @return directDebitText
     */
    public java.lang.String getDirectDebitText() {
        return directDebitText;
    }


    /**
     * Sets the directDebitText value for this DirectDebitService.
     * 
     * @param directDebitText
     */
    public void setDirectDebitText(java.lang.String directDebitText) {
        this.directDebitText = directDebitText;
    }


    /**
     * Gets the authorizationID value for this DirectDebitService.
     * 
     * @return authorizationID
     */
    public java.lang.String getAuthorizationID() {
        return authorizationID;
    }


    /**
     * Sets the authorizationID value for this DirectDebitService.
     * 
     * @param authorizationID
     */
    public void setAuthorizationID(java.lang.String authorizationID) {
        this.authorizationID = authorizationID;
    }


    /**
     * Gets the transactionType value for this DirectDebitService.
     * 
     * @return transactionType
     */
    public java.lang.String getTransactionType() {
        return transactionType;
    }


    /**
     * Sets the transactionType value for this DirectDebitService.
     * 
     * @param transactionType
     */
    public void setTransactionType(java.lang.String transactionType) {
        this.transactionType = transactionType;
    }


    /**
     * Gets the directDebitType value for this DirectDebitService.
     * 
     * @return directDebitType
     */
    public java.lang.String getDirectDebitType() {
        return directDebitType;
    }


    /**
     * Sets the directDebitType value for this DirectDebitService.
     * 
     * @param directDebitType
     */
    public void setDirectDebitType(java.lang.String directDebitType) {
        this.directDebitType = directDebitType;
    }


    /**
     * Gets the validateRequestID value for this DirectDebitService.
     * 
     * @return validateRequestID
     */
    public java.lang.String getValidateRequestID() {
        return validateRequestID;
    }


    /**
     * Sets the validateRequestID value for this DirectDebitService.
     * 
     * @param validateRequestID
     */
    public void setValidateRequestID(java.lang.String validateRequestID) {
        this.validateRequestID = validateRequestID;
    }


    /**
     * Gets the recurringType value for this DirectDebitService.
     * 
     * @return recurringType
     */
    public java.lang.String getRecurringType() {
        return recurringType;
    }


    /**
     * Sets the recurringType value for this DirectDebitService.
     * 
     * @param recurringType
     */
    public void setRecurringType(java.lang.String recurringType) {
        this.recurringType = recurringType;
    }


    /**
     * Gets the mandateID value for this DirectDebitService.
     * 
     * @return mandateID
     */
    public java.lang.String getMandateID() {
        return mandateID;
    }


    /**
     * Sets the mandateID value for this DirectDebitService.
     * 
     * @param mandateID
     */
    public void setMandateID(java.lang.String mandateID) {
        this.mandateID = mandateID;
    }


    /**
     * Gets the validateRequestToken value for this DirectDebitService.
     * 
     * @return validateRequestToken
     */
    public java.lang.String getValidateRequestToken() {
        return validateRequestToken;
    }


    /**
     * Sets the validateRequestToken value for this DirectDebitService.
     * 
     * @param validateRequestToken
     */
    public void setValidateRequestToken(java.lang.String validateRequestToken) {
        this.validateRequestToken = validateRequestToken;
    }


    /**
     * Gets the reconciliationID value for this DirectDebitService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this DirectDebitService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the run value for this DirectDebitService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this DirectDebitService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DirectDebitService)) return false;
        DirectDebitService other = (DirectDebitService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.dateCollect==null && other.getDateCollect()==null) || 
             (this.dateCollect!=null &&
              this.dateCollect.equals(other.getDateCollect()))) &&
            ((this.directDebitText==null && other.getDirectDebitText()==null) || 
             (this.directDebitText!=null &&
              this.directDebitText.equals(other.getDirectDebitText()))) &&
            ((this.authorizationID==null && other.getAuthorizationID()==null) || 
             (this.authorizationID!=null &&
              this.authorizationID.equals(other.getAuthorizationID()))) &&
            ((this.transactionType==null && other.getTransactionType()==null) || 
             (this.transactionType!=null &&
              this.transactionType.equals(other.getTransactionType()))) &&
            ((this.directDebitType==null && other.getDirectDebitType()==null) || 
             (this.directDebitType!=null &&
              this.directDebitType.equals(other.getDirectDebitType()))) &&
            ((this.validateRequestID==null && other.getValidateRequestID()==null) || 
             (this.validateRequestID!=null &&
              this.validateRequestID.equals(other.getValidateRequestID()))) &&
            ((this.recurringType==null && other.getRecurringType()==null) || 
             (this.recurringType!=null &&
              this.recurringType.equals(other.getRecurringType()))) &&
            ((this.mandateID==null && other.getMandateID()==null) || 
             (this.mandateID!=null &&
              this.mandateID.equals(other.getMandateID()))) &&
            ((this.validateRequestToken==null && other.getValidateRequestToken()==null) || 
             (this.validateRequestToken!=null &&
              this.validateRequestToken.equals(other.getValidateRequestToken()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
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
        if (getDateCollect() != null) {
            _hashCode += getDateCollect().hashCode();
        }
        if (getDirectDebitText() != null) {
            _hashCode += getDirectDebitText().hashCode();
        }
        if (getAuthorizationID() != null) {
            _hashCode += getAuthorizationID().hashCode();
        }
        if (getTransactionType() != null) {
            _hashCode += getTransactionType().hashCode();
        }
        if (getDirectDebitType() != null) {
            _hashCode += getDirectDebitType().hashCode();
        }
        if (getValidateRequestID() != null) {
            _hashCode += getValidateRequestID().hashCode();
        }
        if (getRecurringType() != null) {
            _hashCode += getRecurringType().hashCode();
        }
        if (getMandateID() != null) {
            _hashCode += getMandateID().hashCode();
        }
        if (getValidateRequestToken() != null) {
            _hashCode += getValidateRequestToken().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DirectDebitService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateCollect");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "dateCollect"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitText");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationID"));
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
        elemField.setFieldName("directDebitType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validateRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "validateRequestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recurringType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "recurringType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mandateID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "mandateID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("validateRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "validateRequestToken"));
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
