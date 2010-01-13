/**
 * TransactionProcessor.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public interface TransactionProcessor extends javax.xml.rpc.Service {

/**
 * CyberSource Web Service
 */
    public java.lang.String getportXMLAddress();

    public org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessor getportXML() throws javax.xml.rpc.ServiceException;

    public org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessor getportXML(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
