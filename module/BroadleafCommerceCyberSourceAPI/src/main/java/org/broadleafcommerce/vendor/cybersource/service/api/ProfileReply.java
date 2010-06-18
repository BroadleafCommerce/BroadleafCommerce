/**
 * ProfileReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ProfileReply  implements java.io.Serializable {
    private java.lang.String selectedBy;

    private java.lang.String name;

    private java.lang.String destinationQueue;

    private org.broadleafcommerce.vendor.cybersource.service.api.RuleResultItem[] rulesTriggered;

    public ProfileReply() {
    }

    public ProfileReply(
           java.lang.String selectedBy,
           java.lang.String name,
           java.lang.String destinationQueue,
           org.broadleafcommerce.vendor.cybersource.service.api.RuleResultItem[] rulesTriggered) {
           this.selectedBy = selectedBy;
           this.name = name;
           this.destinationQueue = destinationQueue;
           this.rulesTriggered = rulesTriggered;
    }


    /**
     * Gets the selectedBy value for this ProfileReply.
     * 
     * @return selectedBy
     */
    public java.lang.String getSelectedBy() {
        return selectedBy;
    }


    /**
     * Sets the selectedBy value for this ProfileReply.
     * 
     * @param selectedBy
     */
    public void setSelectedBy(java.lang.String selectedBy) {
        this.selectedBy = selectedBy;
    }


    /**
     * Gets the name value for this ProfileReply.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ProfileReply.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the destinationQueue value for this ProfileReply.
     * 
     * @return destinationQueue
     */
    public java.lang.String getDestinationQueue() {
        return destinationQueue;
    }


    /**
     * Sets the destinationQueue value for this ProfileReply.
     * 
     * @param destinationQueue
     */
    public void setDestinationQueue(java.lang.String destinationQueue) {
        this.destinationQueue = destinationQueue;
    }


    /**
     * Gets the rulesTriggered value for this ProfileReply.
     * 
     * @return rulesTriggered
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.RuleResultItem[] getRulesTriggered() {
        return rulesTriggered;
    }


    /**
     * Sets the rulesTriggered value for this ProfileReply.
     * 
     * @param rulesTriggered
     */
    public void setRulesTriggered(org.broadleafcommerce.vendor.cybersource.service.api.RuleResultItem[] rulesTriggered) {
        this.rulesTriggered = rulesTriggered;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProfileReply)) return false;
        ProfileReply other = (ProfileReply) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.selectedBy==null && other.getSelectedBy()==null) || 
             (this.selectedBy!=null &&
              this.selectedBy.equals(other.getSelectedBy()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.destinationQueue==null && other.getDestinationQueue()==null) || 
             (this.destinationQueue!=null &&
              this.destinationQueue.equals(other.getDestinationQueue()))) &&
            ((this.rulesTriggered==null && other.getRulesTriggered()==null) || 
             (this.rulesTriggered!=null &&
              java.util.Arrays.equals(this.rulesTriggered, other.getRulesTriggered())));
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
        if (getSelectedBy() != null) {
            _hashCode += getSelectedBy().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getDestinationQueue() != null) {
            _hashCode += getDestinationQueue().hashCode();
        }
        if (getRulesTriggered() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRulesTriggered());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRulesTriggered(), i);
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
        new org.apache.axis.description.TypeDesc(ProfileReply.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ProfileReply"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("selectedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "selectedBy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destinationQueue");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "destinationQueue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rulesTriggered");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "rulesTriggered"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RuleResultItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ruleResultItem"));
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
