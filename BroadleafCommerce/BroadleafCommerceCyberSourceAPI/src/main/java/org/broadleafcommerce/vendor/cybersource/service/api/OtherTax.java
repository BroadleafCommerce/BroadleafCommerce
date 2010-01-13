/**
 * OtherTax.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class OtherTax  implements java.io.Serializable {
    private java.lang.String vatTaxAmount;

    private java.lang.String vatTaxRate;

    private java.lang.String alternateTaxAmount;

    private java.lang.String alternateTaxIndicator;

    private java.lang.String alternateTaxID;

    private java.lang.String localTaxAmount;

    private java.math.BigInteger localTaxIndicator;

    private java.lang.String nationalTaxAmount;

    private java.math.BigInteger nationalTaxIndicator;

    public OtherTax() {
    }

    public OtherTax(
           java.lang.String vatTaxAmount,
           java.lang.String vatTaxRate,
           java.lang.String alternateTaxAmount,
           java.lang.String alternateTaxIndicator,
           java.lang.String alternateTaxID,
           java.lang.String localTaxAmount,
           java.math.BigInteger localTaxIndicator,
           java.lang.String nationalTaxAmount,
           java.math.BigInteger nationalTaxIndicator) {
           this.vatTaxAmount = vatTaxAmount;
           this.vatTaxRate = vatTaxRate;
           this.alternateTaxAmount = alternateTaxAmount;
           this.alternateTaxIndicator = alternateTaxIndicator;
           this.alternateTaxID = alternateTaxID;
           this.localTaxAmount = localTaxAmount;
           this.localTaxIndicator = localTaxIndicator;
           this.nationalTaxAmount = nationalTaxAmount;
           this.nationalTaxIndicator = nationalTaxIndicator;
    }


    /**
     * Gets the vatTaxAmount value for this OtherTax.
     * 
     * @return vatTaxAmount
     */
    public java.lang.String getVatTaxAmount() {
        return vatTaxAmount;
    }


    /**
     * Sets the vatTaxAmount value for this OtherTax.
     * 
     * @param vatTaxAmount
     */
    public void setVatTaxAmount(java.lang.String vatTaxAmount) {
        this.vatTaxAmount = vatTaxAmount;
    }


    /**
     * Gets the vatTaxRate value for this OtherTax.
     * 
     * @return vatTaxRate
     */
    public java.lang.String getVatTaxRate() {
        return vatTaxRate;
    }


    /**
     * Sets the vatTaxRate value for this OtherTax.
     * 
     * @param vatTaxRate
     */
    public void setVatTaxRate(java.lang.String vatTaxRate) {
        this.vatTaxRate = vatTaxRate;
    }


    /**
     * Gets the alternateTaxAmount value for this OtherTax.
     * 
     * @return alternateTaxAmount
     */
    public java.lang.String getAlternateTaxAmount() {
        return alternateTaxAmount;
    }


    /**
     * Sets the alternateTaxAmount value for this OtherTax.
     * 
     * @param alternateTaxAmount
     */
    public void setAlternateTaxAmount(java.lang.String alternateTaxAmount) {
        this.alternateTaxAmount = alternateTaxAmount;
    }


    /**
     * Gets the alternateTaxIndicator value for this OtherTax.
     * 
     * @return alternateTaxIndicator
     */
    public java.lang.String getAlternateTaxIndicator() {
        return alternateTaxIndicator;
    }


    /**
     * Sets the alternateTaxIndicator value for this OtherTax.
     * 
     * @param alternateTaxIndicator
     */
    public void setAlternateTaxIndicator(java.lang.String alternateTaxIndicator) {
        this.alternateTaxIndicator = alternateTaxIndicator;
    }


    /**
     * Gets the alternateTaxID value for this OtherTax.
     * 
     * @return alternateTaxID
     */
    public java.lang.String getAlternateTaxID() {
        return alternateTaxID;
    }


    /**
     * Sets the alternateTaxID value for this OtherTax.
     * 
     * @param alternateTaxID
     */
    public void setAlternateTaxID(java.lang.String alternateTaxID) {
        this.alternateTaxID = alternateTaxID;
    }


    /**
     * Gets the localTaxAmount value for this OtherTax.
     * 
     * @return localTaxAmount
     */
    public java.lang.String getLocalTaxAmount() {
        return localTaxAmount;
    }


    /**
     * Sets the localTaxAmount value for this OtherTax.
     * 
     * @param localTaxAmount
     */
    public void setLocalTaxAmount(java.lang.String localTaxAmount) {
        this.localTaxAmount = localTaxAmount;
    }


    /**
     * Gets the localTaxIndicator value for this OtherTax.
     * 
     * @return localTaxIndicator
     */
    public java.math.BigInteger getLocalTaxIndicator() {
        return localTaxIndicator;
    }


    /**
     * Sets the localTaxIndicator value for this OtherTax.
     * 
     * @param localTaxIndicator
     */
    public void setLocalTaxIndicator(java.math.BigInteger localTaxIndicator) {
        this.localTaxIndicator = localTaxIndicator;
    }


    /**
     * Gets the nationalTaxAmount value for this OtherTax.
     * 
     * @return nationalTaxAmount
     */
    public java.lang.String getNationalTaxAmount() {
        return nationalTaxAmount;
    }


    /**
     * Sets the nationalTaxAmount value for this OtherTax.
     * 
     * @param nationalTaxAmount
     */
    public void setNationalTaxAmount(java.lang.String nationalTaxAmount) {
        this.nationalTaxAmount = nationalTaxAmount;
    }


    /**
     * Gets the nationalTaxIndicator value for this OtherTax.
     * 
     * @return nationalTaxIndicator
     */
    public java.math.BigInteger getNationalTaxIndicator() {
        return nationalTaxIndicator;
    }


    /**
     * Sets the nationalTaxIndicator value for this OtherTax.
     * 
     * @param nationalTaxIndicator
     */
    public void setNationalTaxIndicator(java.math.BigInteger nationalTaxIndicator) {
        this.nationalTaxIndicator = nationalTaxIndicator;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OtherTax)) return false;
        OtherTax other = (OtherTax) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.vatTaxAmount==null && other.getVatTaxAmount()==null) || 
             (this.vatTaxAmount!=null &&
              this.vatTaxAmount.equals(other.getVatTaxAmount()))) &&
            ((this.vatTaxRate==null && other.getVatTaxRate()==null) || 
             (this.vatTaxRate!=null &&
              this.vatTaxRate.equals(other.getVatTaxRate()))) &&
            ((this.alternateTaxAmount==null && other.getAlternateTaxAmount()==null) || 
             (this.alternateTaxAmount!=null &&
              this.alternateTaxAmount.equals(other.getAlternateTaxAmount()))) &&
            ((this.alternateTaxIndicator==null && other.getAlternateTaxIndicator()==null) || 
             (this.alternateTaxIndicator!=null &&
              this.alternateTaxIndicator.equals(other.getAlternateTaxIndicator()))) &&
            ((this.alternateTaxID==null && other.getAlternateTaxID()==null) || 
             (this.alternateTaxID!=null &&
              this.alternateTaxID.equals(other.getAlternateTaxID()))) &&
            ((this.localTaxAmount==null && other.getLocalTaxAmount()==null) || 
             (this.localTaxAmount!=null &&
              this.localTaxAmount.equals(other.getLocalTaxAmount()))) &&
            ((this.localTaxIndicator==null && other.getLocalTaxIndicator()==null) || 
             (this.localTaxIndicator!=null &&
              this.localTaxIndicator.equals(other.getLocalTaxIndicator()))) &&
            ((this.nationalTaxAmount==null && other.getNationalTaxAmount()==null) || 
             (this.nationalTaxAmount!=null &&
              this.nationalTaxAmount.equals(other.getNationalTaxAmount()))) &&
            ((this.nationalTaxIndicator==null && other.getNationalTaxIndicator()==null) || 
             (this.nationalTaxIndicator!=null &&
              this.nationalTaxIndicator.equals(other.getNationalTaxIndicator())));
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
        if (getVatTaxAmount() != null) {
            _hashCode += getVatTaxAmount().hashCode();
        }
        if (getVatTaxRate() != null) {
            _hashCode += getVatTaxRate().hashCode();
        }
        if (getAlternateTaxAmount() != null) {
            _hashCode += getAlternateTaxAmount().hashCode();
        }
        if (getAlternateTaxIndicator() != null) {
            _hashCode += getAlternateTaxIndicator().hashCode();
        }
        if (getAlternateTaxID() != null) {
            _hashCode += getAlternateTaxID().hashCode();
        }
        if (getLocalTaxAmount() != null) {
            _hashCode += getLocalTaxAmount().hashCode();
        }
        if (getLocalTaxIndicator() != null) {
            _hashCode += getLocalTaxIndicator().hashCode();
        }
        if (getNationalTaxAmount() != null) {
            _hashCode += getNationalTaxAmount().hashCode();
        }
        if (getNationalTaxIndicator() != null) {
            _hashCode += getNationalTaxIndicator().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OtherTax.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "OtherTax"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vatTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "vatTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vatTaxRate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "vatTaxRate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alternateTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "alternateTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alternateTaxIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "alternateTaxIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("alternateTaxID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "alternateTaxID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "localTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localTaxIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "localTaxIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nationalTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "nationalTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nationalTaxIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "nationalTaxIndicator"));
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
