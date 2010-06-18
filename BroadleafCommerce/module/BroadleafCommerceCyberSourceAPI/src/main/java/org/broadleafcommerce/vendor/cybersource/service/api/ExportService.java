/**
 * ExportService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ExportService  implements java.io.Serializable {
    private java.lang.String addressOperator;

    private java.lang.String addressWeight;

    private java.lang.String companyWeight;

    private java.lang.String nameWeight;

    private java.lang.String run;  // attribute

    public ExportService() {
    }

    public ExportService(
           java.lang.String addressOperator,
           java.lang.String addressWeight,
           java.lang.String companyWeight,
           java.lang.String nameWeight,
           java.lang.String run) {
           this.addressOperator = addressOperator;
           this.addressWeight = addressWeight;
           this.companyWeight = companyWeight;
           this.nameWeight = nameWeight;
           this.run = run;
    }


    /**
     * Gets the addressOperator value for this ExportService.
     * 
     * @return addressOperator
     */
    public java.lang.String getAddressOperator() {
        return addressOperator;
    }


    /**
     * Sets the addressOperator value for this ExportService.
     * 
     * @param addressOperator
     */
    public void setAddressOperator(java.lang.String addressOperator) {
        this.addressOperator = addressOperator;
    }


    /**
     * Gets the addressWeight value for this ExportService.
     * 
     * @return addressWeight
     */
    public java.lang.String getAddressWeight() {
        return addressWeight;
    }


    /**
     * Sets the addressWeight value for this ExportService.
     * 
     * @param addressWeight
     */
    public void setAddressWeight(java.lang.String addressWeight) {
        this.addressWeight = addressWeight;
    }


    /**
     * Gets the companyWeight value for this ExportService.
     * 
     * @return companyWeight
     */
    public java.lang.String getCompanyWeight() {
        return companyWeight;
    }


    /**
     * Sets the companyWeight value for this ExportService.
     * 
     * @param companyWeight
     */
    public void setCompanyWeight(java.lang.String companyWeight) {
        this.companyWeight = companyWeight;
    }


    /**
     * Gets the nameWeight value for this ExportService.
     * 
     * @return nameWeight
     */
    public java.lang.String getNameWeight() {
        return nameWeight;
    }


    /**
     * Sets the nameWeight value for this ExportService.
     * 
     * @param nameWeight
     */
    public void setNameWeight(java.lang.String nameWeight) {
        this.nameWeight = nameWeight;
    }


    /**
     * Gets the run value for this ExportService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this ExportService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ExportService)) return false;
        ExportService other = (ExportService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.addressOperator==null && other.getAddressOperator()==null) || 
             (this.addressOperator!=null &&
              this.addressOperator.equals(other.getAddressOperator()))) &&
            ((this.addressWeight==null && other.getAddressWeight()==null) || 
             (this.addressWeight!=null &&
              this.addressWeight.equals(other.getAddressWeight()))) &&
            ((this.companyWeight==null && other.getCompanyWeight()==null) || 
             (this.companyWeight!=null &&
              this.companyWeight.equals(other.getCompanyWeight()))) &&
            ((this.nameWeight==null && other.getNameWeight()==null) || 
             (this.nameWeight!=null &&
              this.nameWeight.equals(other.getNameWeight()))) &&
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
        if (getAddressOperator() != null) {
            _hashCode += getAddressOperator().hashCode();
        }
        if (getAddressWeight() != null) {
            _hashCode += getAddressWeight().hashCode();
        }
        if (getCompanyWeight() != null) {
            _hashCode += getCompanyWeight().hashCode();
        }
        if (getNameWeight() != null) {
            _hashCode += getNameWeight().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ExportService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ExportService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressOperator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressOperator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addressWeight");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "addressWeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyWeight");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "companyWeight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameWeight");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "nameWeight"));
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
