/**
 * TransactionProcessorLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class TransactionProcessorLocator extends org.apache.axis.client.Service implements org.broadleafcommerce.vendor.cybersource.service.api.TransactionProcessor {

/**
 * CyberSource Web Service
 */

    public TransactionProcessorLocator() {
    }


    public TransactionProcessorLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TransactionProcessorLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for portXML
    private java.lang.String portXML_address = "https://ics2wstest.ic3.com/commerce/1.x/transactionProcessor";

    public java.lang.String getportXMLAddress() {
        return portXML_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String portXMLWSDDServiceName = "portXML";

    public java.lang.String getportXMLWSDDServiceName() {
        return portXMLWSDDServiceName;
    }

    public void setportXMLWSDDServiceName(java.lang.String name) {
        portXMLWSDDServiceName = name;
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessor getportXML() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(portXML_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getportXML(endpoint);
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessor getportXML(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessorStub _stub = new org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessorStub(portAddress, this);
            _stub.setPortName(getportXMLWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setportXMLEndpointAddress(java.lang.String address) {
        portXML_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessor.class.isAssignableFrom(serviceEndpointInterface)) {
                org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessorStub _stub = new org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessorStub(new java.net.URL(portXML_address), this);
                _stub.setPortName(getportXMLWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("portXML".equals(inputPortName)) {
            return getportXML();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data:TransactionProcessor", "TransactionProcessor");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data:TransactionProcessor", "portXML"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("portXML".equals(portName)) {
            setportXMLEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
