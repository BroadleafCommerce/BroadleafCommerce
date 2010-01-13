/**
 * DecisionManagerTravelData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class DecisionManagerTravelData  implements java.io.Serializable {
    private org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg[] leg;

    private java.lang.String departureDateTime;

    private java.lang.String completeRoute;

    private java.lang.String journeyType;

    public DecisionManagerTravelData() {
    }

    public DecisionManagerTravelData(
           org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg[] leg,
           java.lang.String departureDateTime,
           java.lang.String completeRoute,
           java.lang.String journeyType) {
           this.leg = leg;
           this.departureDateTime = departureDateTime;
           this.completeRoute = completeRoute;
           this.journeyType = journeyType;
    }


    /**
     * Gets the leg value for this DecisionManagerTravelData.
     * 
     * @return leg
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg[] getLeg() {
        return leg;
    }


    /**
     * Sets the leg value for this DecisionManagerTravelData.
     * 
     * @param leg
     */
    public void setLeg(org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg[] leg) {
        this.leg = leg;
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg getLeg(int i) {
        return this.leg[i];
    }

    public void setLeg(int i, org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg _value) {
        this.leg[i] = _value;
    }


    /**
     * Gets the departureDateTime value for this DecisionManagerTravelData.
     * 
     * @return departureDateTime
     */
    public java.lang.String getDepartureDateTime() {
        return departureDateTime;
    }


    /**
     * Sets the departureDateTime value for this DecisionManagerTravelData.
     * 
     * @param departureDateTime
     */
    public void setDepartureDateTime(java.lang.String departureDateTime) {
        this.departureDateTime = departureDateTime;
    }


    /**
     * Gets the completeRoute value for this DecisionManagerTravelData.
     * 
     * @return completeRoute
     */
    public java.lang.String getCompleteRoute() {
        return completeRoute;
    }


    /**
     * Sets the completeRoute value for this DecisionManagerTravelData.
     * 
     * @param completeRoute
     */
    public void setCompleteRoute(java.lang.String completeRoute) {
        this.completeRoute = completeRoute;
    }


    /**
     * Gets the journeyType value for this DecisionManagerTravelData.
     * 
     * @return journeyType
     */
    public java.lang.String getJourneyType() {
        return journeyType;
    }


    /**
     * Sets the journeyType value for this DecisionManagerTravelData.
     * 
     * @param journeyType
     */
    public void setJourneyType(java.lang.String journeyType) {
        this.journeyType = journeyType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DecisionManagerTravelData)) return false;
        DecisionManagerTravelData other = (DecisionManagerTravelData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.leg==null && other.getLeg()==null) || 
             (this.leg!=null &&
              java.util.Arrays.equals(this.leg, other.getLeg()))) &&
            ((this.departureDateTime==null && other.getDepartureDateTime()==null) || 
             (this.departureDateTime!=null &&
              this.departureDateTime.equals(other.getDepartureDateTime()))) &&
            ((this.completeRoute==null && other.getCompleteRoute()==null) || 
             (this.completeRoute!=null &&
              this.completeRoute.equals(other.getCompleteRoute()))) &&
            ((this.journeyType==null && other.getJourneyType()==null) || 
             (this.journeyType!=null &&
              this.journeyType.equals(other.getJourneyType())));
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
        if (getLeg() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getLeg());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getLeg(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDepartureDateTime() != null) {
            _hashCode += getDepartureDateTime().hashCode();
        }
        if (getCompleteRoute() != null) {
            _hashCode += getCompleteRoute().hashCode();
        }
        if (getJourneyType() != null) {
            _hashCode += getJourneyType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DecisionManagerTravelData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManagerTravelData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("leg");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "leg"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManagerTravelLeg"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departureDateTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "departureDateTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("completeRoute");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "completeRoute"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("journeyType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "journeyType"));
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
