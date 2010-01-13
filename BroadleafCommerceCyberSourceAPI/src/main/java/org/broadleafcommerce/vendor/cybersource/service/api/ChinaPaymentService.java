/**
 * ChinaPaymentService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ChinaPaymentService  implements java.io.Serializable {
    private java.lang.String paymentMode;

    private java.lang.String returnURL;

    private java.lang.String pickUpAddress;

    private java.lang.String pickUpPhoneNumber;

    private java.lang.String pickUpPostalCode;

    private java.lang.String pickUpName;

    private java.lang.String run;  // attribute

    public ChinaPaymentService() {
    }

    public ChinaPaymentService(
           java.lang.String paymentMode,
           java.lang.String returnURL,
           java.lang.String pickUpAddress,
           java.lang.String pickUpPhoneNumber,
           java.lang.String pickUpPostalCode,
           java.lang.String pickUpName,
           java.lang.String run) {
           this.paymentMode = paymentMode;
           this.returnURL = returnURL;
           this.pickUpAddress = pickUpAddress;
           this.pickUpPhoneNumber = pickUpPhoneNumber;
           this.pickUpPostalCode = pickUpPostalCode;
           this.pickUpName = pickUpName;
           this.run = run;
    }


    /**
     * Gets the paymentMode value for this ChinaPaymentService.
     * 
     * @return paymentMode
     */
    public java.lang.String getPaymentMode() {
        return paymentMode;
    }


    /**
     * Sets the paymentMode value for this ChinaPaymentService.
     * 
     * @param paymentMode
     */
    public void setPaymentMode(java.lang.String paymentMode) {
        this.paymentMode = paymentMode;
    }


    /**
     * Gets the returnURL value for this ChinaPaymentService.
     * 
     * @return returnURL
     */
    public java.lang.String getReturnURL() {
        return returnURL;
    }


    /**
     * Sets the returnURL value for this ChinaPaymentService.
     * 
     * @param returnURL
     */
    public void setReturnURL(java.lang.String returnURL) {
        this.returnURL = returnURL;
    }


    /**
     * Gets the pickUpAddress value for this ChinaPaymentService.
     * 
     * @return pickUpAddress
     */
    public java.lang.String getPickUpAddress() {
        return pickUpAddress;
    }


    /**
     * Sets the pickUpAddress value for this ChinaPaymentService.
     * 
     * @param pickUpAddress
     */
    public void setPickUpAddress(java.lang.String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }


    /**
     * Gets the pickUpPhoneNumber value for this ChinaPaymentService.
     * 
     * @return pickUpPhoneNumber
     */
    public java.lang.String getPickUpPhoneNumber() {
        return pickUpPhoneNumber;
    }


    /**
     * Sets the pickUpPhoneNumber value for this ChinaPaymentService.
     * 
     * @param pickUpPhoneNumber
     */
    public void setPickUpPhoneNumber(java.lang.String pickUpPhoneNumber) {
        this.pickUpPhoneNumber = pickUpPhoneNumber;
    }


    /**
     * Gets the pickUpPostalCode value for this ChinaPaymentService.
     * 
     * @return pickUpPostalCode
     */
    public java.lang.String getPickUpPostalCode() {
        return pickUpPostalCode;
    }


    /**
     * Sets the pickUpPostalCode value for this ChinaPaymentService.
     * 
     * @param pickUpPostalCode
     */
    public void setPickUpPostalCode(java.lang.String pickUpPostalCode) {
        this.pickUpPostalCode = pickUpPostalCode;
    }


    /**
     * Gets the pickUpName value for this ChinaPaymentService.
     * 
     * @return pickUpName
     */
    public java.lang.String getPickUpName() {
        return pickUpName;
    }


    /**
     * Sets the pickUpName value for this ChinaPaymentService.
     * 
     * @param pickUpName
     */
    public void setPickUpName(java.lang.String pickUpName) {
        this.pickUpName = pickUpName;
    }


    /**
     * Gets the run value for this ChinaPaymentService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this ChinaPaymentService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ChinaPaymentService)) return false;
        ChinaPaymentService other = (ChinaPaymentService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paymentMode==null && other.getPaymentMode()==null) || 
             (this.paymentMode!=null &&
              this.paymentMode.equals(other.getPaymentMode()))) &&
            ((this.returnURL==null && other.getReturnURL()==null) || 
             (this.returnURL!=null &&
              this.returnURL.equals(other.getReturnURL()))) &&
            ((this.pickUpAddress==null && other.getPickUpAddress()==null) || 
             (this.pickUpAddress!=null &&
              this.pickUpAddress.equals(other.getPickUpAddress()))) &&
            ((this.pickUpPhoneNumber==null && other.getPickUpPhoneNumber()==null) || 
             (this.pickUpPhoneNumber!=null &&
              this.pickUpPhoneNumber.equals(other.getPickUpPhoneNumber()))) &&
            ((this.pickUpPostalCode==null && other.getPickUpPostalCode()==null) || 
             (this.pickUpPostalCode!=null &&
              this.pickUpPostalCode.equals(other.getPickUpPostalCode()))) &&
            ((this.pickUpName==null && other.getPickUpName()==null) || 
             (this.pickUpName!=null &&
              this.pickUpName.equals(other.getPickUpName()))) &&
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
        if (getPaymentMode() != null) {
            _hashCode += getPaymentMode().hashCode();
        }
        if (getReturnURL() != null) {
            _hashCode += getReturnURL().hashCode();
        }
        if (getPickUpAddress() != null) {
            _hashCode += getPickUpAddress().hashCode();
        }
        if (getPickUpPhoneNumber() != null) {
            _hashCode += getPickUpPhoneNumber().hashCode();
        }
        if (getPickUpPostalCode() != null) {
            _hashCode += getPickUpPostalCode().hashCode();
        }
        if (getPickUpName() != null) {
            _hashCode += getPickUpName().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ChinaPaymentService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaPaymentService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentMode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnURL");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "returnURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pickUpAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pickUpAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pickUpPhoneNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pickUpPhoneNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pickUpPostalCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pickUpPostalCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pickUpName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pickUpName"));
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
