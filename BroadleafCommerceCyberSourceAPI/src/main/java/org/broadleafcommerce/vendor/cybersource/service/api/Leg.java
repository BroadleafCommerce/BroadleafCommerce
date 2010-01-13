/**
 * Leg.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class Leg  implements java.io.Serializable {
    private java.lang.String carrierCode;

    private java.lang.String flightNumber;

    private java.lang.String originatingAirportCode;

    private java.lang.String _class;

    private java.lang.String stopoverCode;

    private java.lang.String departureDate;

    private java.lang.String destination;

    private java.lang.String fareBasis;

    private java.lang.String departTax;

    private java.lang.String conjunctionTicket;

    private java.lang.String exchangeTicket;

    private java.lang.String couponNumber;

    private java.lang.String departureTime;

    private java.lang.String departureTimeSegment;

    private java.lang.String arrivalTime;

    private java.lang.String arrivalTimeSegment;

    private java.lang.String endorsementsRestrictions;

    private java.math.BigInteger id;  // attribute

    public Leg() {
    }

    public Leg(
           java.lang.String carrierCode,
           java.lang.String flightNumber,
           java.lang.String originatingAirportCode,
           java.lang.String _class,
           java.lang.String stopoverCode,
           java.lang.String departureDate,
           java.lang.String destination,
           java.lang.String fareBasis,
           java.lang.String departTax,
           java.lang.String conjunctionTicket,
           java.lang.String exchangeTicket,
           java.lang.String couponNumber,
           java.lang.String departureTime,
           java.lang.String departureTimeSegment,
           java.lang.String arrivalTime,
           java.lang.String arrivalTimeSegment,
           java.lang.String endorsementsRestrictions,
           java.math.BigInteger id) {
           this.carrierCode = carrierCode;
           this.flightNumber = flightNumber;
           this.originatingAirportCode = originatingAirportCode;
           this._class = _class;
           this.stopoverCode = stopoverCode;
           this.departureDate = departureDate;
           this.destination = destination;
           this.fareBasis = fareBasis;
           this.departTax = departTax;
           this.conjunctionTicket = conjunctionTicket;
           this.exchangeTicket = exchangeTicket;
           this.couponNumber = couponNumber;
           this.departureTime = departureTime;
           this.departureTimeSegment = departureTimeSegment;
           this.arrivalTime = arrivalTime;
           this.arrivalTimeSegment = arrivalTimeSegment;
           this.endorsementsRestrictions = endorsementsRestrictions;
           this.id = id;
    }


    /**
     * Gets the carrierCode value for this Leg.
     * 
     * @return carrierCode
     */
    public java.lang.String getCarrierCode() {
        return carrierCode;
    }


    /**
     * Sets the carrierCode value for this Leg.
     * 
     * @param carrierCode
     */
    public void setCarrierCode(java.lang.String carrierCode) {
        this.carrierCode = carrierCode;
    }


    /**
     * Gets the flightNumber value for this Leg.
     * 
     * @return flightNumber
     */
    public java.lang.String getFlightNumber() {
        return flightNumber;
    }


    /**
     * Sets the flightNumber value for this Leg.
     * 
     * @param flightNumber
     */
    public void setFlightNumber(java.lang.String flightNumber) {
        this.flightNumber = flightNumber;
    }


    /**
     * Gets the originatingAirportCode value for this Leg.
     * 
     * @return originatingAirportCode
     */
    public java.lang.String getOriginatingAirportCode() {
        return originatingAirportCode;
    }


    /**
     * Sets the originatingAirportCode value for this Leg.
     * 
     * @param originatingAirportCode
     */
    public void setOriginatingAirportCode(java.lang.String originatingAirportCode) {
        this.originatingAirportCode = originatingAirportCode;
    }


    /**
     * Gets the _class value for this Leg.
     * 
     * @return _class
     */
    public java.lang.String get_class() {
        return _class;
    }


    /**
     * Sets the _class value for this Leg.
     * 
     * @param _class
     */
    public void set_class(java.lang.String _class) {
        this._class = _class;
    }


    /**
     * Gets the stopoverCode value for this Leg.
     * 
     * @return stopoverCode
     */
    public java.lang.String getStopoverCode() {
        return stopoverCode;
    }


    /**
     * Sets the stopoverCode value for this Leg.
     * 
     * @param stopoverCode
     */
    public void setStopoverCode(java.lang.String stopoverCode) {
        this.stopoverCode = stopoverCode;
    }


    /**
     * Gets the departureDate value for this Leg.
     * 
     * @return departureDate
     */
    public java.lang.String getDepartureDate() {
        return departureDate;
    }


    /**
     * Sets the departureDate value for this Leg.
     * 
     * @param departureDate
     */
    public void setDepartureDate(java.lang.String departureDate) {
        this.departureDate = departureDate;
    }


    /**
     * Gets the destination value for this Leg.
     * 
     * @return destination
     */
    public java.lang.String getDestination() {
        return destination;
    }


    /**
     * Sets the destination value for this Leg.
     * 
     * @param destination
     */
    public void setDestination(java.lang.String destination) {
        this.destination = destination;
    }


    /**
     * Gets the fareBasis value for this Leg.
     * 
     * @return fareBasis
     */
    public java.lang.String getFareBasis() {
        return fareBasis;
    }


    /**
     * Sets the fareBasis value for this Leg.
     * 
     * @param fareBasis
     */
    public void setFareBasis(java.lang.String fareBasis) {
        this.fareBasis = fareBasis;
    }


    /**
     * Gets the departTax value for this Leg.
     * 
     * @return departTax
     */
    public java.lang.String getDepartTax() {
        return departTax;
    }


    /**
     * Sets the departTax value for this Leg.
     * 
     * @param departTax
     */
    public void setDepartTax(java.lang.String departTax) {
        this.departTax = departTax;
    }


    /**
     * Gets the conjunctionTicket value for this Leg.
     * 
     * @return conjunctionTicket
     */
    public java.lang.String getConjunctionTicket() {
        return conjunctionTicket;
    }


    /**
     * Sets the conjunctionTicket value for this Leg.
     * 
     * @param conjunctionTicket
     */
    public void setConjunctionTicket(java.lang.String conjunctionTicket) {
        this.conjunctionTicket = conjunctionTicket;
    }


    /**
     * Gets the exchangeTicket value for this Leg.
     * 
     * @return exchangeTicket
     */
    public java.lang.String getExchangeTicket() {
        return exchangeTicket;
    }


    /**
     * Sets the exchangeTicket value for this Leg.
     * 
     * @param exchangeTicket
     */
    public void setExchangeTicket(java.lang.String exchangeTicket) {
        this.exchangeTicket = exchangeTicket;
    }


    /**
     * Gets the couponNumber value for this Leg.
     * 
     * @return couponNumber
     */
    public java.lang.String getCouponNumber() {
        return couponNumber;
    }


    /**
     * Sets the couponNumber value for this Leg.
     * 
     * @param couponNumber
     */
    public void setCouponNumber(java.lang.String couponNumber) {
        this.couponNumber = couponNumber;
    }


    /**
     * Gets the departureTime value for this Leg.
     * 
     * @return departureTime
     */
    public java.lang.String getDepartureTime() {
        return departureTime;
    }


    /**
     * Sets the departureTime value for this Leg.
     * 
     * @param departureTime
     */
    public void setDepartureTime(java.lang.String departureTime) {
        this.departureTime = departureTime;
    }


    /**
     * Gets the departureTimeSegment value for this Leg.
     * 
     * @return departureTimeSegment
     */
    public java.lang.String getDepartureTimeSegment() {
        return departureTimeSegment;
    }


    /**
     * Sets the departureTimeSegment value for this Leg.
     * 
     * @param departureTimeSegment
     */
    public void setDepartureTimeSegment(java.lang.String departureTimeSegment) {
        this.departureTimeSegment = departureTimeSegment;
    }


    /**
     * Gets the arrivalTime value for this Leg.
     * 
     * @return arrivalTime
     */
    public java.lang.String getArrivalTime() {
        return arrivalTime;
    }


    /**
     * Sets the arrivalTime value for this Leg.
     * 
     * @param arrivalTime
     */
    public void setArrivalTime(java.lang.String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


    /**
     * Gets the arrivalTimeSegment value for this Leg.
     * 
     * @return arrivalTimeSegment
     */
    public java.lang.String getArrivalTimeSegment() {
        return arrivalTimeSegment;
    }


    /**
     * Sets the arrivalTimeSegment value for this Leg.
     * 
     * @param arrivalTimeSegment
     */
    public void setArrivalTimeSegment(java.lang.String arrivalTimeSegment) {
        this.arrivalTimeSegment = arrivalTimeSegment;
    }


    /**
     * Gets the endorsementsRestrictions value for this Leg.
     * 
     * @return endorsementsRestrictions
     */
    public java.lang.String getEndorsementsRestrictions() {
        return endorsementsRestrictions;
    }


    /**
     * Sets the endorsementsRestrictions value for this Leg.
     * 
     * @param endorsementsRestrictions
     */
    public void setEndorsementsRestrictions(java.lang.String endorsementsRestrictions) {
        this.endorsementsRestrictions = endorsementsRestrictions;
    }


    /**
     * Gets the id value for this Leg.
     * 
     * @return id
     */
    public java.math.BigInteger getId() {
        return id;
    }


    /**
     * Sets the id value for this Leg.
     * 
     * @param id
     */
    public void setId(java.math.BigInteger id) {
        this.id = id;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Leg)) return false;
        Leg other = (Leg) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.carrierCode==null && other.getCarrierCode()==null) || 
             (this.carrierCode!=null &&
              this.carrierCode.equals(other.getCarrierCode()))) &&
            ((this.flightNumber==null && other.getFlightNumber()==null) || 
             (this.flightNumber!=null &&
              this.flightNumber.equals(other.getFlightNumber()))) &&
            ((this.originatingAirportCode==null && other.getOriginatingAirportCode()==null) || 
             (this.originatingAirportCode!=null &&
              this.originatingAirportCode.equals(other.getOriginatingAirportCode()))) &&
            ((this._class==null && other.get_class()==null) || 
             (this._class!=null &&
              this._class.equals(other.get_class()))) &&
            ((this.stopoverCode==null && other.getStopoverCode()==null) || 
             (this.stopoverCode!=null &&
              this.stopoverCode.equals(other.getStopoverCode()))) &&
            ((this.departureDate==null && other.getDepartureDate()==null) || 
             (this.departureDate!=null &&
              this.departureDate.equals(other.getDepartureDate()))) &&
            ((this.destination==null && other.getDestination()==null) || 
             (this.destination!=null &&
              this.destination.equals(other.getDestination()))) &&
            ((this.fareBasis==null && other.getFareBasis()==null) || 
             (this.fareBasis!=null &&
              this.fareBasis.equals(other.getFareBasis()))) &&
            ((this.departTax==null && other.getDepartTax()==null) || 
             (this.departTax!=null &&
              this.departTax.equals(other.getDepartTax()))) &&
            ((this.conjunctionTicket==null && other.getConjunctionTicket()==null) || 
             (this.conjunctionTicket!=null &&
              this.conjunctionTicket.equals(other.getConjunctionTicket()))) &&
            ((this.exchangeTicket==null && other.getExchangeTicket()==null) || 
             (this.exchangeTicket!=null &&
              this.exchangeTicket.equals(other.getExchangeTicket()))) &&
            ((this.couponNumber==null && other.getCouponNumber()==null) || 
             (this.couponNumber!=null &&
              this.couponNumber.equals(other.getCouponNumber()))) &&
            ((this.departureTime==null && other.getDepartureTime()==null) || 
             (this.departureTime!=null &&
              this.departureTime.equals(other.getDepartureTime()))) &&
            ((this.departureTimeSegment==null && other.getDepartureTimeSegment()==null) || 
             (this.departureTimeSegment!=null &&
              this.departureTimeSegment.equals(other.getDepartureTimeSegment()))) &&
            ((this.arrivalTime==null && other.getArrivalTime()==null) || 
             (this.arrivalTime!=null &&
              this.arrivalTime.equals(other.getArrivalTime()))) &&
            ((this.arrivalTimeSegment==null && other.getArrivalTimeSegment()==null) || 
             (this.arrivalTimeSegment!=null &&
              this.arrivalTimeSegment.equals(other.getArrivalTimeSegment()))) &&
            ((this.endorsementsRestrictions==null && other.getEndorsementsRestrictions()==null) || 
             (this.endorsementsRestrictions!=null &&
              this.endorsementsRestrictions.equals(other.getEndorsementsRestrictions()))) &&
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
        if (getCarrierCode() != null) {
            _hashCode += getCarrierCode().hashCode();
        }
        if (getFlightNumber() != null) {
            _hashCode += getFlightNumber().hashCode();
        }
        if (getOriginatingAirportCode() != null) {
            _hashCode += getOriginatingAirportCode().hashCode();
        }
        if (get_class() != null) {
            _hashCode += get_class().hashCode();
        }
        if (getStopoverCode() != null) {
            _hashCode += getStopoverCode().hashCode();
        }
        if (getDepartureDate() != null) {
            _hashCode += getDepartureDate().hashCode();
        }
        if (getDestination() != null) {
            _hashCode += getDestination().hashCode();
        }
        if (getFareBasis() != null) {
            _hashCode += getFareBasis().hashCode();
        }
        if (getDepartTax() != null) {
            _hashCode += getDepartTax().hashCode();
        }
        if (getConjunctionTicket() != null) {
            _hashCode += getConjunctionTicket().hashCode();
        }
        if (getExchangeTicket() != null) {
            _hashCode += getExchangeTicket().hashCode();
        }
        if (getCouponNumber() != null) {
            _hashCode += getCouponNumber().hashCode();
        }
        if (getDepartureTime() != null) {
            _hashCode += getDepartureTime().hashCode();
        }
        if (getDepartureTimeSegment() != null) {
            _hashCode += getDepartureTimeSegment().hashCode();
        }
        if (getArrivalTime() != null) {
            _hashCode += getArrivalTime().hashCode();
        }
        if (getArrivalTimeSegment() != null) {
            _hashCode += getArrivalTimeSegment().hashCode();
        }
        if (getEndorsementsRestrictions() != null) {
            _hashCode += getEndorsementsRestrictions().hashCode();
        }
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Leg.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Leg"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("id");
        attrField.setXmlName(new javax.xml.namespace.QName("", "id"));
        attrField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("carrierCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "carrierCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flightNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "flightNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("originatingAirportCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "originatingAirportCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_class");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "class"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("stopoverCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "stopoverCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departureDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "departureDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("destination");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "destination"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fareBasis");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fareBasis"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departTax");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "departTax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conjunctionTicket");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "conjunctionTicket"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangeTicket");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "exchangeTicket"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("couponNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "couponNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departureTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "departureTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("departureTimeSegment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "departureTimeSegment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrivalTime");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "arrivalTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrivalTimeSegment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "arrivalTimeSegment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endorsementsRestrictions");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "endorsementsRestrictions"));
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
