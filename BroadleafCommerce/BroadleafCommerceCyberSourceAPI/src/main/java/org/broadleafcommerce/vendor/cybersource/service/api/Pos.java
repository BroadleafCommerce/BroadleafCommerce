/**
 * Pos.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class Pos  implements java.io.Serializable {
    private java.lang.String entryMode;

    private java.lang.String cardPresent;

    private java.lang.String terminalCapability;

    private java.lang.String trackData;

    private java.lang.String terminalID;

    private java.lang.String terminalType;

    private java.lang.String terminalLocation;

    private java.lang.String transactionSecurity;

    private java.lang.String catLevel;

    private java.lang.String conditionCode;

    public Pos() {
    }

    public Pos(
           java.lang.String entryMode,
           java.lang.String cardPresent,
           java.lang.String terminalCapability,
           java.lang.String trackData,
           java.lang.String terminalID,
           java.lang.String terminalType,
           java.lang.String terminalLocation,
           java.lang.String transactionSecurity,
           java.lang.String catLevel,
           java.lang.String conditionCode) {
           this.entryMode = entryMode;
           this.cardPresent = cardPresent;
           this.terminalCapability = terminalCapability;
           this.trackData = trackData;
           this.terminalID = terminalID;
           this.terminalType = terminalType;
           this.terminalLocation = terminalLocation;
           this.transactionSecurity = transactionSecurity;
           this.catLevel = catLevel;
           this.conditionCode = conditionCode;
    }


    /**
     * Gets the entryMode value for this Pos.
     * 
     * @return entryMode
     */
    public java.lang.String getEntryMode() {
        return entryMode;
    }


    /**
     * Sets the entryMode value for this Pos.
     * 
     * @param entryMode
     */
    public void setEntryMode(java.lang.String entryMode) {
        this.entryMode = entryMode;
    }


    /**
     * Gets the cardPresent value for this Pos.
     * 
     * @return cardPresent
     */
    public java.lang.String getCardPresent() {
        return cardPresent;
    }


    /**
     * Sets the cardPresent value for this Pos.
     * 
     * @param cardPresent
     */
    public void setCardPresent(java.lang.String cardPresent) {
        this.cardPresent = cardPresent;
    }


    /**
     * Gets the terminalCapability value for this Pos.
     * 
     * @return terminalCapability
     */
    public java.lang.String getTerminalCapability() {
        return terminalCapability;
    }


    /**
     * Sets the terminalCapability value for this Pos.
     * 
     * @param terminalCapability
     */
    public void setTerminalCapability(java.lang.String terminalCapability) {
        this.terminalCapability = terminalCapability;
    }


    /**
     * Gets the trackData value for this Pos.
     * 
     * @return trackData
     */
    public java.lang.String getTrackData() {
        return trackData;
    }


    /**
     * Sets the trackData value for this Pos.
     * 
     * @param trackData
     */
    public void setTrackData(java.lang.String trackData) {
        this.trackData = trackData;
    }


    /**
     * Gets the terminalID value for this Pos.
     * 
     * @return terminalID
     */
    public java.lang.String getTerminalID() {
        return terminalID;
    }


    /**
     * Sets the terminalID value for this Pos.
     * 
     * @param terminalID
     */
    public void setTerminalID(java.lang.String terminalID) {
        this.terminalID = terminalID;
    }


    /**
     * Gets the terminalType value for this Pos.
     * 
     * @return terminalType
     */
    public java.lang.String getTerminalType() {
        return terminalType;
    }


    /**
     * Sets the terminalType value for this Pos.
     * 
     * @param terminalType
     */
    public void setTerminalType(java.lang.String terminalType) {
        this.terminalType = terminalType;
    }


    /**
     * Gets the terminalLocation value for this Pos.
     * 
     * @return terminalLocation
     */
    public java.lang.String getTerminalLocation() {
        return terminalLocation;
    }


    /**
     * Sets the terminalLocation value for this Pos.
     * 
     * @param terminalLocation
     */
    public void setTerminalLocation(java.lang.String terminalLocation) {
        this.terminalLocation = terminalLocation;
    }


    /**
     * Gets the transactionSecurity value for this Pos.
     * 
     * @return transactionSecurity
     */
    public java.lang.String getTransactionSecurity() {
        return transactionSecurity;
    }


    /**
     * Sets the transactionSecurity value for this Pos.
     * 
     * @param transactionSecurity
     */
    public void setTransactionSecurity(java.lang.String transactionSecurity) {
        this.transactionSecurity = transactionSecurity;
    }


    /**
     * Gets the catLevel value for this Pos.
     * 
     * @return catLevel
     */
    public java.lang.String getCatLevel() {
        return catLevel;
    }


    /**
     * Sets the catLevel value for this Pos.
     * 
     * @param catLevel
     */
    public void setCatLevel(java.lang.String catLevel) {
        this.catLevel = catLevel;
    }


    /**
     * Gets the conditionCode value for this Pos.
     * 
     * @return conditionCode
     */
    public java.lang.String getConditionCode() {
        return conditionCode;
    }


    /**
     * Sets the conditionCode value for this Pos.
     * 
     * @param conditionCode
     */
    public void setConditionCode(java.lang.String conditionCode) {
        this.conditionCode = conditionCode;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Pos)) return false;
        Pos other = (Pos) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.entryMode==null && other.getEntryMode()==null) || 
             (this.entryMode!=null &&
              this.entryMode.equals(other.getEntryMode()))) &&
            ((this.cardPresent==null && other.getCardPresent()==null) || 
             (this.cardPresent!=null &&
              this.cardPresent.equals(other.getCardPresent()))) &&
            ((this.terminalCapability==null && other.getTerminalCapability()==null) || 
             (this.terminalCapability!=null &&
              this.terminalCapability.equals(other.getTerminalCapability()))) &&
            ((this.trackData==null && other.getTrackData()==null) || 
             (this.trackData!=null &&
              this.trackData.equals(other.getTrackData()))) &&
            ((this.terminalID==null && other.getTerminalID()==null) || 
             (this.terminalID!=null &&
              this.terminalID.equals(other.getTerminalID()))) &&
            ((this.terminalType==null && other.getTerminalType()==null) || 
             (this.terminalType!=null &&
              this.terminalType.equals(other.getTerminalType()))) &&
            ((this.terminalLocation==null && other.getTerminalLocation()==null) || 
             (this.terminalLocation!=null &&
              this.terminalLocation.equals(other.getTerminalLocation()))) &&
            ((this.transactionSecurity==null && other.getTransactionSecurity()==null) || 
             (this.transactionSecurity!=null &&
              this.transactionSecurity.equals(other.getTransactionSecurity()))) &&
            ((this.catLevel==null && other.getCatLevel()==null) || 
             (this.catLevel!=null &&
              this.catLevel.equals(other.getCatLevel()))) &&
            ((this.conditionCode==null && other.getConditionCode()==null) || 
             (this.conditionCode!=null &&
              this.conditionCode.equals(other.getConditionCode())));
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
        if (getEntryMode() != null) {
            _hashCode += getEntryMode().hashCode();
        }
        if (getCardPresent() != null) {
            _hashCode += getCardPresent().hashCode();
        }
        if (getTerminalCapability() != null) {
            _hashCode += getTerminalCapability().hashCode();
        }
        if (getTrackData() != null) {
            _hashCode += getTrackData().hashCode();
        }
        if (getTerminalID() != null) {
            _hashCode += getTerminalID().hashCode();
        }
        if (getTerminalType() != null) {
            _hashCode += getTerminalType().hashCode();
        }
        if (getTerminalLocation() != null) {
            _hashCode += getTerminalLocation().hashCode();
        }
        if (getTransactionSecurity() != null) {
            _hashCode += getTransactionSecurity().hashCode();
        }
        if (getCatLevel() != null) {
            _hashCode += getCatLevel().hashCode();
        }
        if (getConditionCode() != null) {
            _hashCode += getConditionCode().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Pos.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Pos"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entryMode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "entryMode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cardPresent");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cardPresent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalCapability");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "terminalCapability"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("trackData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "trackData"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "terminalID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "terminalType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("terminalLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "terminalLocation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("transactionSecurity");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "transactionSecurity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("catLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "catLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conditionCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "conditionCode"));
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
