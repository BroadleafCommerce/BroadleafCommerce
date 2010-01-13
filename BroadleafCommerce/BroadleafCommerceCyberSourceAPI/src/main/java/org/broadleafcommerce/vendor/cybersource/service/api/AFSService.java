/**
 * AFSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class AFSService  implements java.io.Serializable {
    private java.lang.String avsCode;

    private java.lang.String cvCode;

    private java.lang.String disableAVSScoring;

    private java.lang.String customRiskModel;

    private java.lang.String run;  // attribute

    public AFSService() {
    }

    public AFSService(
           java.lang.String avsCode,
           java.lang.String cvCode,
           java.lang.String disableAVSScoring,
           java.lang.String customRiskModel,
           java.lang.String run) {
           this.avsCode = avsCode;
           this.cvCode = cvCode;
           this.disableAVSScoring = disableAVSScoring;
           this.customRiskModel = customRiskModel;
           this.run = run;
    }


    /**
     * Gets the avsCode value for this AFSService.
     * 
     * @return avsCode
     */
    public java.lang.String getAvsCode() {
        return avsCode;
    }


    /**
     * Sets the avsCode value for this AFSService.
     * 
     * @param avsCode
     */
    public void setAvsCode(java.lang.String avsCode) {
        this.avsCode = avsCode;
    }


    /**
     * Gets the cvCode value for this AFSService.
     * 
     * @return cvCode
     */
    public java.lang.String getCvCode() {
        return cvCode;
    }


    /**
     * Sets the cvCode value for this AFSService.
     * 
     * @param cvCode
     */
    public void setCvCode(java.lang.String cvCode) {
        this.cvCode = cvCode;
    }


    /**
     * Gets the disableAVSScoring value for this AFSService.
     * 
     * @return disableAVSScoring
     */
    public java.lang.String getDisableAVSScoring() {
        return disableAVSScoring;
    }


    /**
     * Sets the disableAVSScoring value for this AFSService.
     * 
     * @param disableAVSScoring
     */
    public void setDisableAVSScoring(java.lang.String disableAVSScoring) {
        this.disableAVSScoring = disableAVSScoring;
    }


    /**
     * Gets the customRiskModel value for this AFSService.
     * 
     * @return customRiskModel
     */
    public java.lang.String getCustomRiskModel() {
        return customRiskModel;
    }


    /**
     * Sets the customRiskModel value for this AFSService.
     * 
     * @param customRiskModel
     */
    public void setCustomRiskModel(java.lang.String customRiskModel) {
        this.customRiskModel = customRiskModel;
    }


    /**
     * Gets the run value for this AFSService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this AFSService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AFSService)) return false;
        AFSService other = (AFSService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.avsCode==null && other.getAvsCode()==null) || 
             (this.avsCode!=null &&
              this.avsCode.equals(other.getAvsCode()))) &&
            ((this.cvCode==null && other.getCvCode()==null) || 
             (this.cvCode!=null &&
              this.cvCode.equals(other.getCvCode()))) &&
            ((this.disableAVSScoring==null && other.getDisableAVSScoring()==null) || 
             (this.disableAVSScoring!=null &&
              this.disableAVSScoring.equals(other.getDisableAVSScoring()))) &&
            ((this.customRiskModel==null && other.getCustomRiskModel()==null) || 
             (this.customRiskModel!=null &&
              this.customRiskModel.equals(other.getCustomRiskModel()))) &&
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
        if (getAvsCode() != null) {
            _hashCode += getAvsCode().hashCode();
        }
        if (getCvCode() != null) {
            _hashCode += getCvCode().hashCode();
        }
        if (getDisableAVSScoring() != null) {
            _hashCode += getDisableAVSScoring().hashCode();
        }
        if (getCustomRiskModel() != null) {
            _hashCode += getCustomRiskModel().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AFSService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AFSService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "avsCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cvCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cvCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("disableAVSScoring");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "disableAVSScoring"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customRiskModel");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "customRiskModel"));
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
