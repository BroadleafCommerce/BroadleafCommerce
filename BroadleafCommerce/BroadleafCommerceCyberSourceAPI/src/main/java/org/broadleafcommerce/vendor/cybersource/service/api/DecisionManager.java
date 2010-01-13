/**
 * DecisionManager.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class DecisionManager  implements java.io.Serializable {
    private java.lang.String enabled;

    private java.lang.String profile;

    private org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelData travelData;

    public DecisionManager() {
    }

    public DecisionManager(
           java.lang.String enabled,
           java.lang.String profile,
           org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelData travelData) {
           this.enabled = enabled;
           this.profile = profile;
           this.travelData = travelData;
    }


    /**
     * Gets the enabled value for this DecisionManager.
     * 
     * @return enabled
     */
    public java.lang.String getEnabled() {
        return enabled;
    }


    /**
     * Sets the enabled value for this DecisionManager.
     * 
     * @param enabled
     */
    public void setEnabled(java.lang.String enabled) {
        this.enabled = enabled;
    }


    /**
     * Gets the profile value for this DecisionManager.
     * 
     * @return profile
     */
    public java.lang.String getProfile() {
        return profile;
    }


    /**
     * Sets the profile value for this DecisionManager.
     * 
     * @param profile
     */
    public void setProfile(java.lang.String profile) {
        this.profile = profile;
    }


    /**
     * Gets the travelData value for this DecisionManager.
     * 
     * @return travelData
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelData getTravelData() {
        return travelData;
    }


    /**
     * Sets the travelData value for this DecisionManager.
     * 
     * @param travelData
     */
    public void setTravelData(org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelData travelData) {
        this.travelData = travelData;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DecisionManager)) return false;
        DecisionManager other = (DecisionManager) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.enabled==null && other.getEnabled()==null) || 
             (this.enabled!=null &&
              this.enabled.equals(other.getEnabled()))) &&
            ((this.profile==null && other.getProfile()==null) || 
             (this.profile!=null &&
              this.profile.equals(other.getProfile()))) &&
            ((this.travelData==null && other.getTravelData()==null) || 
             (this.travelData!=null &&
              this.travelData.equals(other.getTravelData())));
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
        if (getEnabled() != null) {
            _hashCode += getEnabled().hashCode();
        }
        if (getProfile() != null) {
            _hashCode += getProfile().hashCode();
        }
        if (getTravelData() != null) {
            _hashCode += getTravelData().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DecisionManager.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManager"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("enabled");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "enabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("profile");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "profile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("travelData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "travelData"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManagerTravelData"));
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
