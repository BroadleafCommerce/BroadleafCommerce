/**
 * TaxReplyItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class TaxReplyItem  implements java.io.Serializable {
    private java.lang.String cityTaxAmount;

    private java.lang.String countyTaxAmount;

    private java.lang.String districtTaxAmount;

    private java.lang.String stateTaxAmount;

    private java.lang.String totalTaxAmount;

    private java.math.BigInteger id;  // attribute

    public TaxReplyItem() {
    }

    public TaxReplyItem(
           java.lang.String cityTaxAmount,
           java.lang.String countyTaxAmount,
           java.lang.String districtTaxAmount,
           java.lang.String stateTaxAmount,
           java.lang.String totalTaxAmount,
           java.math.BigInteger id) {
           this.cityTaxAmount = cityTaxAmount;
           this.countyTaxAmount = countyTaxAmount;
           this.districtTaxAmount = districtTaxAmount;
           this.stateTaxAmount = stateTaxAmount;
           this.totalTaxAmount = totalTaxAmount;
           this.id = id;
    }


    /**
     * Gets the cityTaxAmount value for this TaxReplyItem.
     * 
     * @return cityTaxAmount
     */
    public java.lang.String getCityTaxAmount() {
        return cityTaxAmount;
    }


    /**
     * Sets the cityTaxAmount value for this TaxReplyItem.
     * 
     * @param cityTaxAmount
     */
    public void setCityTaxAmount(java.lang.String cityTaxAmount) {
        this.cityTaxAmount = cityTaxAmount;
    }


    /**
     * Gets the countyTaxAmount value for this TaxReplyItem.
     * 
     * @return countyTaxAmount
     */
    public java.lang.String getCountyTaxAmount() {
        return countyTaxAmount;
    }


    /**
     * Sets the countyTaxAmount value for this TaxReplyItem.
     * 
     * @param countyTaxAmount
     */
    public void setCountyTaxAmount(java.lang.String countyTaxAmount) {
        this.countyTaxAmount = countyTaxAmount;
    }


    /**
     * Gets the districtTaxAmount value for this TaxReplyItem.
     * 
     * @return districtTaxAmount
     */
    public java.lang.String getDistrictTaxAmount() {
        return districtTaxAmount;
    }


    /**
     * Sets the districtTaxAmount value for this TaxReplyItem.
     * 
     * @param districtTaxAmount
     */
    public void setDistrictTaxAmount(java.lang.String districtTaxAmount) {
        this.districtTaxAmount = districtTaxAmount;
    }


    /**
     * Gets the stateTaxAmount value for this TaxReplyItem.
     * 
     * @return stateTaxAmount
     */
    public java.lang.String getStateTaxAmount() {
        return stateTaxAmount;
    }


    /**
     * Sets the stateTaxAmount value for this TaxReplyItem.
     * 
     * @param stateTaxAmount
     */
    public void setStateTaxAmount(java.lang.String stateTaxAmount) {
        this.stateTaxAmount = stateTaxAmount;
    }


    /**
     * Gets the totalTaxAmount value for this TaxReplyItem.
     * 
     * @return totalTaxAmount
     */
    public java.lang.String getTotalTaxAmount() {
        return totalTaxAmount;
    }


    /**
     * Sets the totalTaxAmount value for this TaxReplyItem.
     * 
     * @param totalTaxAmount
     */
    public void setTotalTaxAmount(java.lang.String totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }


    /**
     * Gets the id value for this TaxReplyItem.
     * 
     * @return id
     */
    public java.math.BigInteger getId() {
        return id;
    }


    /**
     * Sets the id value for this TaxReplyItem.
     * 
     * @param id
     */
    public void setId(java.math.BigInteger id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof TaxReplyItem)) return false;
        TaxReplyItem other = (TaxReplyItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cityTaxAmount==null && other.getCityTaxAmount()==null) || 
             (this.cityTaxAmount!=null &&
              this.cityTaxAmount.equals(other.getCityTaxAmount()))) &&
            ((this.countyTaxAmount==null && other.getCountyTaxAmount()==null) || 
             (this.countyTaxAmount!=null &&
              this.countyTaxAmount.equals(other.getCountyTaxAmount()))) &&
            ((this.districtTaxAmount==null && other.getDistrictTaxAmount()==null) || 
             (this.districtTaxAmount!=null &&
              this.districtTaxAmount.equals(other.getDistrictTaxAmount()))) &&
            ((this.stateTaxAmount==null && other.getStateTaxAmount()==null) || 
             (this.stateTaxAmount!=null &&
              this.stateTaxAmount.equals(other.getStateTaxAmount()))) &&
            ((this.totalTaxAmount==null && other.getTotalTaxAmount()==null) || 
             (this.totalTaxAmount!=null &&
              this.totalTaxAmount.equals(other.getTotalTaxAmount()))) &&
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId())));
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
        if (getCityTaxAmount() != null) {
            _hashCode += getCityTaxAmount().hashCode();
        }
        if (getCountyTaxAmount() != null) {
            _hashCode += getCountyTaxAmount().hashCode();
        }
        if (getDistrictTaxAmount() != null) {
            _hashCode += getDistrictTaxAmount().hashCode();
        }
        if (getStateTaxAmount() != null) {
            _hashCode += getStateTaxAmount().hashCode();
        }
        if (getTotalTaxAmount() != null) {
            _hashCode += getTotalTaxAmount().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(TaxReplyItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "TaxReplyItem"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cityTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cityTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countyTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "countyTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("districtTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "districtTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stateTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "stateTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalTaxAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "totalTaxAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
