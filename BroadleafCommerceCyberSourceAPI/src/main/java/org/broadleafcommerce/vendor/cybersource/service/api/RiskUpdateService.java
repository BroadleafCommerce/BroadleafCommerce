/**
 * RiskUpdateService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class RiskUpdateService  implements java.io.Serializable {
    private java.lang.String actionCode;

    private java.lang.String recordID;

    private java.lang.String recordName;

    private org.broadleafcommerce.vendor.cybersource.service.api.Address negativeAddress;

    private java.lang.String markingReason;

    private java.lang.String markingNotes;

    private java.lang.String markingRequestID;

    private java.lang.String run;  // attribute

    public RiskUpdateService() {
    }

    public RiskUpdateService(
           java.lang.String actionCode,
           java.lang.String recordID,
           java.lang.String recordName,
           org.broadleafcommerce.vendor.cybersource.service.api.Address negativeAddress,
           java.lang.String markingReason,
           java.lang.String markingNotes,
           java.lang.String markingRequestID,
           java.lang.String run) {
           this.actionCode = actionCode;
           this.recordID = recordID;
           this.recordName = recordName;
           this.negativeAddress = negativeAddress;
           this.markingReason = markingReason;
           this.markingNotes = markingNotes;
           this.markingRequestID = markingRequestID;
           this.run = run;
    }


    /**
     * Gets the actionCode value for this RiskUpdateService.
     * 
     * @return actionCode
     */
    public java.lang.String getActionCode() {
        return actionCode;
    }


    /**
     * Sets the actionCode value for this RiskUpdateService.
     * 
     * @param actionCode
     */
    public void setActionCode(java.lang.String actionCode) {
        this.actionCode = actionCode;
    }


    /**
     * Gets the recordID value for this RiskUpdateService.
     * 
     * @return recordID
     */
    public java.lang.String getRecordID() {
        return recordID;
    }


    /**
     * Sets the recordID value for this RiskUpdateService.
     * 
     * @param recordID
     */
    public void setRecordID(java.lang.String recordID) {
        this.recordID = recordID;
    }


    /**
     * Gets the recordName value for this RiskUpdateService.
     * 
     * @return recordName
     */
    public java.lang.String getRecordName() {
        return recordName;
    }


    /**
     * Sets the recordName value for this RiskUpdateService.
     * 
     * @param recordName
     */
    public void setRecordName(java.lang.String recordName) {
        this.recordName = recordName;
    }


    /**
     * Gets the negativeAddress value for this RiskUpdateService.
     * 
     * @return negativeAddress
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Address getNegativeAddress() {
        return negativeAddress;
    }


    /**
     * Sets the negativeAddress value for this RiskUpdateService.
     * 
     * @param negativeAddress
     */
    public void setNegativeAddress(org.broadleafcommerce.vendor.cybersource.service.api.Address negativeAddress) {
        this.negativeAddress = negativeAddress;
    }


    /**
     * Gets the markingReason value for this RiskUpdateService.
     * 
     * @return markingReason
     */
    public java.lang.String getMarkingReason() {
        return markingReason;
    }


    /**
     * Sets the markingReason value for this RiskUpdateService.
     * 
     * @param markingReason
     */
    public void setMarkingReason(java.lang.String markingReason) {
        this.markingReason = markingReason;
    }


    /**
     * Gets the markingNotes value for this RiskUpdateService.
     * 
     * @return markingNotes
     */
    public java.lang.String getMarkingNotes() {
        return markingNotes;
    }


    /**
     * Sets the markingNotes value for this RiskUpdateService.
     * 
     * @param markingNotes
     */
    public void setMarkingNotes(java.lang.String markingNotes) {
        this.markingNotes = markingNotes;
    }


    /**
     * Gets the markingRequestID value for this RiskUpdateService.
     * 
     * @return markingRequestID
     */
    public java.lang.String getMarkingRequestID() {
        return markingRequestID;
    }


    /**
     * Sets the markingRequestID value for this RiskUpdateService.
     * 
     * @param markingRequestID
     */
    public void setMarkingRequestID(java.lang.String markingRequestID) {
        this.markingRequestID = markingRequestID;
    }


    /**
     * Gets the run value for this RiskUpdateService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this RiskUpdateService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RiskUpdateService)) return false;
        RiskUpdateService other = (RiskUpdateService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.actionCode==null && other.getActionCode()==null) || 
             (this.actionCode!=null &&
              this.actionCode.equals(other.getActionCode()))) &&
            ((this.recordID==null && other.getRecordID()==null) || 
             (this.recordID!=null &&
              this.recordID.equals(other.getRecordID()))) &&
            ((this.recordName==null && other.getRecordName()==null) || 
             (this.recordName!=null &&
              this.recordName.equals(other.getRecordName()))) &&
            ((this.negativeAddress==null && other.getNegativeAddress()==null) || 
             (this.negativeAddress!=null &&
              this.negativeAddress.equals(other.getNegativeAddress()))) &&
            ((this.markingReason==null && other.getMarkingReason()==null) || 
             (this.markingReason!=null &&
              this.markingReason.equals(other.getMarkingReason()))) &&
            ((this.markingNotes==null && other.getMarkingNotes()==null) || 
             (this.markingNotes!=null &&
              this.markingNotes.equals(other.getMarkingNotes()))) &&
            ((this.markingRequestID==null && other.getMarkingRequestID()==null) || 
             (this.markingRequestID!=null &&
              this.markingRequestID.equals(other.getMarkingRequestID()))) &&
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
        if (getActionCode() != null) {
            _hashCode += getActionCode().hashCode();
        }
        if (getRecordID() != null) {
            _hashCode += getRecordID().hashCode();
        }
        if (getRecordName() != null) {
            _hashCode += getRecordName().hashCode();
        }
        if (getNegativeAddress() != null) {
            _hashCode += getNegativeAddress().hashCode();
        }
        if (getMarkingReason() != null) {
            _hashCode += getMarkingReason().hashCode();
        }
        if (getMarkingNotes() != null) {
            _hashCode += getMarkingNotes().hashCode();
        }
        if (getMarkingRequestID() != null) {
            _hashCode += getMarkingRequestID().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RiskUpdateService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RiskUpdateService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actionCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "actionCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "recordID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recordName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "recordName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("negativeAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "negativeAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Address"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("markingReason");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "markingReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("markingNotes");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "markingNotes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("markingRequestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "markingRequestID"));
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
