/**
 * RecurringSubscriptionInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class RecurringSubscriptionInfo  implements java.io.Serializable {
    private java.lang.String subscriptionID;

    private java.lang.String status;

    private java.lang.String amount;

    private java.math.BigInteger numberOfPayments;

    private java.math.BigInteger numberOfPaymentsToAdd;

    private java.lang.String automaticRenew;

    private java.lang.String frequency;

    private java.lang.String startDate;

    private java.lang.String endDate;

    private java.lang.String approvalRequired;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEvent event;

    private java.lang.String billPayment;

    public RecurringSubscriptionInfo() {
    }

    public RecurringSubscriptionInfo(
           java.lang.String subscriptionID,
           java.lang.String status,
           java.lang.String amount,
           java.math.BigInteger numberOfPayments,
           java.math.BigInteger numberOfPaymentsToAdd,
           java.lang.String automaticRenew,
           java.lang.String frequency,
           java.lang.String startDate,
           java.lang.String endDate,
           java.lang.String approvalRequired,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEvent event,
           java.lang.String billPayment) {
           this.subscriptionID = subscriptionID;
           this.status = status;
           this.amount = amount;
           this.numberOfPayments = numberOfPayments;
           this.numberOfPaymentsToAdd = numberOfPaymentsToAdd;
           this.automaticRenew = automaticRenew;
           this.frequency = frequency;
           this.startDate = startDate;
           this.endDate = endDate;
           this.approvalRequired = approvalRequired;
           this.event = event;
           this.billPayment = billPayment;
    }


    /**
     * Gets the subscriptionID value for this RecurringSubscriptionInfo.
     * 
     * @return subscriptionID
     */
    public java.lang.String getSubscriptionID() {
        return subscriptionID;
    }


    /**
     * Sets the subscriptionID value for this RecurringSubscriptionInfo.
     * 
     * @param subscriptionID
     */
    public void setSubscriptionID(java.lang.String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }


    /**
     * Gets the status value for this RecurringSubscriptionInfo.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this RecurringSubscriptionInfo.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the amount value for this RecurringSubscriptionInfo.
     * 
     * @return amount
     */
    public java.lang.String getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this RecurringSubscriptionInfo.
     * 
     * @param amount
     */
    public void setAmount(java.lang.String amount) {
        this.amount = amount;
    }


    /**
     * Gets the numberOfPayments value for this RecurringSubscriptionInfo.
     * 
     * @return numberOfPayments
     */
    public java.math.BigInteger getNumberOfPayments() {
        return numberOfPayments;
    }


    /**
     * Sets the numberOfPayments value for this RecurringSubscriptionInfo.
     * 
     * @param numberOfPayments
     */
    public void setNumberOfPayments(java.math.BigInteger numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
    }


    /**
     * Gets the numberOfPaymentsToAdd value for this RecurringSubscriptionInfo.
     * 
     * @return numberOfPaymentsToAdd
     */
    public java.math.BigInteger getNumberOfPaymentsToAdd() {
        return numberOfPaymentsToAdd;
    }


    /**
     * Sets the numberOfPaymentsToAdd value for this RecurringSubscriptionInfo.
     * 
     * @param numberOfPaymentsToAdd
     */
    public void setNumberOfPaymentsToAdd(java.math.BigInteger numberOfPaymentsToAdd) {
        this.numberOfPaymentsToAdd = numberOfPaymentsToAdd;
    }


    /**
     * Gets the automaticRenew value for this RecurringSubscriptionInfo.
     * 
     * @return automaticRenew
     */
    public java.lang.String getAutomaticRenew() {
        return automaticRenew;
    }


    /**
     * Sets the automaticRenew value for this RecurringSubscriptionInfo.
     * 
     * @param automaticRenew
     */
    public void setAutomaticRenew(java.lang.String automaticRenew) {
        this.automaticRenew = automaticRenew;
    }


    /**
     * Gets the frequency value for this RecurringSubscriptionInfo.
     * 
     * @return frequency
     */
    public java.lang.String getFrequency() {
        return frequency;
    }


    /**
     * Sets the frequency value for this RecurringSubscriptionInfo.
     * 
     * @param frequency
     */
    public void setFrequency(java.lang.String frequency) {
        this.frequency = frequency;
    }


    /**
     * Gets the startDate value for this RecurringSubscriptionInfo.
     * 
     * @return startDate
     */
    public java.lang.String getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this RecurringSubscriptionInfo.
     * 
     * @param startDate
     */
    public void setStartDate(java.lang.String startDate) {
        this.startDate = startDate;
    }


    /**
     * Gets the endDate value for this RecurringSubscriptionInfo.
     * 
     * @return endDate
     */
    public java.lang.String getEndDate() {
        return endDate;
    }


    /**
     * Sets the endDate value for this RecurringSubscriptionInfo.
     * 
     * @param endDate
     */
    public void setEndDate(java.lang.String endDate) {
        this.endDate = endDate;
    }


    /**
     * Gets the approvalRequired value for this RecurringSubscriptionInfo.
     * 
     * @return approvalRequired
     */
    public java.lang.String getApprovalRequired() {
        return approvalRequired;
    }


    /**
     * Sets the approvalRequired value for this RecurringSubscriptionInfo.
     * 
     * @param approvalRequired
     */
    public void setApprovalRequired(java.lang.String approvalRequired) {
        this.approvalRequired = approvalRequired;
    }


    /**
     * Gets the event value for this RecurringSubscriptionInfo.
     * 
     * @return event
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEvent getEvent() {
        return event;
    }


    /**
     * Sets the event value for this RecurringSubscriptionInfo.
     * 
     * @param event
     */
    public void setEvent(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEvent event) {
        this.event = event;
    }


    /**
     * Gets the billPayment value for this RecurringSubscriptionInfo.
     * 
     * @return billPayment
     */
    public java.lang.String getBillPayment() {
        return billPayment;
    }


    /**
     * Sets the billPayment value for this RecurringSubscriptionInfo.
     * 
     * @param billPayment
     */
    public void setBillPayment(java.lang.String billPayment) {
        this.billPayment = billPayment;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RecurringSubscriptionInfo)) return false;
        RecurringSubscriptionInfo other = (RecurringSubscriptionInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.subscriptionID==null && other.getSubscriptionID()==null) || 
             (this.subscriptionID!=null &&
              this.subscriptionID.equals(other.getSubscriptionID()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.amount==null && other.getAmount()==null) || 
             (this.amount!=null &&
              this.amount.equals(other.getAmount()))) &&
            ((this.numberOfPayments==null && other.getNumberOfPayments()==null) || 
             (this.numberOfPayments!=null &&
              this.numberOfPayments.equals(other.getNumberOfPayments()))) &&
            ((this.numberOfPaymentsToAdd==null && other.getNumberOfPaymentsToAdd()==null) || 
             (this.numberOfPaymentsToAdd!=null &&
              this.numberOfPaymentsToAdd.equals(other.getNumberOfPaymentsToAdd()))) &&
            ((this.automaticRenew==null && other.getAutomaticRenew()==null) || 
             (this.automaticRenew!=null &&
              this.automaticRenew.equals(other.getAutomaticRenew()))) &&
            ((this.frequency==null && other.getFrequency()==null) || 
             (this.frequency!=null &&
              this.frequency.equals(other.getFrequency()))) &&
            ((this.startDate==null && other.getStartDate()==null) || 
             (this.startDate!=null &&
              this.startDate.equals(other.getStartDate()))) &&
            ((this.endDate==null && other.getEndDate()==null) || 
             (this.endDate!=null &&
              this.endDate.equals(other.getEndDate()))) &&
            ((this.approvalRequired==null && other.getApprovalRequired()==null) || 
             (this.approvalRequired!=null &&
              this.approvalRequired.equals(other.getApprovalRequired()))) &&
            ((this.event==null && other.getEvent()==null) || 
             (this.event!=null &&
              this.event.equals(other.getEvent()))) &&
            ((this.billPayment==null && other.getBillPayment()==null) || 
             (this.billPayment!=null &&
              this.billPayment.equals(other.getBillPayment())));
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
        if (getSubscriptionID() != null) {
            _hashCode += getSubscriptionID().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getAmount() != null) {
            _hashCode += getAmount().hashCode();
        }
        if (getNumberOfPayments() != null) {
            _hashCode += getNumberOfPayments().hashCode();
        }
        if (getNumberOfPaymentsToAdd() != null) {
            _hashCode += getNumberOfPaymentsToAdd().hashCode();
        }
        if (getAutomaticRenew() != null) {
            _hashCode += getAutomaticRenew().hashCode();
        }
        if (getFrequency() != null) {
            _hashCode += getFrequency().hashCode();
        }
        if (getStartDate() != null) {
            _hashCode += getStartDate().hashCode();
        }
        if (getEndDate() != null) {
            _hashCode += getEndDate().hashCode();
        }
        if (getApprovalRequired() != null) {
            _hashCode += getApprovalRequired().hashCode();
        }
        if (getEvent() != null) {
            _hashCode += getEvent().hashCode();
        }
        if (getBillPayment() != null) {
            _hashCode += getBillPayment().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RecurringSubscriptionInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RecurringSubscriptionInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscriptionID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "subscriptionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amount");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfPayments");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "numberOfPayments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("numberOfPaymentsToAdd");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "numberOfPaymentsToAdd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("automaticRenew");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "automaticRenew"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("frequency");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "frequency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "startDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "endDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvalRequired");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "approvalRequired"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("event");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "event"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionEvent"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billPayment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "billPayment"));
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
