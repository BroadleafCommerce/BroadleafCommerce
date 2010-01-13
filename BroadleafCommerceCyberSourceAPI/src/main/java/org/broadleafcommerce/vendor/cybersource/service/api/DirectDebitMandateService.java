/**
 * DirectDebitMandateService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class DirectDebitMandateService  implements java.io.Serializable {
    private java.lang.String mandateDescriptor;

    private java.lang.String firstDebitDate;

    private java.lang.String run;  // attribute

    public DirectDebitMandateService() {
    }

    public DirectDebitMandateService(
           java.lang.String mandateDescriptor,
           java.lang.String firstDebitDate,
           java.lang.String run) {
           this.mandateDescriptor = mandateDescriptor;
           this.firstDebitDate = firstDebitDate;
           this.run = run;
    }


    /**
     * Gets the mandateDescriptor value for this DirectDebitMandateService.
     * 
     * @return mandateDescriptor
     */
    public java.lang.String getMandateDescriptor() {
        return mandateDescriptor;
    }


    /**
     * Sets the mandateDescriptor value for this DirectDebitMandateService.
     * 
     * @param mandateDescriptor
     */
    public void setMandateDescriptor(java.lang.String mandateDescriptor) {
        this.mandateDescriptor = mandateDescriptor;
    }


    /**
     * Gets the firstDebitDate value for this DirectDebitMandateService.
     * 
     * @return firstDebitDate
     */
    public java.lang.String getFirstDebitDate() {
        return firstDebitDate;
    }


    /**
     * Sets the firstDebitDate value for this DirectDebitMandateService.
     * 
     * @param firstDebitDate
     */
    public void setFirstDebitDate(java.lang.String firstDebitDate) {
        this.firstDebitDate = firstDebitDate;
    }


    /**
     * Gets the run value for this DirectDebitMandateService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this DirectDebitMandateService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DirectDebitMandateService)) return false;
        DirectDebitMandateService other = (DirectDebitMandateService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.mandateDescriptor==null && other.getMandateDescriptor()==null) || 
             (this.mandateDescriptor!=null &&
              this.mandateDescriptor.equals(other.getMandateDescriptor()))) &&
            ((this.firstDebitDate==null && other.getFirstDebitDate()==null) || 
             (this.firstDebitDate!=null &&
              this.firstDebitDate.equals(other.getFirstDebitDate()))) &&
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
        if (getMandateDescriptor() != null) {
            _hashCode += getMandateDescriptor().hashCode();
        }
        if (getFirstDebitDate() != null) {
            _hashCode += getFirstDebitDate().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DirectDebitMandateService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitMandateService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mandateDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "mandateDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstDebitDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "firstDebitDate"));
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
