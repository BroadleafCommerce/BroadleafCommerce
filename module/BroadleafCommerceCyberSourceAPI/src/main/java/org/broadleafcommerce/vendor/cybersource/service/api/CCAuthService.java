/**
 * CCAuthService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class CCAuthService  implements java.io.Serializable {
    private java.lang.String cavv;

    private java.lang.String cavvAlgorithm;

    private java.lang.String commerceIndicator;

    private java.lang.String eciRaw;

    private java.lang.String xid;

    private java.lang.String reconciliationID;

    private java.lang.String avsLevel;

    private java.lang.String fxQuoteID;

    private java.lang.String returnAuthRecord;

    private java.lang.String authType;

    private java.lang.String verbalAuthCode;

    private java.lang.String billPayment;

    private java.lang.String authenticationXID;

    private java.lang.String authorizationXID;

    private java.lang.String industryDatatype;

    private java.lang.String traceNumber;

    private java.lang.String checksumKey;

    private java.lang.String aggregatorID;

    private java.lang.String splitTenderIndicator;

    private java.lang.String veresEnrolled;

    private java.lang.String paresStatus;

    private java.lang.String linkToRequest;

    private java.lang.String run;  // attribute

    public CCAuthService() {
    }

    public CCAuthService(
           java.lang.String cavv,
           java.lang.String cavvAlgorithm,
           java.lang.String commerceIndicator,
           java.lang.String eciRaw,
           java.lang.String xid,
           java.lang.String reconciliationID,
           java.lang.String avsLevel,
           java.lang.String fxQuoteID,
           java.lang.String returnAuthRecord,
           java.lang.String authType,
           java.lang.String verbalAuthCode,
           java.lang.String billPayment,
           java.lang.String authenticationXID,
           java.lang.String authorizationXID,
           java.lang.String industryDatatype,
           java.lang.String traceNumber,
           java.lang.String checksumKey,
           java.lang.String aggregatorID,
           java.lang.String splitTenderIndicator,
           java.lang.String veresEnrolled,
           java.lang.String paresStatus,
           java.lang.String linkToRequest,
           java.lang.String run) {
           this.cavv = cavv;
           this.cavvAlgorithm = cavvAlgorithm;
           this.commerceIndicator = commerceIndicator;
           this.eciRaw = eciRaw;
           this.xid = xid;
           this.reconciliationID = reconciliationID;
           this.avsLevel = avsLevel;
           this.fxQuoteID = fxQuoteID;
           this.returnAuthRecord = returnAuthRecord;
           this.authType = authType;
           this.verbalAuthCode = verbalAuthCode;
           this.billPayment = billPayment;
           this.authenticationXID = authenticationXID;
           this.authorizationXID = authorizationXID;
           this.industryDatatype = industryDatatype;
           this.traceNumber = traceNumber;
           this.checksumKey = checksumKey;
           this.aggregatorID = aggregatorID;
           this.splitTenderIndicator = splitTenderIndicator;
           this.veresEnrolled = veresEnrolled;
           this.paresStatus = paresStatus;
           this.linkToRequest = linkToRequest;
           this.run = run;
    }


    /**
     * Gets the cavv value for this CCAuthService.
     * 
     * @return cavv
     */
    public java.lang.String getCavv() {
        return cavv;
    }


    /**
     * Sets the cavv value for this CCAuthService.
     * 
     * @param cavv
     */
    public void setCavv(java.lang.String cavv) {
        this.cavv = cavv;
    }


    /**
     * Gets the cavvAlgorithm value for this CCAuthService.
     * 
     * @return cavvAlgorithm
     */
    public java.lang.String getCavvAlgorithm() {
        return cavvAlgorithm;
    }


    /**
     * Sets the cavvAlgorithm value for this CCAuthService.
     * 
     * @param cavvAlgorithm
     */
    public void setCavvAlgorithm(java.lang.String cavvAlgorithm) {
        this.cavvAlgorithm = cavvAlgorithm;
    }


    /**
     * Gets the commerceIndicator value for this CCAuthService.
     * 
     * @return commerceIndicator
     */
    public java.lang.String getCommerceIndicator() {
        return commerceIndicator;
    }


    /**
     * Sets the commerceIndicator value for this CCAuthService.
     * 
     * @param commerceIndicator
     */
    public void setCommerceIndicator(java.lang.String commerceIndicator) {
        this.commerceIndicator = commerceIndicator;
    }


    /**
     * Gets the eciRaw value for this CCAuthService.
     * 
     * @return eciRaw
     */
    public java.lang.String getEciRaw() {
        return eciRaw;
    }


    /**
     * Sets the eciRaw value for this CCAuthService.
     * 
     * @param eciRaw
     */
    public void setEciRaw(java.lang.String eciRaw) {
        this.eciRaw = eciRaw;
    }


    /**
     * Gets the xid value for this CCAuthService.
     * 
     * @return xid
     */
    public java.lang.String getXid() {
        return xid;
    }


    /**
     * Sets the xid value for this CCAuthService.
     * 
     * @param xid
     */
    public void setXid(java.lang.String xid) {
        this.xid = xid;
    }


    /**
     * Gets the reconciliationID value for this CCAuthService.
     * 
     * @return reconciliationID
     */
    public java.lang.String getReconciliationID() {
        return reconciliationID;
    }


    /**
     * Sets the reconciliationID value for this CCAuthService.
     * 
     * @param reconciliationID
     */
    public void setReconciliationID(java.lang.String reconciliationID) {
        this.reconciliationID = reconciliationID;
    }


    /**
     * Gets the avsLevel value for this CCAuthService.
     * 
     * @return avsLevel
     */
    public java.lang.String getAvsLevel() {
        return avsLevel;
    }


    /**
     * Sets the avsLevel value for this CCAuthService.
     * 
     * @param avsLevel
     */
    public void setAvsLevel(java.lang.String avsLevel) {
        this.avsLevel = avsLevel;
    }


    /**
     * Gets the fxQuoteID value for this CCAuthService.
     * 
     * @return fxQuoteID
     */
    public java.lang.String getFxQuoteID() {
        return fxQuoteID;
    }


    /**
     * Sets the fxQuoteID value for this CCAuthService.
     * 
     * @param fxQuoteID
     */
    public void setFxQuoteID(java.lang.String fxQuoteID) {
        this.fxQuoteID = fxQuoteID;
    }


    /**
     * Gets the returnAuthRecord value for this CCAuthService.
     * 
     * @return returnAuthRecord
     */
    public java.lang.String getReturnAuthRecord() {
        return returnAuthRecord;
    }


    /**
     * Sets the returnAuthRecord value for this CCAuthService.
     * 
     * @param returnAuthRecord
     */
    public void setReturnAuthRecord(java.lang.String returnAuthRecord) {
        this.returnAuthRecord = returnAuthRecord;
    }


    /**
     * Gets the authType value for this CCAuthService.
     * 
     * @return authType
     */
    public java.lang.String getAuthType() {
        return authType;
    }


    /**
     * Sets the authType value for this CCAuthService.
     * 
     * @param authType
     */
    public void setAuthType(java.lang.String authType) {
        this.authType = authType;
    }


    /**
     * Gets the verbalAuthCode value for this CCAuthService.
     * 
     * @return verbalAuthCode
     */
    public java.lang.String getVerbalAuthCode() {
        return verbalAuthCode;
    }


    /**
     * Sets the verbalAuthCode value for this CCAuthService.
     * 
     * @param verbalAuthCode
     */
    public void setVerbalAuthCode(java.lang.String verbalAuthCode) {
        this.verbalAuthCode = verbalAuthCode;
    }


    /**
     * Gets the billPayment value for this CCAuthService.
     * 
     * @return billPayment
     */
    public java.lang.String getBillPayment() {
        return billPayment;
    }


    /**
     * Sets the billPayment value for this CCAuthService.
     * 
     * @param billPayment
     */
    public void setBillPayment(java.lang.String billPayment) {
        this.billPayment = billPayment;
    }


    /**
     * Gets the authenticationXID value for this CCAuthService.
     * 
     * @return authenticationXID
     */
    public java.lang.String getAuthenticationXID() {
        return authenticationXID;
    }


    /**
     * Sets the authenticationXID value for this CCAuthService.
     * 
     * @param authenticationXID
     */
    public void setAuthenticationXID(java.lang.String authenticationXID) {
        this.authenticationXID = authenticationXID;
    }


    /**
     * Gets the authorizationXID value for this CCAuthService.
     * 
     * @return authorizationXID
     */
    public java.lang.String getAuthorizationXID() {
        return authorizationXID;
    }


    /**
     * Sets the authorizationXID value for this CCAuthService.
     * 
     * @param authorizationXID
     */
    public void setAuthorizationXID(java.lang.String authorizationXID) {
        this.authorizationXID = authorizationXID;
    }


    /**
     * Gets the industryDatatype value for this CCAuthService.
     * 
     * @return industryDatatype
     */
    public java.lang.String getIndustryDatatype() {
        return industryDatatype;
    }


    /**
     * Sets the industryDatatype value for this CCAuthService.
     * 
     * @param industryDatatype
     */
    public void setIndustryDatatype(java.lang.String industryDatatype) {
        this.industryDatatype = industryDatatype;
    }


    /**
     * Gets the traceNumber value for this CCAuthService.
     * 
     * @return traceNumber
     */
    public java.lang.String getTraceNumber() {
        return traceNumber;
    }


    /**
     * Sets the traceNumber value for this CCAuthService.
     * 
     * @param traceNumber
     */
    public void setTraceNumber(java.lang.String traceNumber) {
        this.traceNumber = traceNumber;
    }


    /**
     * Gets the checksumKey value for this CCAuthService.
     * 
     * @return checksumKey
     */
    public java.lang.String getChecksumKey() {
        return checksumKey;
    }


    /**
     * Sets the checksumKey value for this CCAuthService.
     * 
     * @param checksumKey
     */
    public void setChecksumKey(java.lang.String checksumKey) {
        this.checksumKey = checksumKey;
    }


    /**
     * Gets the aggregatorID value for this CCAuthService.
     * 
     * @return aggregatorID
     */
    public java.lang.String getAggregatorID() {
        return aggregatorID;
    }


    /**
     * Sets the aggregatorID value for this CCAuthService.
     * 
     * @param aggregatorID
     */
    public void setAggregatorID(java.lang.String aggregatorID) {
        this.aggregatorID = aggregatorID;
    }


    /**
     * Gets the splitTenderIndicator value for this CCAuthService.
     * 
     * @return splitTenderIndicator
     */
    public java.lang.String getSplitTenderIndicator() {
        return splitTenderIndicator;
    }


    /**
     * Sets the splitTenderIndicator value for this CCAuthService.
     * 
     * @param splitTenderIndicator
     */
    public void setSplitTenderIndicator(java.lang.String splitTenderIndicator) {
        this.splitTenderIndicator = splitTenderIndicator;
    }


    /**
     * Gets the veresEnrolled value for this CCAuthService.
     * 
     * @return veresEnrolled
     */
    public java.lang.String getVeresEnrolled() {
        return veresEnrolled;
    }


    /**
     * Sets the veresEnrolled value for this CCAuthService.
     * 
     * @param veresEnrolled
     */
    public void setVeresEnrolled(java.lang.String veresEnrolled) {
        this.veresEnrolled = veresEnrolled;
    }


    /**
     * Gets the paresStatus value for this CCAuthService.
     * 
     * @return paresStatus
     */
    public java.lang.String getParesStatus() {
        return paresStatus;
    }


    /**
     * Sets the paresStatus value for this CCAuthService.
     * 
     * @param paresStatus
     */
    public void setParesStatus(java.lang.String paresStatus) {
        this.paresStatus = paresStatus;
    }


    /**
     * Gets the linkToRequest value for this CCAuthService.
     * 
     * @return linkToRequest
     */
    public java.lang.String getLinkToRequest() {
        return linkToRequest;
    }


    /**
     * Sets the linkToRequest value for this CCAuthService.
     * 
     * @param linkToRequest
     */
    public void setLinkToRequest(java.lang.String linkToRequest) {
        this.linkToRequest = linkToRequest;
    }


    /**
     * Gets the run value for this CCAuthService.
     * 
     * @return run
     */
    public java.lang.String getRun() {
        return run;
    }


    /**
     * Sets the run value for this CCAuthService.
     * 
     * @param run
     */
    public void setRun(java.lang.String run) {
        this.run = run;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CCAuthService)) return false;
        CCAuthService other = (CCAuthService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.cavv==null && other.getCavv()==null) || 
             (this.cavv!=null &&
              this.cavv.equals(other.getCavv()))) &&
            ((this.cavvAlgorithm==null && other.getCavvAlgorithm()==null) || 
             (this.cavvAlgorithm!=null &&
              this.cavvAlgorithm.equals(other.getCavvAlgorithm()))) &&
            ((this.commerceIndicator==null && other.getCommerceIndicator()==null) || 
             (this.commerceIndicator!=null &&
              this.commerceIndicator.equals(other.getCommerceIndicator()))) &&
            ((this.eciRaw==null && other.getEciRaw()==null) || 
             (this.eciRaw!=null &&
              this.eciRaw.equals(other.getEciRaw()))) &&
            ((this.xid==null && other.getXid()==null) || 
             (this.xid!=null &&
              this.xid.equals(other.getXid()))) &&
            ((this.reconciliationID==null && other.getReconciliationID()==null) || 
             (this.reconciliationID!=null &&
              this.reconciliationID.equals(other.getReconciliationID()))) &&
            ((this.avsLevel==null && other.getAvsLevel()==null) || 
             (this.avsLevel!=null &&
              this.avsLevel.equals(other.getAvsLevel()))) &&
            ((this.fxQuoteID==null && other.getFxQuoteID()==null) || 
             (this.fxQuoteID!=null &&
              this.fxQuoteID.equals(other.getFxQuoteID()))) &&
            ((this.returnAuthRecord==null && other.getReturnAuthRecord()==null) || 
             (this.returnAuthRecord!=null &&
              this.returnAuthRecord.equals(other.getReturnAuthRecord()))) &&
            ((this.authType==null && other.getAuthType()==null) || 
             (this.authType!=null &&
              this.authType.equals(other.getAuthType()))) &&
            ((this.verbalAuthCode==null && other.getVerbalAuthCode()==null) || 
             (this.verbalAuthCode!=null &&
              this.verbalAuthCode.equals(other.getVerbalAuthCode()))) &&
            ((this.billPayment==null && other.getBillPayment()==null) || 
             (this.billPayment!=null &&
              this.billPayment.equals(other.getBillPayment()))) &&
            ((this.authenticationXID==null && other.getAuthenticationXID()==null) || 
             (this.authenticationXID!=null &&
              this.authenticationXID.equals(other.getAuthenticationXID()))) &&
            ((this.authorizationXID==null && other.getAuthorizationXID()==null) || 
             (this.authorizationXID!=null &&
              this.authorizationXID.equals(other.getAuthorizationXID()))) &&
            ((this.industryDatatype==null && other.getIndustryDatatype()==null) || 
             (this.industryDatatype!=null &&
              this.industryDatatype.equals(other.getIndustryDatatype()))) &&
            ((this.traceNumber==null && other.getTraceNumber()==null) || 
             (this.traceNumber!=null &&
              this.traceNumber.equals(other.getTraceNumber()))) &&
            ((this.checksumKey==null && other.getChecksumKey()==null) || 
             (this.checksumKey!=null &&
              this.checksumKey.equals(other.getChecksumKey()))) &&
            ((this.aggregatorID==null && other.getAggregatorID()==null) || 
             (this.aggregatorID!=null &&
              this.aggregatorID.equals(other.getAggregatorID()))) &&
            ((this.splitTenderIndicator==null && other.getSplitTenderIndicator()==null) || 
             (this.splitTenderIndicator!=null &&
              this.splitTenderIndicator.equals(other.getSplitTenderIndicator()))) &&
            ((this.veresEnrolled==null && other.getVeresEnrolled()==null) || 
             (this.veresEnrolled!=null &&
              this.veresEnrolled.equals(other.getVeresEnrolled()))) &&
            ((this.paresStatus==null && other.getParesStatus()==null) || 
             (this.paresStatus!=null &&
              this.paresStatus.equals(other.getParesStatus()))) &&
            ((this.linkToRequest==null && other.getLinkToRequest()==null) || 
             (this.linkToRequest!=null &&
              this.linkToRequest.equals(other.getLinkToRequest()))) &&
            ((this.run==null && other.getRun()==null) || 
             (this.run!=null &&
              this.run.equals(other.getRun())));
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
        if (getCavv() != null) {
            _hashCode += getCavv().hashCode();
        }
        if (getCavvAlgorithm() != null) {
            _hashCode += getCavvAlgorithm().hashCode();
        }
        if (getCommerceIndicator() != null) {
            _hashCode += getCommerceIndicator().hashCode();
        }
        if (getEciRaw() != null) {
            _hashCode += getEciRaw().hashCode();
        }
        if (getXid() != null) {
            _hashCode += getXid().hashCode();
        }
        if (getReconciliationID() != null) {
            _hashCode += getReconciliationID().hashCode();
        }
        if (getAvsLevel() != null) {
            _hashCode += getAvsLevel().hashCode();
        }
        if (getFxQuoteID() != null) {
            _hashCode += getFxQuoteID().hashCode();
        }
        if (getReturnAuthRecord() != null) {
            _hashCode += getReturnAuthRecord().hashCode();
        }
        if (getAuthType() != null) {
            _hashCode += getAuthType().hashCode();
        }
        if (getVerbalAuthCode() != null) {
            _hashCode += getVerbalAuthCode().hashCode();
        }
        if (getBillPayment() != null) {
            _hashCode += getBillPayment().hashCode();
        }
        if (getAuthenticationXID() != null) {
            _hashCode += getAuthenticationXID().hashCode();
        }
        if (getAuthorizationXID() != null) {
            _hashCode += getAuthorizationXID().hashCode();
        }
        if (getIndustryDatatype() != null) {
            _hashCode += getIndustryDatatype().hashCode();
        }
        if (getTraceNumber() != null) {
            _hashCode += getTraceNumber().hashCode();
        }
        if (getChecksumKey() != null) {
            _hashCode += getChecksumKey().hashCode();
        }
        if (getAggregatorID() != null) {
            _hashCode += getAggregatorID().hashCode();
        }
        if (getSplitTenderIndicator() != null) {
            _hashCode += getSplitTenderIndicator().hashCode();
        }
        if (getVeresEnrolled() != null) {
            _hashCode += getVeresEnrolled().hashCode();
        }
        if (getParesStatus() != null) {
            _hashCode += getParesStatus().hashCode();
        }
        if (getLinkToRequest() != null) {
            _hashCode += getLinkToRequest().hashCode();
        }
        if (getRun() != null) {
            _hashCode += getRun().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CCAuthService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthService"));
        org.apache.axis.description.AttributeDesc attrField = new org.apache.axis.description.AttributeDesc();
        attrField.setFieldName("run");
        attrField.setXmlName(new javax.xml.namespace.QName("", "run"));
        attrField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean"));
        typeDesc.addFieldDesc(attrField);
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cavv");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cavv"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cavvAlgorithm");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "cavvAlgorithm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commerceIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "commerceIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eciRaw");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "eciRaw"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("xid");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "xid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reconciliationID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reconciliationID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("avsLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "avsLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxQuoteID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxQuoteID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnAuthRecord");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "returnAuthRecord"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("verbalAuthCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "verbalAuthCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authenticationXID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authenticationXID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorizationXID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "authorizationXID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("industryDatatype");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "industryDatatype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("traceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "traceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checksumKey");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "checksumKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aggregatorID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "aggregatorID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("splitTenderIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "splitTenderIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("veresEnrolled");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "veresEnrolled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paresStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paresStatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkToRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "linkToRequest"));
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
