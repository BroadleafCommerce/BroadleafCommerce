/**
 * JPO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class JPO  implements java.io.Serializable {
    private java.math.BigInteger paymentMethod;

    private java.lang.String bonusAmount;

    private java.math.BigInteger bonuses;

    private java.math.BigInteger installments;

    public JPO() {
    }

    public JPO(
           java.math.BigInteger paymentMethod,
           java.lang.String bonusAmount,
           java.math.BigInteger bonuses,
           java.math.BigInteger installments) {
           this.paymentMethod = paymentMethod;
           this.bonusAmount = bonusAmount;
           this.bonuses = bonuses;
           this.installments = installments;
    }


    /**
     * Gets the paymentMethod value for this JPO.
     * 
     * @return paymentMethod
     */
    public java.math.BigInteger getPaymentMethod() {
        return paymentMethod;
    }


    /**
     * Sets the paymentMethod value for this JPO.
     * 
     * @param paymentMethod
     */
    public void setPaymentMethod(java.math.BigInteger paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    /**
     * Gets the bonusAmount value for this JPO.
     * 
     * @return bonusAmount
     */
    public java.lang.String getBonusAmount() {
        return bonusAmount;
    }


    /**
     * Sets the bonusAmount value for this JPO.
     * 
     * @param bonusAmount
     */
    public void setBonusAmount(java.lang.String bonusAmount) {
        this.bonusAmount = bonusAmount;
    }


    /**
     * Gets the bonuses value for this JPO.
     * 
     * @return bonuses
     */
    public java.math.BigInteger getBonuses() {
        return bonuses;
    }


    /**
     * Sets the bonuses value for this JPO.
     * 
     * @param bonuses
     */
    public void setBonuses(java.math.BigInteger bonuses) {
        this.bonuses = bonuses;
    }


    /**
     * Gets the installments value for this JPO.
     * 
     * @return installments
     */
    public java.math.BigInteger getInstallments() {
        return installments;
    }


    /**
     * Sets the installments value for this JPO.
     * 
     * @param installments
     */
    public void setInstallments(java.math.BigInteger installments) {
        this.installments = installments;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof JPO)) return false;
        JPO other = (JPO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.paymentMethod==null && other.getPaymentMethod()==null) || 
             (this.paymentMethod!=null &&
              this.paymentMethod.equals(other.getPaymentMethod()))) &&
            ((this.bonusAmount==null && other.getBonusAmount()==null) || 
             (this.bonusAmount!=null &&
              this.bonusAmount.equals(other.getBonusAmount()))) &&
            ((this.bonuses==null && other.getBonuses()==null) || 
             (this.bonuses!=null &&
              this.bonuses.equals(other.getBonuses()))) &&
            ((this.installments==null && other.getInstallments()==null) || 
             (this.installments!=null &&
              this.installments.equals(other.getInstallments())));
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
        if (getPaymentMethod() != null) {
            _hashCode += getPaymentMethod().hashCode();
        }
        if (getBonusAmount() != null) {
            _hashCode += getBonusAmount().hashCode();
        }
        if (getBonuses() != null) {
            _hashCode += getBonuses().hashCode();
        }
        if (getInstallments() != null) {
            _hashCode += getInstallments().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(JPO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "JPO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymentMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paymentMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bonusAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bonusAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bonuses");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bonuses"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("installments");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "installments"));
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
