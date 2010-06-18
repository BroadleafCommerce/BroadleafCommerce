/**
 * InvoiceHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class InvoiceHeader  implements java.io.Serializable {
    private java.lang.String merchantDescriptor;

    private java.lang.String merchantDescriptorContact;

    private java.lang.String merchantDescriptorAlternate;

    private java.lang.String isGift;

    private java.lang.String returnsAccepted;

    private java.lang.String tenderType;

    private java.lang.String merchantVATRegistrationNumber;

    private java.lang.String purchaserOrderDate;

    private java.lang.String purchaserVATRegistrationNumber;

    private java.lang.String vatInvoiceReferenceNumber;

    private java.lang.String summaryCommodityCode;

    private java.lang.String supplierOrderReference;

    private java.lang.String userPO;

    private java.lang.String costCenter;

    private java.lang.String purchaserCode;

    private java.lang.String taxable;

    private java.lang.String amexDataTAA1;

    private java.lang.String amexDataTAA2;

    private java.lang.String amexDataTAA3;

    private java.lang.String amexDataTAA4;

    private java.lang.String invoiceDate;

    public InvoiceHeader() {
    }

    public InvoiceHeader(
           java.lang.String merchantDescriptor,
           java.lang.String merchantDescriptorContact,
           java.lang.String merchantDescriptorAlternate,
           java.lang.String isGift,
           java.lang.String returnsAccepted,
           java.lang.String tenderType,
           java.lang.String merchantVATRegistrationNumber,
           java.lang.String purchaserOrderDate,
           java.lang.String purchaserVATRegistrationNumber,
           java.lang.String vatInvoiceReferenceNumber,
           java.lang.String summaryCommodityCode,
           java.lang.String supplierOrderReference,
           java.lang.String userPO,
           java.lang.String costCenter,
           java.lang.String purchaserCode,
           java.lang.String taxable,
           java.lang.String amexDataTAA1,
           java.lang.String amexDataTAA2,
           java.lang.String amexDataTAA3,
           java.lang.String amexDataTAA4,
           java.lang.String invoiceDate) {
           this.merchantDescriptor = merchantDescriptor;
           this.merchantDescriptorContact = merchantDescriptorContact;
           this.merchantDescriptorAlternate = merchantDescriptorAlternate;
           this.isGift = isGift;
           this.returnsAccepted = returnsAccepted;
           this.tenderType = tenderType;
           this.merchantVATRegistrationNumber = merchantVATRegistrationNumber;
           this.purchaserOrderDate = purchaserOrderDate;
           this.purchaserVATRegistrationNumber = purchaserVATRegistrationNumber;
           this.vatInvoiceReferenceNumber = vatInvoiceReferenceNumber;
           this.summaryCommodityCode = summaryCommodityCode;
           this.supplierOrderReference = supplierOrderReference;
           this.userPO = userPO;
           this.costCenter = costCenter;
           this.purchaserCode = purchaserCode;
           this.taxable = taxable;
           this.amexDataTAA1 = amexDataTAA1;
           this.amexDataTAA2 = amexDataTAA2;
           this.amexDataTAA3 = amexDataTAA3;
           this.amexDataTAA4 = amexDataTAA4;
           this.invoiceDate = invoiceDate;
    }


    /**
     * Gets the merchantDescriptor value for this InvoiceHeader.
     * 
     * @return merchantDescriptor
     */
    public java.lang.String getMerchantDescriptor() {
        return merchantDescriptor;
    }


    /**
     * Sets the merchantDescriptor value for this InvoiceHeader.
     * 
     * @param merchantDescriptor
     */
    public void setMerchantDescriptor(java.lang.String merchantDescriptor) {
        this.merchantDescriptor = merchantDescriptor;
    }


    /**
     * Gets the merchantDescriptorContact value for this InvoiceHeader.
     * 
     * @return merchantDescriptorContact
     */
    public java.lang.String getMerchantDescriptorContact() {
        return merchantDescriptorContact;
    }


    /**
     * Sets the merchantDescriptorContact value for this InvoiceHeader.
     * 
     * @param merchantDescriptorContact
     */
    public void setMerchantDescriptorContact(java.lang.String merchantDescriptorContact) {
        this.merchantDescriptorContact = merchantDescriptorContact;
    }


    /**
     * Gets the merchantDescriptorAlternate value for this InvoiceHeader.
     * 
     * @return merchantDescriptorAlternate
     */
    public java.lang.String getMerchantDescriptorAlternate() {
        return merchantDescriptorAlternate;
    }


    /**
     * Sets the merchantDescriptorAlternate value for this InvoiceHeader.
     * 
     * @param merchantDescriptorAlternate
     */
    public void setMerchantDescriptorAlternate(java.lang.String merchantDescriptorAlternate) {
        this.merchantDescriptorAlternate = merchantDescriptorAlternate;
    }


    /**
     * Gets the isGift value for this InvoiceHeader.
     * 
     * @return isGift
     */
    public java.lang.String getIsGift() {
        return isGift;
    }


    /**
     * Sets the isGift value for this InvoiceHeader.
     * 
     * @param isGift
     */
    public void setIsGift(java.lang.String isGift) {
        this.isGift = isGift;
    }


    /**
     * Gets the returnsAccepted value for this InvoiceHeader.
     * 
     * @return returnsAccepted
     */
    public java.lang.String getReturnsAccepted() {
        return returnsAccepted;
    }


    /**
     * Sets the returnsAccepted value for this InvoiceHeader.
     * 
     * @param returnsAccepted
     */
    public void setReturnsAccepted(java.lang.String returnsAccepted) {
        this.returnsAccepted = returnsAccepted;
    }


    /**
     * Gets the tenderType value for this InvoiceHeader.
     * 
     * @return tenderType
     */
    public java.lang.String getTenderType() {
        return tenderType;
    }


    /**
     * Sets the tenderType value for this InvoiceHeader.
     * 
     * @param tenderType
     */
    public void setTenderType(java.lang.String tenderType) {
        this.tenderType = tenderType;
    }


    /**
     * Gets the merchantVATRegistrationNumber value for this InvoiceHeader.
     * 
     * @return merchantVATRegistrationNumber
     */
    public java.lang.String getMerchantVATRegistrationNumber() {
        return merchantVATRegistrationNumber;
    }


    /**
     * Sets the merchantVATRegistrationNumber value for this InvoiceHeader.
     * 
     * @param merchantVATRegistrationNumber
     */
    public void setMerchantVATRegistrationNumber(java.lang.String merchantVATRegistrationNumber) {
        this.merchantVATRegistrationNumber = merchantVATRegistrationNumber;
    }


    /**
     * Gets the purchaserOrderDate value for this InvoiceHeader.
     * 
     * @return purchaserOrderDate
     */
    public java.lang.String getPurchaserOrderDate() {
        return purchaserOrderDate;
    }


    /**
     * Sets the purchaserOrderDate value for this InvoiceHeader.
     * 
     * @param purchaserOrderDate
     */
    public void setPurchaserOrderDate(java.lang.String purchaserOrderDate) {
        this.purchaserOrderDate = purchaserOrderDate;
    }


    /**
     * Gets the purchaserVATRegistrationNumber value for this InvoiceHeader.
     * 
     * @return purchaserVATRegistrationNumber
     */
    public java.lang.String getPurchaserVATRegistrationNumber() {
        return purchaserVATRegistrationNumber;
    }


    /**
     * Sets the purchaserVATRegistrationNumber value for this InvoiceHeader.
     * 
     * @param purchaserVATRegistrationNumber
     */
    public void setPurchaserVATRegistrationNumber(java.lang.String purchaserVATRegistrationNumber) {
        this.purchaserVATRegistrationNumber = purchaserVATRegistrationNumber;
    }


    /**
     * Gets the vatInvoiceReferenceNumber value for this InvoiceHeader.
     * 
     * @return vatInvoiceReferenceNumber
     */
    public java.lang.String getVatInvoiceReferenceNumber() {
        return vatInvoiceReferenceNumber;
    }


    /**
     * Sets the vatInvoiceReferenceNumber value for this InvoiceHeader.
     * 
     * @param vatInvoiceReferenceNumber
     */
    public void setVatInvoiceReferenceNumber(java.lang.String vatInvoiceReferenceNumber) {
        this.vatInvoiceReferenceNumber = vatInvoiceReferenceNumber;
    }


    /**
     * Gets the summaryCommodityCode value for this InvoiceHeader.
     * 
     * @return summaryCommodityCode
     */
    public java.lang.String getSummaryCommodityCode() {
        return summaryCommodityCode;
    }


    /**
     * Sets the summaryCommodityCode value for this InvoiceHeader.
     * 
     * @param summaryCommodityCode
     */
    public void setSummaryCommodityCode(java.lang.String summaryCommodityCode) {
        this.summaryCommodityCode = summaryCommodityCode;
    }


    /**
     * Gets the supplierOrderReference value for this InvoiceHeader.
     * 
     * @return supplierOrderReference
     */
    public java.lang.String getSupplierOrderReference() {
        return supplierOrderReference;
    }


    /**
     * Sets the supplierOrderReference value for this InvoiceHeader.
     * 
     * @param supplierOrderReference
     */
    public void setSupplierOrderReference(java.lang.String supplierOrderReference) {
        this.supplierOrderReference = supplierOrderReference;
    }


    /**
     * Gets the userPO value for this InvoiceHeader.
     * 
     * @return userPO
     */
    public java.lang.String getUserPO() {
        return userPO;
    }


    /**
     * Sets the userPO value for this InvoiceHeader.
     * 
     * @param userPO
     */
    public void setUserPO(java.lang.String userPO) {
        this.userPO = userPO;
    }


    /**
     * Gets the costCenter value for this InvoiceHeader.
     * 
     * @return costCenter
     */
    public java.lang.String getCostCenter() {
        return costCenter;
    }


    /**
     * Sets the costCenter value for this InvoiceHeader.
     * 
     * @param costCenter
     */
    public void setCostCenter(java.lang.String costCenter) {
        this.costCenter = costCenter;
    }


    /**
     * Gets the purchaserCode value for this InvoiceHeader.
     * 
     * @return purchaserCode
     */
    public java.lang.String getPurchaserCode() {
        return purchaserCode;
    }


    /**
     * Sets the purchaserCode value for this InvoiceHeader.
     * 
     * @param purchaserCode
     */
    public void setPurchaserCode(java.lang.String purchaserCode) {
        this.purchaserCode = purchaserCode;
    }


    /**
     * Gets the taxable value for this InvoiceHeader.
     * 
     * @return taxable
     */
    public java.lang.String getTaxable() {
        return taxable;
    }


    /**
     * Sets the taxable value for this InvoiceHeader.
     * 
     * @param taxable
     */
    public void setTaxable(java.lang.String taxable) {
        this.taxable = taxable;
    }


    /**
     * Gets the amexDataTAA1 value for this InvoiceHeader.
     * 
     * @return amexDataTAA1
     */
    public java.lang.String getAmexDataTAA1() {
        return amexDataTAA1;
    }


    /**
     * Sets the amexDataTAA1 value for this InvoiceHeader.
     * 
     * @param amexDataTAA1
     */
    public void setAmexDataTAA1(java.lang.String amexDataTAA1) {
        this.amexDataTAA1 = amexDataTAA1;
    }


    /**
     * Gets the amexDataTAA2 value for this InvoiceHeader.
     * 
     * @return amexDataTAA2
     */
    public java.lang.String getAmexDataTAA2() {
        return amexDataTAA2;
    }


    /**
     * Sets the amexDataTAA2 value for this InvoiceHeader.
     * 
     * @param amexDataTAA2
     */
    public void setAmexDataTAA2(java.lang.String amexDataTAA2) {
        this.amexDataTAA2 = amexDataTAA2;
    }


    /**
     * Gets the amexDataTAA3 value for this InvoiceHeader.
     * 
     * @return amexDataTAA3
     */
    public java.lang.String getAmexDataTAA3() {
        return amexDataTAA3;
    }


    /**
     * Sets the amexDataTAA3 value for this InvoiceHeader.
     * 
     * @param amexDataTAA3
     */
    public void setAmexDataTAA3(java.lang.String amexDataTAA3) {
        this.amexDataTAA3 = amexDataTAA3;
    }


    /**
     * Gets the amexDataTAA4 value for this InvoiceHeader.
     * 
     * @return amexDataTAA4
     */
    public java.lang.String getAmexDataTAA4() {
        return amexDataTAA4;
    }


    /**
     * Sets the amexDataTAA4 value for this InvoiceHeader.
     * 
     * @param amexDataTAA4
     */
    public void setAmexDataTAA4(java.lang.String amexDataTAA4) {
        this.amexDataTAA4 = amexDataTAA4;
    }


    /**
     * Gets the invoiceDate value for this InvoiceHeader.
     * 
     * @return invoiceDate
     */
    public java.lang.String getInvoiceDate() {
        return invoiceDate;
    }


    /**
     * Sets the invoiceDate value for this InvoiceHeader.
     * 
     * @param invoiceDate
     */
    public void setInvoiceDate(java.lang.String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InvoiceHeader)) return false;
        InvoiceHeader other = (InvoiceHeader) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchantDescriptor==null && other.getMerchantDescriptor()==null) || 
             (this.merchantDescriptor!=null &&
              this.merchantDescriptor.equals(other.getMerchantDescriptor()))) &&
            ((this.merchantDescriptorContact==null && other.getMerchantDescriptorContact()==null) || 
             (this.merchantDescriptorContact!=null &&
              this.merchantDescriptorContact.equals(other.getMerchantDescriptorContact()))) &&
            ((this.merchantDescriptorAlternate==null && other.getMerchantDescriptorAlternate()==null) || 
             (this.merchantDescriptorAlternate!=null &&
              this.merchantDescriptorAlternate.equals(other.getMerchantDescriptorAlternate()))) &&
            ((this.isGift==null && other.getIsGift()==null) || 
             (this.isGift!=null &&
              this.isGift.equals(other.getIsGift()))) &&
            ((this.returnsAccepted==null && other.getReturnsAccepted()==null) || 
             (this.returnsAccepted!=null &&
              this.returnsAccepted.equals(other.getReturnsAccepted()))) &&
            ((this.tenderType==null && other.getTenderType()==null) || 
             (this.tenderType!=null &&
              this.tenderType.equals(other.getTenderType()))) &&
            ((this.merchantVATRegistrationNumber==null && other.getMerchantVATRegistrationNumber()==null) || 
             (this.merchantVATRegistrationNumber!=null &&
              this.merchantVATRegistrationNumber.equals(other.getMerchantVATRegistrationNumber()))) &&
            ((this.purchaserOrderDate==null && other.getPurchaserOrderDate()==null) || 
             (this.purchaserOrderDate!=null &&
              this.purchaserOrderDate.equals(other.getPurchaserOrderDate()))) &&
            ((this.purchaserVATRegistrationNumber==null && other.getPurchaserVATRegistrationNumber()==null) || 
             (this.purchaserVATRegistrationNumber!=null &&
              this.purchaserVATRegistrationNumber.equals(other.getPurchaserVATRegistrationNumber()))) &&
            ((this.vatInvoiceReferenceNumber==null && other.getVatInvoiceReferenceNumber()==null) || 
             (this.vatInvoiceReferenceNumber!=null &&
              this.vatInvoiceReferenceNumber.equals(other.getVatInvoiceReferenceNumber()))) &&
            ((this.summaryCommodityCode==null && other.getSummaryCommodityCode()==null) || 
             (this.summaryCommodityCode!=null &&
              this.summaryCommodityCode.equals(other.getSummaryCommodityCode()))) &&
            ((this.supplierOrderReference==null && other.getSupplierOrderReference()==null) || 
             (this.supplierOrderReference!=null &&
              this.supplierOrderReference.equals(other.getSupplierOrderReference()))) &&
            ((this.userPO==null && other.getUserPO()==null) || 
             (this.userPO!=null &&
              this.userPO.equals(other.getUserPO()))) &&
            ((this.costCenter==null && other.getCostCenter()==null) || 
             (this.costCenter!=null &&
              this.costCenter.equals(other.getCostCenter()))) &&
            ((this.purchaserCode==null && other.getPurchaserCode()==null) || 
             (this.purchaserCode!=null &&
              this.purchaserCode.equals(other.getPurchaserCode()))) &&
            ((this.taxable==null && other.getTaxable()==null) || 
             (this.taxable!=null &&
              this.taxable.equals(other.getTaxable()))) &&
            ((this.amexDataTAA1==null && other.getAmexDataTAA1()==null) || 
             (this.amexDataTAA1!=null &&
              this.amexDataTAA1.equals(other.getAmexDataTAA1()))) &&
            ((this.amexDataTAA2==null && other.getAmexDataTAA2()==null) || 
             (this.amexDataTAA2!=null &&
              this.amexDataTAA2.equals(other.getAmexDataTAA2()))) &&
            ((this.amexDataTAA3==null && other.getAmexDataTAA3()==null) || 
             (this.amexDataTAA3!=null &&
              this.amexDataTAA3.equals(other.getAmexDataTAA3()))) &&
            ((this.amexDataTAA4==null && other.getAmexDataTAA4()==null) || 
             (this.amexDataTAA4!=null &&
              this.amexDataTAA4.equals(other.getAmexDataTAA4()))) &&
            ((this.invoiceDate==null && other.getInvoiceDate()==null) || 
             (this.invoiceDate!=null &&
              this.invoiceDate.equals(other.getInvoiceDate())));
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
        if (getMerchantDescriptor() != null) {
            _hashCode += getMerchantDescriptor().hashCode();
        }
        if (getMerchantDescriptorContact() != null) {
            _hashCode += getMerchantDescriptorContact().hashCode();
        }
        if (getMerchantDescriptorAlternate() != null) {
            _hashCode += getMerchantDescriptorAlternate().hashCode();
        }
        if (getIsGift() != null) {
            _hashCode += getIsGift().hashCode();
        }
        if (getReturnsAccepted() != null) {
            _hashCode += getReturnsAccepted().hashCode();
        }
        if (getTenderType() != null) {
            _hashCode += getTenderType().hashCode();
        }
        if (getMerchantVATRegistrationNumber() != null) {
            _hashCode += getMerchantVATRegistrationNumber().hashCode();
        }
        if (getPurchaserOrderDate() != null) {
            _hashCode += getPurchaserOrderDate().hashCode();
        }
        if (getPurchaserVATRegistrationNumber() != null) {
            _hashCode += getPurchaserVATRegistrationNumber().hashCode();
        }
        if (getVatInvoiceReferenceNumber() != null) {
            _hashCode += getVatInvoiceReferenceNumber().hashCode();
        }
        if (getSummaryCommodityCode() != null) {
            _hashCode += getSummaryCommodityCode().hashCode();
        }
        if (getSupplierOrderReference() != null) {
            _hashCode += getSupplierOrderReference().hashCode();
        }
        if (getUserPO() != null) {
            _hashCode += getUserPO().hashCode();
        }
        if (getCostCenter() != null) {
            _hashCode += getCostCenter().hashCode();
        }
        if (getPurchaserCode() != null) {
            _hashCode += getPurchaserCode().hashCode();
        }
        if (getTaxable() != null) {
            _hashCode += getTaxable().hashCode();
        }
        if (getAmexDataTAA1() != null) {
            _hashCode += getAmexDataTAA1().hashCode();
        }
        if (getAmexDataTAA2() != null) {
            _hashCode += getAmexDataTAA2().hashCode();
        }
        if (getAmexDataTAA3() != null) {
            _hashCode += getAmexDataTAA3().hashCode();
        }
        if (getAmexDataTAA4() != null) {
            _hashCode += getAmexDataTAA4().hashCode();
        }
        if (getInvoiceDate() != null) {
            _hashCode += getInvoiceDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InvoiceHeader.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "InvoiceHeader"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDescriptor");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDescriptor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDescriptorContact");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDescriptorContact"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDescriptorAlternate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDescriptorAlternate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isGift");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "isGift"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnsAccepted");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "returnsAccepted"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("tenderType");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "tenderType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantVATRegistrationNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantVATRegistrationNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaserOrderDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaserOrderDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaserVATRegistrationNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaserVATRegistrationNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vatInvoiceReferenceNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "vatInvoiceReferenceNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("summaryCommodityCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "summaryCommodityCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("supplierOrderReference");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "supplierOrderReference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userPO");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "userPO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("costCenter");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "costCenter"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaserCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaserCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxable");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "taxable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amexDataTAA1");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amexDataTAA1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amexDataTAA2");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amexDataTAA2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amexDataTAA3");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amexDataTAA3"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amexDataTAA4");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amexDataTAA4"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invoiceDate");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "invoiceDate"));
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
