/**
 * RuleResultItem.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class RuleResultItem  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String decision;

    private java.lang.String evaluation;

    private java.math.BigInteger ruleID;

    public RuleResultItem() {
    }

    public RuleResultItem(
           java.lang.String name,
           java.lang.String decision,
           java.lang.String evaluation,
           java.math.BigInteger ruleID) {
           this.name = name;
           this.decision = decision;
           this.evaluation = evaluation;
           this.ruleID = ruleID;
    }


    /**
     * Gets the name value for this RuleResultItem.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this RuleResultItem.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the decision value for this RuleResultItem.
     * 
     * @return decision
     */
    public java.lang.String getDecision() {
        return decision;
    }


    /**
     * Sets the decision value for this RuleResultItem.
     * 
     * @param decision
     */
    public void setDecision(java.lang.String decision) {
        this.decision = decision;
    }


    /**
     * Gets the evaluation value for this RuleResultItem.
     * 
     * @return evaluation
     */
    public java.lang.String getEvaluation() {
        return evaluation;
    }


    /**
     * Sets the evaluation value for this RuleResultItem.
     * 
     * @param evaluation
     */
    public void setEvaluation(java.lang.String evaluation) {
        this.evaluation = evaluation;
    }


    /**
     * Gets the ruleID value for this RuleResultItem.
     * 
     * @return ruleID
     */
    public java.math.BigInteger getRuleID() {
        return ruleID;
    }


    /**
     * Sets the ruleID value for this RuleResultItem.
     * 
     * @param ruleID
     */
    public void setRuleID(java.math.BigInteger ruleID) {
        this.ruleID = ruleID;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RuleResultItem)) return false;
        RuleResultItem other = (RuleResultItem) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.decision==null && other.getDecision()==null) || 
             (this.decision!=null &&
              this.decision.equals(other.getDecision()))) &&
            ((this.evaluation==null && other.getEvaluation()==null) || 
             (this.evaluation!=null &&
              this.evaluation.equals(other.getEvaluation()))) &&
            ((this.ruleID==null && other.getRuleID()==null) || 
             (this.ruleID!=null &&
              this.ruleID.equals(other.getRuleID())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getDecision() != null) {
            _hashCode += getDecision().hashCode();
        }
        if (getEvaluation() != null) {
            _hashCode += getEvaluation().hashCode();
        }
        if (getRuleID() != null) {
            _hashCode += getRuleID().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RuleResultItem.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RuleResultItem"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("decision");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "decision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("evaluation");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "evaluation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ruleID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ruleID"));
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
