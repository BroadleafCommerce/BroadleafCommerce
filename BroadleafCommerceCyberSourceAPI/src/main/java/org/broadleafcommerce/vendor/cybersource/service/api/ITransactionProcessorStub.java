/**
 * ITransactionProcessorStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ITransactionProcessorStub extends org.apache.axis.client.Stub implements org.broadleafcommerce.vendor.cybersource.service.api.ITransactionProcessor {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[1];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("runTransaction");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "requestMessage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RequestMessage"), org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ReplyMessage"));
        oper.setReturnClass(org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "replyMessage"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

    }

    public ITransactionProcessorStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ITransactionProcessorStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ITransactionProcessorStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
    }

    private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Address");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Address.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AFSReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.AFSReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AFSService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.AFSService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AirlineData");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.AirlineData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "amount");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankInfo");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRealTimeReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRealTimeService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRefundReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRefundService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankTransferReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BankTransferService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Batch");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Batch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BillTo");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BillTo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BML");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BML.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BoletoPaymentReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BoletoPaymentService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boolean");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BusinessRules");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.BusinessRules.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Card");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Card.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReversalReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReversalService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAutoAuthReversalReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAutoAuthReversalService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCaptureReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCaptureService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCreditReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCCreditReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCreditService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCCreditService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCDCCReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCDCCReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCDCCService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.CCDCCService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Check");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Check.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaPaymentReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaPaymentService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaRefundReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaRefundService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "dateTime");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DAVReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DAVReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DAVService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DAVService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DCC");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DCC.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManager");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DecisionManager.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManagerTravelData");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManagerTravelLeg");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DecisionManagerTravelLeg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DecisionReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DeniedPartiesMatch");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DeviceFingerprint");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DeviceFingerprint.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitMandateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitMandateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitRefundReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitRefundService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitValidateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitValidateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECAuthenticateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECAuthenticateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECCreditReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ECCreditReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECCreditService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ECCreditService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECDebitReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ECDebitReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECDebitService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ECDebitService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ExportReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ExportReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ExportService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ExportService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FaultDetails");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.FaultDetails.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundingTotals");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundTransfer");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.FundTransfer.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXQuote");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.FXQuote.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXRatesReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.FXRatesReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXRatesService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.FXRatesService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "GECC");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.GECC.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Installment");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Installment.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "InvoiceHeader");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.InvoiceHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Item");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Item.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "JPO");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.JPO.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Leg");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Leg.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "MerchantDefinedData");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.MerchantDefinedData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "MerchantSecureData");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.MerchantSecureData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "OtherTax");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.OtherTax.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthEnrollReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthEnrollService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthValidateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthValidateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPal");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPal.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthorizationReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthorizationService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthReversalReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthReversalService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalButtonCreateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalButtonCreateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreateAgreementReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreateAgreementService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreditReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreditService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoCaptureReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoCaptureService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoRefTransactionReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoRefTransactionService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcDoPaymentReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcDoPaymentService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcGetDetailsReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }
    private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcGetDetailsService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcOrderSetupReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcOrderSetupService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcSetReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcSetService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPaymentReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPaymentService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedPaymentReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedPaymentService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedUpdateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedUpdateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalRefundReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalRefundService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalUpdateAgreementReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalUpdateAgreementService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionCreateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionCreateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionEvent");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEvent.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionEventUpdateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionEventUpdateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionRetrieveReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionRetrieveService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionUpdateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionUpdateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReversalReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReversalService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitValidateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitValidateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Pos");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Pos.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ProfileReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ProfileReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PurchaseTotals");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RecurringSubscriptionInfo");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RecurringSubscriptionInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ReplyMessage");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ReplyReserved");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ReplyReserved.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RequestMessage");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RequestReserved");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RiskUpdateReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RiskUpdateService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RuleResultItem");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RuleResultItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RuleResultItems");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.RuleResultItem[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RuleResultItem");
            qName2 = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ruleResultItem");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ShipFrom");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ShipFrom.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ShipTo");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.ShipTo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Subscription");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.Subscription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "TaxReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.TaxReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "TaxReplyItem");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.TaxReplyItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "TaxService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.TaxService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "UCAF");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.UCAF.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "VoidReply");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.VoidReply.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "VoidService");
            cachedSerQNames.add(qName);
            cls = org.broadleafcommerce.vendor.cybersource.service.api.VoidService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage runTransaction(org.broadleafcommerce.vendor.cybersource.service.api.RequestMessage input) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("runTransaction");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "runTransaction"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {input});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage) org.apache.axis.utils.JavaUtils.convert(_resp, org.broadleafcommerce.vendor.cybersource.service.api.ReplyMessage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
