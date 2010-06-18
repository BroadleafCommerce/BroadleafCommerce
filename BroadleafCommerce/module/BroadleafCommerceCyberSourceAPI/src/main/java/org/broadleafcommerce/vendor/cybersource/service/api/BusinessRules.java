/**
 * BusinessRules.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class BusinessRules  implements java.io.Serializable {
    private java.lang.String ignoreAVSResult;

    private java.lang.String ignoreCVResult;

    private java.lang.String ignoreDAVResult;

    private java.lang.String ignoreExportResult;

    private java.lang.String ignoreValidateResult;

    private java.lang.String declineAVSFlags;

    private java.math.BigInteger scoreThreshold;

    public BusinessRules() {
    }

    public BusinessRules(
           java.lang.String ignoreAVSResult,
           java.lang.String ignoreCVResult,
           java.lang.String ignoreDAVResult,
           java.lang.String ignoreExportResult,
           java.lang.String ignoreValidateResult,
           java.lang.String declineAVSFlags,
           java.math.BigInteger scoreThreshold) {
           this.ignoreAVSResult = ignoreAVSResult;
           this.ignoreCVResult = ignoreCVResult;
           this.ignoreDAVResult = ignoreDAVResult;
           this.ignoreExportResult = ignoreExportResult;
           this.ignoreValidateResult = ignoreValidateResult;
           this.declineAVSFlags = declineAVSFlags;
           this.scoreThreshold = scoreThreshold;
    }


    /**
     * Gets the ignoreAVSResult value for this BusinessRules.
     * 
     * @return ignoreAVSResult
     */
    public java.lang.String getIgnoreAVSResult() {
        return ignoreAVSResult;
    }


    /**
     * Sets the ignoreAVSResult value for this BusinessRules.
     * 
     * @param ignoreAVSResult
     */
    public void setIgnoreAVSResult(java.lang.String ignoreAVSResult) {
        this.ignoreAVSResult = ignoreAVSResult;
    }


    /**
     * Gets the ignoreCVResult value for this BusinessRules.
     * 
     * @return ignoreCVResult
     */
    public java.lang.String getIgnoreCVResult() {
        return ignoreCVResult;
    }


    /**
     * Sets the ignoreCVResult value for this BusinessRules.
     * 
     * @param ignoreCVResult
     */
    public void setIgnoreCVResult(java.lang.String ignoreCVResult) {
        this.ignoreCVResult = ignoreCVResult;
    }


    /**
     * Gets the ignoreDAVResult value for this BusinessRules.
     * 
     * @return ignoreDAVResult
     */
    public java.lang.String getIgnoreDAVResult() {
        return ignoreDAVResult;
    }


    /**
     * Sets the ignoreDAVResult value for this BusinessRules.
     * 
     * @param ignoreDAVResult
     */
    public void setIgnoreDAVResult(java.lang.String ignoreDAVResult) {
        this.ignoreDAVResult = ignoreDAVResult;
    }


    /**
     * Gets the ignoreExportResult value for this BusinessRules.
     * 
     * @return ignoreExportResult
     */
    public java.lang.String getIgnoreExportResult() {
        return ignoreExportResult;
    }


    /**
     * Sets the ignoreExportResult value for this BusinessRules.
     * 
     * @param ignoreExportResult
     */
    public void setIgnoreExportResult(java.lang.String ignoreExportResult) {
        this.ignoreExportResult = ignoreExportResult;
    }


    /**
     * Gets the ignoreValidateResult value for this BusinessRules.
     * 
     * @return ignoreValidateResult
     */
    public java.lang.String getIgnoreValidateResult() {
        return ignoreValidateResult;
    }


    /**
     * Sets the ignoreValidateResult value for this BusinessRules.
     * 
     * @param ignoreValidateResult
     */
    public void setIgnoreValidateResult(java.lang.String ignoreValidateResult) {
        this.ignoreValidateResult = ignoreValidateResult;
    }


    /**
     * Gets the declineAVSFlags value for this BusinessRules.
     * 
     * @return declineAVSFlags
     */
    public java.lang.String getDeclineAVSFlags() {
        return declineAVSFlags;
    }


    /**
     * Sets the declineAVSFlags value for this BusinessRules.
     * 
     * @param declineAVSFlags
     */
    public void setDeclineAVSFlags(java.lang.String declineAVSFlags) {
        this.declineAVSFlags = declineAVSFlags;
    }


    /**
     * Gets the scoreThreshold value for this BusinessRules.
     * 
     * @return scoreThreshold
     */
    public java.math.BigInteger getScoreThreshold() {
        return scoreThreshold;
    }


    /**
     * Sets the scoreThreshold value for this BusinessRules.
     * 
     * @param scoreThreshold
     */
    public void setScoreThreshold(java.math.BigInteger scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof BusinessRules)) return false;
        BusinessRules other = (BusinessRules) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ignoreAVSResult==null && other.getIgnoreAVSResult()==null) || 
             (this.ignoreAVSResult!=null &&
              this.ignoreAVSResult.equals(other.getIgnoreAVSResult()))) &&
            ((this.ignoreCVResult==null && other.getIgnoreCVResult()==null) || 
             (this.ignoreCVResult!=null &&
              this.ignoreCVResult.equals(other.getIgnoreCVResult()))) &&
            ((this.ignoreDAVResult==null && other.getIgnoreDAVResult()==null) || 
             (this.ignoreDAVResult!=null &&
              this.ignoreDAVResult.equals(other.getIgnoreDAVResult()))) &&
            ((this.ignoreExportResult==null && other.getIgnoreExportResult()==null) || 
             (this.ignoreExportResult!=null &&
              this.ignoreExportResult.equals(other.getIgnoreExportResult()))) &&
            ((this.ignoreValidateResult==null && other.getIgnoreValidateResult()==null) || 
             (this.ignoreValidateResult!=null &&
              this.ignoreValidateResult.equals(other.getIgnoreValidateResult()))) &&
            ((this.declineAVSFlags==null && other.getDeclineAVSFlags()==null) || 
             (this.declineAVSFlags!=null &&
              this.declineAVSFlags.equals(other.getDeclineAVSFlags()))) &&
            ((this.scoreThreshold==null && other.getScoreThreshold()==null) || 
             (this.scoreThreshold!=null &&
              this.scoreThreshold.equals(other.getScoreThreshold())));
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
        if (getIgnoreAVSResult() != null) {
            _hashCode += getIgnoreAVSResult().hashCode();
        }
        if (getIgnoreCVResult() != null) {
            _hashCode += getIgnoreCVResult().hashCode();
        }
        if (getIgnoreDAVResult() != null) {
            _hashCode += getIgnoreDAVResult().hashCode();
        }
        if (getIgnoreExportResult() != null) {
            _hashCode += getIgnoreExportResult().hashCode();
        }
        if (getIgnoreValidateResult() != null) {
            _hashCode += getIgnoreValidateResult().hashCode();
        }
        if (getDeclineAVSFlags() != null) {
            _hashCode += getDeclineAVSFlags().hashCode();
        }
        if (getScoreThreshold() != null) {
            _hashCode += getScoreThreshold().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(BusinessRules.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BusinessRules"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignoreAVSResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ignoreAVSResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignoreCVResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ignoreCVResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignoreDAVResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ignoreDAVResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignoreExportResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ignoreExportResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ignoreValidateResult");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ignoreValidateResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("declineAVSFlags");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "declineAVSFlags"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scoreThreshold");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "scoreThreshold"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
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
