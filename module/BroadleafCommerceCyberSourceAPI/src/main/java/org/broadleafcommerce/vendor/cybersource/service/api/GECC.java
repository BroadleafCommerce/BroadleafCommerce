/**
 * GECC.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class GECC  implements java.io.Serializable {
    private java.lang.String saleType;

    private java.lang.String planNumber;

    private java.lang.String sequenceNumber;

    private java.lang.String promotionEndDate;

    private java.lang.String promotionPlan;

    private java.lang.String[] line;

    public GECC() {
    }

    public GECC(
           java.lang.String saleType,
           java.lang.String planNumber,
           java.lang.String sequenceNumber,
           java.lang.String promotionEndDate,
           java.lang.String promotionPlan,
           java.lang.String[] line) {
           this.saleType = saleType;
           this.planNumber = planNumber;
           this.sequenceNumber = sequenceNumber;
           this.promotionEndDate = promotionEndDate;
           this.promotionPlan = promotionPlan;
           this.line = line;
    }


    /**
     * Gets the saleType value for this GECC.
     * 
     * @return saleType
     */
    public java.lang.String getSaleType() {
        return saleType;
    }


    /**
     * Sets the saleType value for this GECC.
     * 
     * @param saleType
     */
    public void setSaleType(java.lang.String saleType) {
        this.saleType = saleType;
    }


    /**
     * Gets the planNumber value for this GECC.
     * 
     * @return planNumber
     */
    public java.lang.String getPlanNumber() {
        return planNumber;
    }


    /**
     * Sets the planNumber value for this GECC.
     * 
     * @param planNumber
     */
    public void setPlanNumber(java.lang.String planNumber) {
        this.planNumber = planNumber;
    }


    /**
     * Gets the sequenceNumber value for this GECC.
     * 
     * @return sequenceNumber
     */
    public java.lang.String getSequenceNumber() {
        return sequenceNumber;
    }


    /**
     * Sets the sequenceNumber value for this GECC.
     * 
     * @param sequenceNumber
     */
    public void setSequenceNumber(java.lang.String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    /**
     * Gets the promotionEndDate value for this GECC.
     * 
     * @return promotionEndDate
     */
    public java.lang.String getPromotionEndDate() {
        return promotionEndDate;
    }


    /**
     * Sets the promotionEndDate value for this GECC.
     * 
     * @param promotionEndDate
     */
    public void setPromotionEndDate(java.lang.String promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }


    /**
     * Gets the promotionPlan value for this GECC.
     * 
     * @return promotionPlan
     */
    public java.lang.String getPromotionPlan() {
        return promotionPlan;
    }


    /**
     * Sets the promotionPlan value for this GECC.
     * 
     * @param promotionPlan
     */
    public void setPromotionPlan(java.lang.String promotionPlan) {
        this.promotionPlan = promotionPlan;
    }


    /**
     * Gets the line value for this GECC.
     * 
     * @return line
     */
    public java.lang.String[] getLine() {
        return line;
    }


    /**
     * Sets the line value for this GECC.
     * 
     * @param line
     */
    public void setLine(java.lang.String[] line) {
        this.line = line;
    }

    public java.lang.String getLine(int i) {
        return this.line[i];
    }

    public void setLine(int i, java.lang.String _value) {
        this.line[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GECC)) return false;
        GECC other = (GECC) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.saleType==null && other.getSaleType()==null) || 
             (this.saleType!=null &&
              this.saleType.equals(other.getSaleType()))) &&
            ((this.planNumber==null && other.getPlanNumber()==null) || 
             (this.planNumber!=null &&
              this.planNumber.equals(other.getPlanNumber()))) &&
            ((this.sequenceNumber==null && other.getSequenceNumber()==null) || 
             (this.sequenceNumber!=null &&
              this.sequenceNumber.equals(other.getSequenceNumber()))) &&
            ((this.promotionEndDate==null && other.getPromotionEndDate()==null) || 
             (this.promotionEndDate!=null &&
              this.promotionEndDate.equals(other.getPromotionEndDate()))) &&
            ((this.promotionPlan==null && other.getPromotionPlan()==null) || 
             (this.promotionPlan!=null &&
              this.promotionPlan.equals(other.getPromotionPlan()))) &&
            ((this.line==null && other.getLine()==null) || 
             (this.line!=null &&
              java.util.Arrays.equals(this.line, other.getLine())));
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
        if (getSaleType() != null) {
            _hashCode += getSaleType().hashCode();
        }
        if (getPlanNumber() != null) {
            _hashCode += getPlanNumber().hashCode();
        }
        if (getSequenceNumber() != null) {
            _hashCode += getSequenceNumber().hashCode();
        }
        if (getPromotionEndDate() != null) {
            _hashCode += getPromotionEndDate().hashCode();
        }
        if (getPromotionPlan() != null) {
            _hashCode += getPromotionPlan().hashCode();
        }
        if (getLine() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLine());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLine(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GECC.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "GECC"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("saleType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "saleType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("planNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "planNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sequenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "sequenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("promotionEndDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "promotionEndDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("promotionPlan");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "promotionPlan"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("line");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "line"));
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
