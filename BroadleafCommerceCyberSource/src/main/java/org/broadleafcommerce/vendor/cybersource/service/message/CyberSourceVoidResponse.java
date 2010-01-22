/**
 * VoidReply.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.message;

import org.broadleafcommerce.util.money.Money;

public class CyberSourceVoidResponse  implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private java.math.BigInteger reasonCode;
    private java.lang.String requestDateTime;
    private Money amount;

    /**
     * Gets the reasonCode value for this VoidReply.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this VoidReply.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the requestDateTime value for this VoidReply.
     * 
     * @return requestDateTime
     */
    public java.lang.String getRequestDateTime() {
        return requestDateTime;
    }


    /**
     * Sets the requestDateTime value for this VoidReply.
     * 
     * @param requestDateTime
     */
    public void setRequestDateTime(java.lang.String requestDateTime) {
        this.requestDateTime = requestDateTime;
    }


    /**
     * Gets the amount value for this VoidReply.
     * 
     * @return amount
     */
    public Money getAmount() {
        return amount;
    }


    /**
     * Sets the amount value for this VoidReply.
     * 
     * @param amount
     */
    public void setAmount(Money amount) {
        this.amount = amount;
    }

}
