/**
 * RequestMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class RequestMessage  implements java.io.Serializable {
    private java.lang.String merchantID;

    private java.lang.String merchantReferenceCode;

    private java.lang.String debtIndicator;

    private java.lang.String clientLibrary;

    private java.lang.String clientLibraryVersion;

    private java.lang.String clientEnvironment;

    private java.lang.String clientSecurityLibraryVersion;

    private java.lang.String clientApplication;

    private java.lang.String clientApplicationVersion;

    private java.lang.String clientApplicationUser;

    private java.lang.String routingCode;

    private java.lang.String comments;

    private java.lang.String returnURL;

    private org.broadleafcommerce.vendor.cybersource.service.api.InvoiceHeader invoiceHeader;

    private org.broadleafcommerce.vendor.cybersource.service.api.BillTo billTo;

    private org.broadleafcommerce.vendor.cybersource.service.api.ShipTo shipTo;

    private org.broadleafcommerce.vendor.cybersource.service.api.ShipFrom shipFrom;

    private org.broadleafcommerce.vendor.cybersource.service.api.Item[] item;

    private org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals purchaseTotals;

    private org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals;

    private org.broadleafcommerce.vendor.cybersource.service.api.DCC dcc;

    private org.broadleafcommerce.vendor.cybersource.service.api.Pos pos;

    private org.broadleafcommerce.vendor.cybersource.service.api.Installment installment;

    private org.broadleafcommerce.vendor.cybersource.service.api.Card card;

    private org.broadleafcommerce.vendor.cybersource.service.api.Check check;

    private org.broadleafcommerce.vendor.cybersource.service.api.BML bml;

    private org.broadleafcommerce.vendor.cybersource.service.api.GECC gecc;

    private org.broadleafcommerce.vendor.cybersource.service.api.UCAF ucaf;

    private org.broadleafcommerce.vendor.cybersource.service.api.FundTransfer fundTransfer;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankInfo bankInfo;

    private org.broadleafcommerce.vendor.cybersource.service.api.Subscription subscription;

    private org.broadleafcommerce.vendor.cybersource.service.api.RecurringSubscriptionInfo recurringSubscriptionInfo;

    private org.broadleafcommerce.vendor.cybersource.service.api.DecisionManager decisionManager;

    private org.broadleafcommerce.vendor.cybersource.service.api.OtherTax otherTax;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPal paypal;

    private org.broadleafcommerce.vendor.cybersource.service.api.MerchantDefinedData merchantDefinedData;

    private org.broadleafcommerce.vendor.cybersource.service.api.MerchantSecureData merchantSecureData;

    private org.broadleafcommerce.vendor.cybersource.service.api.JPO jpo;

    private java.lang.String orderRequestToken;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService ccAuthService;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureService ccCaptureService;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCCreditService ccCreditService;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalService ccAuthReversalService;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalService ccAutoAuthReversalService;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCDCCService ccDCCService;

    private org.broadleafcommerce.vendor.cybersource.service.api.ECDebitService ecDebitService;

    private org.broadleafcommerce.vendor.cybersource.service.api.ECCreditService ecCreditService;

    private org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateService ecAuthenticateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollService payerAuthEnrollService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateService payerAuthValidateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.TaxService taxService;

    private org.broadleafcommerce.vendor.cybersource.service.api.AFSService afsService;

    private org.broadleafcommerce.vendor.cybersource.service.api.DAVService davService;

    private org.broadleafcommerce.vendor.cybersource.service.api.ExportService exportService;

    private org.broadleafcommerce.vendor.cybersource.service.api.FXRatesService fxRatesService;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankTransferService bankTransferService;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundService bankTransferRefundService;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeService bankTransferRealTimeService;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateService directDebitMandateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitService directDebitService;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundService directDebitRefundService;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateService directDebitValidateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateService paySubscriptionCreateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateService paySubscriptionUpdateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateService paySubscriptionEventUpdateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveService paySubscriptionRetrieveService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentService payPalPaymentService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditService payPalCreditService;

    private org.broadleafcommerce.vendor.cybersource.service.api.VoidService voidService;

    private org.broadleafcommerce.vendor.cybersource.service.api.BusinessRules businessRules;

    private org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitService pinlessDebitService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateService pinlessDebitValidateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalService pinlessDebitReversalService;

    private org.broadleafcommerce.vendor.cybersource.service.api.Batch batch;

    private org.broadleafcommerce.vendor.cybersource.service.api.AirlineData airlineData;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateService payPalButtonCreateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentService payPalPreapprovedPaymentService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateService payPalPreapprovedUpdateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateService riskUpdateService;

    private org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved[] reserved;

    private java.lang.String deviceFingerprintID;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundService payPalRefundService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalService payPalAuthReversalService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureService payPalDoCaptureService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentService payPalEcDoPaymentService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsService payPalEcGetDetailsService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetService payPalEcSetService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupService payPalEcOrderSetupService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationService payPalAuthorizationService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementService payPalUpdateAgreementService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementService payPalCreateAgreementService;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionService payPalDoRefTransactionService;

    private org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentService chinaPaymentService;

    private org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundService chinaRefundService;

    private org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentService boletoPaymentService;

    public RequestMessage() {
    }

    public RequestMessage(
           java.lang.String merchantID,
           java.lang.String merchantReferenceCode,
           java.lang.String debtIndicator,
           java.lang.String clientLibrary,
           java.lang.String clientLibraryVersion,
           java.lang.String clientEnvironment,
           java.lang.String clientSecurityLibraryVersion,
           java.lang.String clientApplication,
           java.lang.String clientApplicationVersion,
           java.lang.String clientApplicationUser,
           java.lang.String routingCode,
           java.lang.String comments,
           java.lang.String returnURL,
           org.broadleafcommerce.vendor.cybersource.service.api.InvoiceHeader invoiceHeader,
           org.broadleafcommerce.vendor.cybersource.service.api.BillTo billTo,
           org.broadleafcommerce.vendor.cybersource.service.api.ShipTo shipTo,
           org.broadleafcommerce.vendor.cybersource.service.api.ShipFrom shipFrom,
           org.broadleafcommerce.vendor.cybersource.service.api.Item[] item,
           org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals purchaseTotals,
           org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals,
           org.broadleafcommerce.vendor.cybersource.service.api.DCC dcc,
           org.broadleafcommerce.vendor.cybersource.service.api.Pos pos,
           org.broadleafcommerce.vendor.cybersource.service.api.Installment installment,
           org.broadleafcommerce.vendor.cybersource.service.api.Card card,
           org.broadleafcommerce.vendor.cybersource.service.api.Check check,
           org.broadleafcommerce.vendor.cybersource.service.api.BML bml,
           org.broadleafcommerce.vendor.cybersource.service.api.GECC gecc,
           org.broadleafcommerce.vendor.cybersource.service.api.UCAF ucaf,
           org.broadleafcommerce.vendor.cybersource.service.api.FundTransfer fundTransfer,
           org.broadleafcommerce.vendor.cybersource.service.api.BankInfo bankInfo,
           org.broadleafcommerce.vendor.cybersource.service.api.Subscription subscription,
           org.broadleafcommerce.vendor.cybersource.service.api.RecurringSubscriptionInfo recurringSubscriptionInfo,
           org.broadleafcommerce.vendor.cybersource.service.api.DecisionManager decisionManager,
           org.broadleafcommerce.vendor.cybersource.service.api.OtherTax otherTax,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPal paypal,
           org.broadleafcommerce.vendor.cybersource.service.api.MerchantDefinedData merchantDefinedData,
           org.broadleafcommerce.vendor.cybersource.service.api.MerchantSecureData merchantSecureData,
           org.broadleafcommerce.vendor.cybersource.service.api.JPO jpo,
           java.lang.String orderRequestToken,
           org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService ccAuthService,
           org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureService ccCaptureService,
           org.broadleafcommerce.vendor.cybersource.service.api.CCCreditService ccCreditService,
           org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalService ccAuthReversalService,
           org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalService ccAutoAuthReversalService,
           org.broadleafcommerce.vendor.cybersource.service.api.CCDCCService ccDCCService,
           org.broadleafcommerce.vendor.cybersource.service.api.ECDebitService ecDebitService,
           org.broadleafcommerce.vendor.cybersource.service.api.ECCreditService ecCreditService,
           org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateService ecAuthenticateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollService payerAuthEnrollService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateService payerAuthValidateService,
           org.broadleafcommerce.vendor.cybersource.service.api.TaxService taxService,
           org.broadleafcommerce.vendor.cybersource.service.api.AFSService afsService,
           org.broadleafcommerce.vendor.cybersource.service.api.DAVService davService,
           org.broadleafcommerce.vendor.cybersource.service.api.ExportService exportService,
           org.broadleafcommerce.vendor.cybersource.service.api.FXRatesService fxRatesService,
           org.broadleafcommerce.vendor.cybersource.service.api.BankTransferService bankTransferService,
           org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundService bankTransferRefundService,
           org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeService bankTransferRealTimeService,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateService directDebitMandateService,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitService directDebitService,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundService directDebitRefundService,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateService directDebitValidateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateService paySubscriptionCreateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateService paySubscriptionUpdateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateService paySubscriptionEventUpdateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveService paySubscriptionRetrieveService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentService payPalPaymentService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditService payPalCreditService,
           org.broadleafcommerce.vendor.cybersource.service.api.VoidService voidService,
           org.broadleafcommerce.vendor.cybersource.service.api.BusinessRules businessRules,
           org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitService pinlessDebitService,
           org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateService pinlessDebitValidateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalService pinlessDebitReversalService,
           org.broadleafcommerce.vendor.cybersource.service.api.Batch batch,
           org.broadleafcommerce.vendor.cybersource.service.api.AirlineData airlineData,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateService payPalButtonCreateService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentService payPalPreapprovedPaymentService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateService payPalPreapprovedUpdateService,
           org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateService riskUpdateService,
           org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved[] reserved,
           java.lang.String deviceFingerprintID,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundService payPalRefundService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalService payPalAuthReversalService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureService payPalDoCaptureService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentService payPalEcDoPaymentService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsService payPalEcGetDetailsService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetService payPalEcSetService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupService payPalEcOrderSetupService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationService payPalAuthorizationService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementService payPalUpdateAgreementService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementService payPalCreateAgreementService,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionService payPalDoRefTransactionService,
           org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentService chinaPaymentService,
           org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundService chinaRefundService,
           org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentService boletoPaymentService) {
           this.merchantID = merchantID;
           this.merchantReferenceCode = merchantReferenceCode;
           this.debtIndicator = debtIndicator;
           this.clientLibrary = clientLibrary;
           this.clientLibraryVersion = clientLibraryVersion;
           this.clientEnvironment = clientEnvironment;
           this.clientSecurityLibraryVersion = clientSecurityLibraryVersion;
           this.clientApplication = clientApplication;
           this.clientApplicationVersion = clientApplicationVersion;
           this.clientApplicationUser = clientApplicationUser;
           this.routingCode = routingCode;
           this.comments = comments;
           this.returnURL = returnURL;
           this.invoiceHeader = invoiceHeader;
           this.billTo = billTo;
           this.shipTo = shipTo;
           this.shipFrom = shipFrom;
           this.item = item;
           this.purchaseTotals = purchaseTotals;
           this.fundingTotals = fundingTotals;
           this.dcc = dcc;
           this.pos = pos;
           this.installment = installment;
           this.card = card;
           this.check = check;
           this.bml = bml;
           this.gecc = gecc;
           this.ucaf = ucaf;
           this.fundTransfer = fundTransfer;
           this.bankInfo = bankInfo;
           this.subscription = subscription;
           this.recurringSubscriptionInfo = recurringSubscriptionInfo;
           this.decisionManager = decisionManager;
           this.otherTax = otherTax;
           this.paypal = paypal;
           this.merchantDefinedData = merchantDefinedData;
           this.merchantSecureData = merchantSecureData;
           this.jpo = jpo;
           this.orderRequestToken = orderRequestToken;
           this.ccAuthService = ccAuthService;
           this.ccCaptureService = ccCaptureService;
           this.ccCreditService = ccCreditService;
           this.ccAuthReversalService = ccAuthReversalService;
           this.ccAutoAuthReversalService = ccAutoAuthReversalService;
           this.ccDCCService = ccDCCService;
           this.ecDebitService = ecDebitService;
           this.ecCreditService = ecCreditService;
           this.ecAuthenticateService = ecAuthenticateService;
           this.payerAuthEnrollService = payerAuthEnrollService;
           this.payerAuthValidateService = payerAuthValidateService;
           this.taxService = taxService;
           this.afsService = afsService;
           this.davService = davService;
           this.exportService = exportService;
           this.fxRatesService = fxRatesService;
           this.bankTransferService = bankTransferService;
           this.bankTransferRefundService = bankTransferRefundService;
           this.bankTransferRealTimeService = bankTransferRealTimeService;
           this.directDebitMandateService = directDebitMandateService;
           this.directDebitService = directDebitService;
           this.directDebitRefundService = directDebitRefundService;
           this.directDebitValidateService = directDebitValidateService;
           this.paySubscriptionCreateService = paySubscriptionCreateService;
           this.paySubscriptionUpdateService = paySubscriptionUpdateService;
           this.paySubscriptionEventUpdateService = paySubscriptionEventUpdateService;
           this.paySubscriptionRetrieveService = paySubscriptionRetrieveService;
           this.payPalPaymentService = payPalPaymentService;
           this.payPalCreditService = payPalCreditService;
           this.voidService = voidService;
           this.businessRules = businessRules;
           this.pinlessDebitService = pinlessDebitService;
           this.pinlessDebitValidateService = pinlessDebitValidateService;
           this.pinlessDebitReversalService = pinlessDebitReversalService;
           this.batch = batch;
           this.airlineData = airlineData;
           this.payPalButtonCreateService = payPalButtonCreateService;
           this.payPalPreapprovedPaymentService = payPalPreapprovedPaymentService;
           this.payPalPreapprovedUpdateService = payPalPreapprovedUpdateService;
           this.riskUpdateService = riskUpdateService;
           this.reserved = reserved;
           this.deviceFingerprintID = deviceFingerprintID;
           this.payPalRefundService = payPalRefundService;
           this.payPalAuthReversalService = payPalAuthReversalService;
           this.payPalDoCaptureService = payPalDoCaptureService;
           this.payPalEcDoPaymentService = payPalEcDoPaymentService;
           this.payPalEcGetDetailsService = payPalEcGetDetailsService;
           this.payPalEcSetService = payPalEcSetService;
           this.payPalEcOrderSetupService = payPalEcOrderSetupService;
           this.payPalAuthorizationService = payPalAuthorizationService;
           this.payPalUpdateAgreementService = payPalUpdateAgreementService;
           this.payPalCreateAgreementService = payPalCreateAgreementService;
           this.payPalDoRefTransactionService = payPalDoRefTransactionService;
           this.chinaPaymentService = chinaPaymentService;
           this.chinaRefundService = chinaRefundService;
           this.boletoPaymentService = boletoPaymentService;
    }


    /**
     * Gets the merchantID value for this RequestMessage.
     * 
     * @return merchantID
     */
    public java.lang.String getMerchantID() {
        return merchantID;
    }


    /**
     * Sets the merchantID value for this RequestMessage.
     * 
     * @param merchantID
     */
    public void setMerchantID(java.lang.String merchantID) {
        this.merchantID = merchantID;
    }


    /**
     * Gets the merchantReferenceCode value for this RequestMessage.
     * 
     * @return merchantReferenceCode
     */
    public java.lang.String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }


    /**
     * Sets the merchantReferenceCode value for this RequestMessage.
     * 
     * @param merchantReferenceCode
     */
    public void setMerchantReferenceCode(java.lang.String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }


    /**
     * Gets the debtIndicator value for this RequestMessage.
     * 
     * @return debtIndicator
     */
    public java.lang.String getDebtIndicator() {
        return debtIndicator;
    }


    /**
     * Sets the debtIndicator value for this RequestMessage.
     * 
     * @param debtIndicator
     */
    public void setDebtIndicator(java.lang.String debtIndicator) {
        this.debtIndicator = debtIndicator;
    }


    /**
     * Gets the clientLibrary value for this RequestMessage.
     * 
     * @return clientLibrary
     */
    public java.lang.String getClientLibrary() {
        return clientLibrary;
    }


    /**
     * Sets the clientLibrary value for this RequestMessage.
     * 
     * @param clientLibrary
     */
    public void setClientLibrary(java.lang.String clientLibrary) {
        this.clientLibrary = clientLibrary;
    }


    /**
     * Gets the clientLibraryVersion value for this RequestMessage.
     * 
     * @return clientLibraryVersion
     */
    public java.lang.String getClientLibraryVersion() {
        return clientLibraryVersion;
    }


    /**
     * Sets the clientLibraryVersion value for this RequestMessage.
     * 
     * @param clientLibraryVersion
     */
    public void setClientLibraryVersion(java.lang.String clientLibraryVersion) {
        this.clientLibraryVersion = clientLibraryVersion;
    }


    /**
     * Gets the clientEnvironment value for this RequestMessage.
     * 
     * @return clientEnvironment
     */
    public java.lang.String getClientEnvironment() {
        return clientEnvironment;
    }


    /**
     * Sets the clientEnvironment value for this RequestMessage.
     * 
     * @param clientEnvironment
     */
    public void setClientEnvironment(java.lang.String clientEnvironment) {
        this.clientEnvironment = clientEnvironment;
    }


    /**
     * Gets the clientSecurityLibraryVersion value for this RequestMessage.
     * 
     * @return clientSecurityLibraryVersion
     */
    public java.lang.String getClientSecurityLibraryVersion() {
        return clientSecurityLibraryVersion;
    }


    /**
     * Sets the clientSecurityLibraryVersion value for this RequestMessage.
     * 
     * @param clientSecurityLibraryVersion
     */
    public void setClientSecurityLibraryVersion(java.lang.String clientSecurityLibraryVersion) {
        this.clientSecurityLibraryVersion = clientSecurityLibraryVersion;
    }


    /**
     * Gets the clientApplication value for this RequestMessage.
     * 
     * @return clientApplication
     */
    public java.lang.String getClientApplication() {
        return clientApplication;
    }


    /**
     * Sets the clientApplication value for this RequestMessage.
     * 
     * @param clientApplication
     */
    public void setClientApplication(java.lang.String clientApplication) {
        this.clientApplication = clientApplication;
    }


    /**
     * Gets the clientApplicationVersion value for this RequestMessage.
     * 
     * @return clientApplicationVersion
     */
    public java.lang.String getClientApplicationVersion() {
        return clientApplicationVersion;
    }


    /**
     * Sets the clientApplicationVersion value for this RequestMessage.
     * 
     * @param clientApplicationVersion
     */
    public void setClientApplicationVersion(java.lang.String clientApplicationVersion) {
        this.clientApplicationVersion = clientApplicationVersion;
    }


    /**
     * Gets the clientApplicationUser value for this RequestMessage.
     * 
     * @return clientApplicationUser
     */
    public java.lang.String getClientApplicationUser() {
        return clientApplicationUser;
    }


    /**
     * Sets the clientApplicationUser value for this RequestMessage.
     * 
     * @param clientApplicationUser
     */
    public void setClientApplicationUser(java.lang.String clientApplicationUser) {
        this.clientApplicationUser = clientApplicationUser;
    }


    /**
     * Gets the routingCode value for this RequestMessage.
     * 
     * @return routingCode
     */
    public java.lang.String getRoutingCode() {
        return routingCode;
    }


    /**
     * Sets the routingCode value for this RequestMessage.
     * 
     * @param routingCode
     */
    public void setRoutingCode(java.lang.String routingCode) {
        this.routingCode = routingCode;
    }


    /**
     * Gets the comments value for this RequestMessage.
     * 
     * @return comments
     */
    public java.lang.String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this RequestMessage.
     * 
     * @param comments
     */
    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }


    /**
     * Gets the returnURL value for this RequestMessage.
     * 
     * @return returnURL
     */
    public java.lang.String getReturnURL() {
        return returnURL;
    }


    /**
     * Sets the returnURL value for this RequestMessage.
     * 
     * @param returnURL
     */
    public void setReturnURL(java.lang.String returnURL) {
        this.returnURL = returnURL;
    }


    /**
     * Gets the invoiceHeader value for this RequestMessage.
     * 
     * @return invoiceHeader
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.InvoiceHeader getInvoiceHeader() {
        return invoiceHeader;
    }


    /**
     * Sets the invoiceHeader value for this RequestMessage.
     * 
     * @param invoiceHeader
     */
    public void setInvoiceHeader(org.broadleafcommerce.vendor.cybersource.service.api.InvoiceHeader invoiceHeader) {
        this.invoiceHeader = invoiceHeader;
    }


    /**
     * Gets the billTo value for this RequestMessage.
     * 
     * @return billTo
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BillTo getBillTo() {
        return billTo;
    }


    /**
     * Sets the billTo value for this RequestMessage.
     * 
     * @param billTo
     */
    public void setBillTo(org.broadleafcommerce.vendor.cybersource.service.api.BillTo billTo) {
        this.billTo = billTo;
    }


    /**
     * Gets the shipTo value for this RequestMessage.
     * 
     * @return shipTo
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ShipTo getShipTo() {
        return shipTo;
    }


    /**
     * Sets the shipTo value for this RequestMessage.
     * 
     * @param shipTo
     */
    public void setShipTo(org.broadleafcommerce.vendor.cybersource.service.api.ShipTo shipTo) {
        this.shipTo = shipTo;
    }


    /**
     * Gets the shipFrom value for this RequestMessage.
     * 
     * @return shipFrom
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ShipFrom getShipFrom() {
        return shipFrom;
    }


    /**
     * Sets the shipFrom value for this RequestMessage.
     * 
     * @param shipFrom
     */
    public void setShipFrom(org.broadleafcommerce.vendor.cybersource.service.api.ShipFrom shipFrom) {
        this.shipFrom = shipFrom;
    }


    /**
     * Gets the item value for this RequestMessage.
     * 
     * @return item
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Item[] getItem() {
        return item;
    }


    /**
     * Sets the item value for this RequestMessage.
     * 
     * @param item
     */
    public void setItem(org.broadleafcommerce.vendor.cybersource.service.api.Item[] item) {
        this.item = item;
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.Item getItem(int i) {
        return this.item[i];
    }

    public void setItem(int i, org.broadleafcommerce.vendor.cybersource.service.api.Item _value) {
        this.item[i] = _value;
    }


    /**
     * Gets the purchaseTotals value for this RequestMessage.
     * 
     * @return purchaseTotals
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals getPurchaseTotals() {
        return purchaseTotals;
    }


    /**
     * Sets the purchaseTotals value for this RequestMessage.
     * 
     * @param purchaseTotals
     */
    public void setPurchaseTotals(org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals purchaseTotals) {
        this.purchaseTotals = purchaseTotals;
    }


    /**
     * Gets the fundingTotals value for this RequestMessage.
     * 
     * @return fundingTotals
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals getFundingTotals() {
        return fundingTotals;
    }


    /**
     * Sets the fundingTotals value for this RequestMessage.
     * 
     * @param fundingTotals
     */
    public void setFundingTotals(org.broadleafcommerce.vendor.cybersource.service.api.FundingTotals fundingTotals) {
        this.fundingTotals = fundingTotals;
    }


    /**
     * Gets the dcc value for this RequestMessage.
     * 
     * @return dcc
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DCC getDcc() {
        return dcc;
    }


    /**
     * Sets the dcc value for this RequestMessage.
     * 
     * @param dcc
     */
    public void setDcc(org.broadleafcommerce.vendor.cybersource.service.api.DCC dcc) {
        this.dcc = dcc;
    }


    /**
     * Gets the pos value for this RequestMessage.
     * 
     * @return pos
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Pos getPos() {
        return pos;
    }


    /**
     * Sets the pos value for this RequestMessage.
     * 
     * @param pos
     */
    public void setPos(org.broadleafcommerce.vendor.cybersource.service.api.Pos pos) {
        this.pos = pos;
    }


    /**
     * Gets the installment value for this RequestMessage.
     * 
     * @return installment
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Installment getInstallment() {
        return installment;
    }


    /**
     * Sets the installment value for this RequestMessage.
     * 
     * @param installment
     */
    public void setInstallment(org.broadleafcommerce.vendor.cybersource.service.api.Installment installment) {
        this.installment = installment;
    }


    /**
     * Gets the card value for this RequestMessage.
     * 
     * @return card
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Card getCard() {
        return card;
    }


    /**
     * Sets the card value for this RequestMessage.
     * 
     * @param card
     */
    public void setCard(org.broadleafcommerce.vendor.cybersource.service.api.Card card) {
        this.card = card;
    }


    /**
     * Gets the check value for this RequestMessage.
     * 
     * @return check
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Check getCheck() {
        return check;
    }


    /**
     * Sets the check value for this RequestMessage.
     * 
     * @param check
     */
    public void setCheck(org.broadleafcommerce.vendor.cybersource.service.api.Check check) {
        this.check = check;
    }


    /**
     * Gets the bml value for this RequestMessage.
     * 
     * @return bml
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BML getBml() {
        return bml;
    }


    /**
     * Sets the bml value for this RequestMessage.
     * 
     * @param bml
     */
    public void setBml(org.broadleafcommerce.vendor.cybersource.service.api.BML bml) {
        this.bml = bml;
    }


    /**
     * Gets the gecc value for this RequestMessage.
     * 
     * @return gecc
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.GECC getGecc() {
        return gecc;
    }


    /**
     * Sets the gecc value for this RequestMessage.
     * 
     * @param gecc
     */
    public void setGecc(org.broadleafcommerce.vendor.cybersource.service.api.GECC gecc) {
        this.gecc = gecc;
    }


    /**
     * Gets the ucaf value for this RequestMessage.
     * 
     * @return ucaf
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.UCAF getUcaf() {
        return ucaf;
    }


    /**
     * Sets the ucaf value for this RequestMessage.
     * 
     * @param ucaf
     */
    public void setUcaf(org.broadleafcommerce.vendor.cybersource.service.api.UCAF ucaf) {
        this.ucaf = ucaf;
    }


    /**
     * Gets the fundTransfer value for this RequestMessage.
     * 
     * @return fundTransfer
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FundTransfer getFundTransfer() {
        return fundTransfer;
    }


    /**
     * Sets the fundTransfer value for this RequestMessage.
     * 
     * @param fundTransfer
     */
    public void setFundTransfer(org.broadleafcommerce.vendor.cybersource.service.api.FundTransfer fundTransfer) {
        this.fundTransfer = fundTransfer;
    }


    /**
     * Gets the bankInfo value for this RequestMessage.
     * 
     * @return bankInfo
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankInfo getBankInfo() {
        return bankInfo;
    }


    /**
     * Sets the bankInfo value for this RequestMessage.
     * 
     * @param bankInfo
     */
    public void setBankInfo(org.broadleafcommerce.vendor.cybersource.service.api.BankInfo bankInfo) {
        this.bankInfo = bankInfo;
    }


    /**
     * Gets the subscription value for this RequestMessage.
     * 
     * @return subscription
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Subscription getSubscription() {
        return subscription;
    }


    /**
     * Sets the subscription value for this RequestMessage.
     * 
     * @param subscription
     */
    public void setSubscription(org.broadleafcommerce.vendor.cybersource.service.api.Subscription subscription) {
        this.subscription = subscription;
    }


    /**
     * Gets the recurringSubscriptionInfo value for this RequestMessage.
     * 
     * @return recurringSubscriptionInfo
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.RecurringSubscriptionInfo getRecurringSubscriptionInfo() {
        return recurringSubscriptionInfo;
    }


    /**
     * Sets the recurringSubscriptionInfo value for this RequestMessage.
     * 
     * @param recurringSubscriptionInfo
     */
    public void setRecurringSubscriptionInfo(org.broadleafcommerce.vendor.cybersource.service.api.RecurringSubscriptionInfo recurringSubscriptionInfo) {
        this.recurringSubscriptionInfo = recurringSubscriptionInfo;
    }


    /**
     * Gets the decisionManager value for this RequestMessage.
     * 
     * @return decisionManager
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DecisionManager getDecisionManager() {
        return decisionManager;
    }


    /**
     * Sets the decisionManager value for this RequestMessage.
     * 
     * @param decisionManager
     */
    public void setDecisionManager(org.broadleafcommerce.vendor.cybersource.service.api.DecisionManager decisionManager) {
        this.decisionManager = decisionManager;
    }


    /**
     * Gets the otherTax value for this RequestMessage.
     * 
     * @return otherTax
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.OtherTax getOtherTax() {
        return otherTax;
    }


    /**
     * Sets the otherTax value for this RequestMessage.
     * 
     * @param otherTax
     */
    public void setOtherTax(org.broadleafcommerce.vendor.cybersource.service.api.OtherTax otherTax) {
        this.otherTax = otherTax;
    }


    /**
     * Gets the paypal value for this RequestMessage.
     * 
     * @return paypal
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPal getPaypal() {
        return paypal;
    }


    /**
     * Sets the paypal value for this RequestMessage.
     * 
     * @param paypal
     */
    public void setPaypal(org.broadleafcommerce.vendor.cybersource.service.api.PayPal paypal) {
        this.paypal = paypal;
    }


    /**
     * Gets the merchantDefinedData value for this RequestMessage.
     * 
     * @return merchantDefinedData
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.MerchantDefinedData getMerchantDefinedData() {
        return merchantDefinedData;
    }


    /**
     * Sets the merchantDefinedData value for this RequestMessage.
     * 
     * @param merchantDefinedData
     */
    public void setMerchantDefinedData(org.broadleafcommerce.vendor.cybersource.service.api.MerchantDefinedData merchantDefinedData) {
        this.merchantDefinedData = merchantDefinedData;
    }


    /**
     * Gets the merchantSecureData value for this RequestMessage.
     * 
     * @return merchantSecureData
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.MerchantSecureData getMerchantSecureData() {
        return merchantSecureData;
    }


    /**
     * Sets the merchantSecureData value for this RequestMessage.
     * 
     * @param merchantSecureData
     */
    public void setMerchantSecureData(org.broadleafcommerce.vendor.cybersource.service.api.MerchantSecureData merchantSecureData) {
        this.merchantSecureData = merchantSecureData;
    }


    /**
     * Gets the jpo value for this RequestMessage.
     * 
     * @return jpo
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.JPO getJpo() {
        return jpo;
    }


    /**
     * Sets the jpo value for this RequestMessage.
     * 
     * @param jpo
     */
    public void setJpo(org.broadleafcommerce.vendor.cybersource.service.api.JPO jpo) {
        this.jpo = jpo;
    }


    /**
     * Gets the orderRequestToken value for this RequestMessage.
     * 
     * @return orderRequestToken
     */
    public java.lang.String getOrderRequestToken() {
        return orderRequestToken;
    }


    /**
     * Sets the orderRequestToken value for this RequestMessage.
     * 
     * @param orderRequestToken
     */
    public void setOrderRequestToken(java.lang.String orderRequestToken) {
        this.orderRequestToken = orderRequestToken;
    }


    /**
     * Gets the ccAuthService value for this RequestMessage.
     * 
     * @return ccAuthService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService getCcAuthService() {
        return ccAuthService;
    }


    /**
     * Sets the ccAuthService value for this RequestMessage.
     * 
     * @param ccAuthService
     */
    public void setCcAuthService(org.broadleafcommerce.vendor.cybersource.service.api.CCAuthService ccAuthService) {
        this.ccAuthService = ccAuthService;
    }


    /**
     * Gets the ccCaptureService value for this RequestMessage.
     * 
     * @return ccCaptureService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureService getCcCaptureService() {
        return ccCaptureService;
    }


    /**
     * Sets the ccCaptureService value for this RequestMessage.
     * 
     * @param ccCaptureService
     */
    public void setCcCaptureService(org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureService ccCaptureService) {
        this.ccCaptureService = ccCaptureService;
    }


    /**
     * Gets the ccCreditService value for this RequestMessage.
     * 
     * @return ccCreditService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCCreditService getCcCreditService() {
        return ccCreditService;
    }


    /**
     * Sets the ccCreditService value for this RequestMessage.
     * 
     * @param ccCreditService
     */
    public void setCcCreditService(org.broadleafcommerce.vendor.cybersource.service.api.CCCreditService ccCreditService) {
        this.ccCreditService = ccCreditService;
    }


    /**
     * Gets the ccAuthReversalService value for this RequestMessage.
     * 
     * @return ccAuthReversalService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalService getCcAuthReversalService() {
        return ccAuthReversalService;
    }


    /**
     * Sets the ccAuthReversalService value for this RequestMessage.
     * 
     * @param ccAuthReversalService
     */
    public void setCcAuthReversalService(org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalService ccAuthReversalService) {
        this.ccAuthReversalService = ccAuthReversalService;
    }


    /**
     * Gets the ccAutoAuthReversalService value for this RequestMessage.
     * 
     * @return ccAutoAuthReversalService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalService getCcAutoAuthReversalService() {
        return ccAutoAuthReversalService;
    }


    /**
     * Sets the ccAutoAuthReversalService value for this RequestMessage.
     * 
     * @param ccAutoAuthReversalService
     */
    public void setCcAutoAuthReversalService(org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalService ccAutoAuthReversalService) {
        this.ccAutoAuthReversalService = ccAutoAuthReversalService;
    }


    /**
     * Gets the ccDCCService value for this RequestMessage.
     * 
     * @return ccDCCService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCDCCService getCcDCCService() {
        return ccDCCService;
    }


    /**
     * Sets the ccDCCService value for this RequestMessage.
     * 
     * @param ccDCCService
     */
    public void setCcDCCService(org.broadleafcommerce.vendor.cybersource.service.api.CCDCCService ccDCCService) {
        this.ccDCCService = ccDCCService;
    }


    /**
     * Gets the ecDebitService value for this RequestMessage.
     * 
     * @return ecDebitService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ECDebitService getEcDebitService() {
        return ecDebitService;
    }


    /**
     * Sets the ecDebitService value for this RequestMessage.
     * 
     * @param ecDebitService
     */
    public void setEcDebitService(org.broadleafcommerce.vendor.cybersource.service.api.ECDebitService ecDebitService) {
        this.ecDebitService = ecDebitService;
    }


    /**
     * Gets the ecCreditService value for this RequestMessage.
     * 
     * @return ecCreditService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ECCreditService getEcCreditService() {
        return ecCreditService;
    }


    /**
     * Sets the ecCreditService value for this RequestMessage.
     * 
     * @param ecCreditService
     */
    public void setEcCreditService(org.broadleafcommerce.vendor.cybersource.service.api.ECCreditService ecCreditService) {
        this.ecCreditService = ecCreditService;
    }


    /**
     * Gets the ecAuthenticateService value for this RequestMessage.
     * 
     * @return ecAuthenticateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateService getEcAuthenticateService() {
        return ecAuthenticateService;
    }


    /**
     * Sets the ecAuthenticateService value for this RequestMessage.
     * 
     * @param ecAuthenticateService
     */
    public void setEcAuthenticateService(org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateService ecAuthenticateService) {
        this.ecAuthenticateService = ecAuthenticateService;
    }


    /**
     * Gets the payerAuthEnrollService value for this RequestMessage.
     * 
     * @return payerAuthEnrollService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollService getPayerAuthEnrollService() {
        return payerAuthEnrollService;
    }


    /**
     * Sets the payerAuthEnrollService value for this RequestMessage.
     * 
     * @param payerAuthEnrollService
     */
    public void setPayerAuthEnrollService(org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollService payerAuthEnrollService) {
        this.payerAuthEnrollService = payerAuthEnrollService;
    }


    /**
     * Gets the payerAuthValidateService value for this RequestMessage.
     * 
     * @return payerAuthValidateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateService getPayerAuthValidateService() {
        return payerAuthValidateService;
    }


    /**
     * Sets the payerAuthValidateService value for this RequestMessage.
     * 
     * @param payerAuthValidateService
     */
    public void setPayerAuthValidateService(org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateService payerAuthValidateService) {
        this.payerAuthValidateService = payerAuthValidateService;
    }


    /**
     * Gets the taxService value for this RequestMessage.
     * 
     * @return taxService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.TaxService getTaxService() {
        return taxService;
    }


    /**
     * Sets the taxService value for this RequestMessage.
     * 
     * @param taxService
     */
    public void setTaxService(org.broadleafcommerce.vendor.cybersource.service.api.TaxService taxService) {
        this.taxService = taxService;
    }


    /**
     * Gets the afsService value for this RequestMessage.
     * 
     * @return afsService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.AFSService getAfsService() {
        return afsService;
    }


    /**
     * Sets the afsService value for this RequestMessage.
     * 
     * @param afsService
     */
    public void setAfsService(org.broadleafcommerce.vendor.cybersource.service.api.AFSService afsService) {
        this.afsService = afsService;
    }


    /**
     * Gets the davService value for this RequestMessage.
     * 
     * @return davService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DAVService getDavService() {
        return davService;
    }


    /**
     * Sets the davService value for this RequestMessage.
     * 
     * @param davService
     */
    public void setDavService(org.broadleafcommerce.vendor.cybersource.service.api.DAVService davService) {
        this.davService = davService;
    }


    /**
     * Gets the exportService value for this RequestMessage.
     * 
     * @return exportService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ExportService getExportService() {
        return exportService;
    }


    /**
     * Sets the exportService value for this RequestMessage.
     * 
     * @param exportService
     */
    public void setExportService(org.broadleafcommerce.vendor.cybersource.service.api.ExportService exportService) {
        this.exportService = exportService;
    }


    /**
     * Gets the fxRatesService value for this RequestMessage.
     * 
     * @return fxRatesService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FXRatesService getFxRatesService() {
        return fxRatesService;
    }


    /**
     * Sets the fxRatesService value for this RequestMessage.
     * 
     * @param fxRatesService
     */
    public void setFxRatesService(org.broadleafcommerce.vendor.cybersource.service.api.FXRatesService fxRatesService) {
        this.fxRatesService = fxRatesService;
    }


    /**
     * Gets the bankTransferService value for this RequestMessage.
     * 
     * @return bankTransferService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankTransferService getBankTransferService() {
        return bankTransferService;
    }


    /**
     * Sets the bankTransferService value for this RequestMessage.
     * 
     * @param bankTransferService
     */
    public void setBankTransferService(org.broadleafcommerce.vendor.cybersource.service.api.BankTransferService bankTransferService) {
        this.bankTransferService = bankTransferService;
    }


    /**
     * Gets the bankTransferRefundService value for this RequestMessage.
     * 
     * @return bankTransferRefundService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundService getBankTransferRefundService() {
        return bankTransferRefundService;
    }


    /**
     * Sets the bankTransferRefundService value for this RequestMessage.
     * 
     * @param bankTransferRefundService
     */
    public void setBankTransferRefundService(org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundService bankTransferRefundService) {
        this.bankTransferRefundService = bankTransferRefundService;
    }


    /**
     * Gets the bankTransferRealTimeService value for this RequestMessage.
     * 
     * @return bankTransferRealTimeService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeService getBankTransferRealTimeService() {
        return bankTransferRealTimeService;
    }


    /**
     * Sets the bankTransferRealTimeService value for this RequestMessage.
     * 
     * @param bankTransferRealTimeService
     */
    public void setBankTransferRealTimeService(org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeService bankTransferRealTimeService) {
        this.bankTransferRealTimeService = bankTransferRealTimeService;
    }


    /**
     * Gets the directDebitMandateService value for this RequestMessage.
     * 
     * @return directDebitMandateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateService getDirectDebitMandateService() {
        return directDebitMandateService;
    }


    /**
     * Sets the directDebitMandateService value for this RequestMessage.
     * 
     * @param directDebitMandateService
     */
    public void setDirectDebitMandateService(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateService directDebitMandateService) {
        this.directDebitMandateService = directDebitMandateService;
    }


    /**
     * Gets the directDebitService value for this RequestMessage.
     * 
     * @return directDebitService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitService getDirectDebitService() {
        return directDebitService;
    }


    /**
     * Sets the directDebitService value for this RequestMessage.
     * 
     * @param directDebitService
     */
    public void setDirectDebitService(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitService directDebitService) {
        this.directDebitService = directDebitService;
    }


    /**
     * Gets the directDebitRefundService value for this RequestMessage.
     * 
     * @return directDebitRefundService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundService getDirectDebitRefundService() {
        return directDebitRefundService;
    }


    /**
     * Sets the directDebitRefundService value for this RequestMessage.
     * 
     * @param directDebitRefundService
     */
    public void setDirectDebitRefundService(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundService directDebitRefundService) {
        this.directDebitRefundService = directDebitRefundService;
    }


    /**
     * Gets the directDebitValidateService value for this RequestMessage.
     * 
     * @return directDebitValidateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateService getDirectDebitValidateService() {
        return directDebitValidateService;
    }


    /**
     * Sets the directDebitValidateService value for this RequestMessage.
     * 
     * @param directDebitValidateService
     */
    public void setDirectDebitValidateService(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateService directDebitValidateService) {
        this.directDebitValidateService = directDebitValidateService;
    }


    /**
     * Gets the paySubscriptionCreateService value for this RequestMessage.
     * 
     * @return paySubscriptionCreateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateService getPaySubscriptionCreateService() {
        return paySubscriptionCreateService;
    }


    /**
     * Sets the paySubscriptionCreateService value for this RequestMessage.
     * 
     * @param paySubscriptionCreateService
     */
    public void setPaySubscriptionCreateService(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateService paySubscriptionCreateService) {
        this.paySubscriptionCreateService = paySubscriptionCreateService;
    }


    /**
     * Gets the paySubscriptionUpdateService value for this RequestMessage.
     * 
     * @return paySubscriptionUpdateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateService getPaySubscriptionUpdateService() {
        return paySubscriptionUpdateService;
    }


    /**
     * Sets the paySubscriptionUpdateService value for this RequestMessage.
     * 
     * @param paySubscriptionUpdateService
     */
    public void setPaySubscriptionUpdateService(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateService paySubscriptionUpdateService) {
        this.paySubscriptionUpdateService = paySubscriptionUpdateService;
    }


    /**
     * Gets the paySubscriptionEventUpdateService value for this RequestMessage.
     * 
     * @return paySubscriptionEventUpdateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateService getPaySubscriptionEventUpdateService() {
        return paySubscriptionEventUpdateService;
    }


    /**
     * Sets the paySubscriptionEventUpdateService value for this RequestMessage.
     * 
     * @param paySubscriptionEventUpdateService
     */
    public void setPaySubscriptionEventUpdateService(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateService paySubscriptionEventUpdateService) {
        this.paySubscriptionEventUpdateService = paySubscriptionEventUpdateService;
    }


    /**
     * Gets the paySubscriptionRetrieveService value for this RequestMessage.
     * 
     * @return paySubscriptionRetrieveService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveService getPaySubscriptionRetrieveService() {
        return paySubscriptionRetrieveService;
    }


    /**
     * Sets the paySubscriptionRetrieveService value for this RequestMessage.
     * 
     * @param paySubscriptionRetrieveService
     */
    public void setPaySubscriptionRetrieveService(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveService paySubscriptionRetrieveService) {
        this.paySubscriptionRetrieveService = paySubscriptionRetrieveService;
    }


    /**
     * Gets the payPalPaymentService value for this RequestMessage.
     * 
     * @return payPalPaymentService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentService getPayPalPaymentService() {
        return payPalPaymentService;
    }


    /**
     * Sets the payPalPaymentService value for this RequestMessage.
     * 
     * @param payPalPaymentService
     */
    public void setPayPalPaymentService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentService payPalPaymentService) {
        this.payPalPaymentService = payPalPaymentService;
    }


    /**
     * Gets the payPalCreditService value for this RequestMessage.
     * 
     * @return payPalCreditService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditService getPayPalCreditService() {
        return payPalCreditService;
    }


    /**
     * Sets the payPalCreditService value for this RequestMessage.
     * 
     * @param payPalCreditService
     */
    public void setPayPalCreditService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditService payPalCreditService) {
        this.payPalCreditService = payPalCreditService;
    }


    /**
     * Gets the voidService value for this RequestMessage.
     * 
     * @return voidService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.VoidService getVoidService() {
        return voidService;
    }


    /**
     * Sets the voidService value for this RequestMessage.
     * 
     * @param voidService
     */
    public void setVoidService(org.broadleafcommerce.vendor.cybersource.service.api.VoidService voidService) {
        this.voidService = voidService;
    }


    /**
     * Gets the businessRules value for this RequestMessage.
     * 
     * @return businessRules
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BusinessRules getBusinessRules() {
        return businessRules;
    }


    /**
     * Sets the businessRules value for this RequestMessage.
     * 
     * @param businessRules
     */
    public void setBusinessRules(org.broadleafcommerce.vendor.cybersource.service.api.BusinessRules businessRules) {
        this.businessRules = businessRules;
    }


    /**
     * Gets the pinlessDebitService value for this RequestMessage.
     * 
     * @return pinlessDebitService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitService getPinlessDebitService() {
        return pinlessDebitService;
    }


    /**
     * Sets the pinlessDebitService value for this RequestMessage.
     * 
     * @param pinlessDebitService
     */
    public void setPinlessDebitService(org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitService pinlessDebitService) {
        this.pinlessDebitService = pinlessDebitService;
    }


    /**
     * Gets the pinlessDebitValidateService value for this RequestMessage.
     * 
     * @return pinlessDebitValidateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateService getPinlessDebitValidateService() {
        return pinlessDebitValidateService;
    }


    /**
     * Sets the pinlessDebitValidateService value for this RequestMessage.
     * 
     * @param pinlessDebitValidateService
     */
    public void setPinlessDebitValidateService(org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateService pinlessDebitValidateService) {
        this.pinlessDebitValidateService = pinlessDebitValidateService;
    }


    /**
     * Gets the pinlessDebitReversalService value for this RequestMessage.
     * 
     * @return pinlessDebitReversalService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalService getPinlessDebitReversalService() {
        return pinlessDebitReversalService;
    }


    /**
     * Sets the pinlessDebitReversalService value for this RequestMessage.
     * 
     * @param pinlessDebitReversalService
     */
    public void setPinlessDebitReversalService(org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalService pinlessDebitReversalService) {
        this.pinlessDebitReversalService = pinlessDebitReversalService;
    }


    /**
     * Gets the batch value for this RequestMessage.
     * 
     * @return batch
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.Batch getBatch() {
        return batch;
    }


    /**
     * Sets the batch value for this RequestMessage.
     * 
     * @param batch
     */
    public void setBatch(org.broadleafcommerce.vendor.cybersource.service.api.Batch batch) {
        this.batch = batch;
    }


    /**
     * Gets the airlineData value for this RequestMessage.
     * 
     * @return airlineData
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.AirlineData getAirlineData() {
        return airlineData;
    }


    /**
     * Sets the airlineData value for this RequestMessage.
     * 
     * @param airlineData
     */
    public void setAirlineData(org.broadleafcommerce.vendor.cybersource.service.api.AirlineData airlineData) {
        this.airlineData = airlineData;
    }


    /**
     * Gets the payPalButtonCreateService value for this RequestMessage.
     * 
     * @return payPalButtonCreateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateService getPayPalButtonCreateService() {
        return payPalButtonCreateService;
    }


    /**
     * Sets the payPalButtonCreateService value for this RequestMessage.
     * 
     * @param payPalButtonCreateService
     */
    public void setPayPalButtonCreateService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateService payPalButtonCreateService) {
        this.payPalButtonCreateService = payPalButtonCreateService;
    }


    /**
     * Gets the payPalPreapprovedPaymentService value for this RequestMessage.
     * 
     * @return payPalPreapprovedPaymentService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentService getPayPalPreapprovedPaymentService() {
        return payPalPreapprovedPaymentService;
    }


    /**
     * Sets the payPalPreapprovedPaymentService value for this RequestMessage.
     * 
     * @param payPalPreapprovedPaymentService
     */
    public void setPayPalPreapprovedPaymentService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentService payPalPreapprovedPaymentService) {
        this.payPalPreapprovedPaymentService = payPalPreapprovedPaymentService;
    }


    /**
     * Gets the payPalPreapprovedUpdateService value for this RequestMessage.
     * 
     * @return payPalPreapprovedUpdateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateService getPayPalPreapprovedUpdateService() {
        return payPalPreapprovedUpdateService;
    }


    /**
     * Sets the payPalPreapprovedUpdateService value for this RequestMessage.
     * 
     * @param payPalPreapprovedUpdateService
     */
    public void setPayPalPreapprovedUpdateService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateService payPalPreapprovedUpdateService) {
        this.payPalPreapprovedUpdateService = payPalPreapprovedUpdateService;
    }


    /**
     * Gets the riskUpdateService value for this RequestMessage.
     * 
     * @return riskUpdateService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateService getRiskUpdateService() {
        return riskUpdateService;
    }


    /**
     * Sets the riskUpdateService value for this RequestMessage.
     * 
     * @param riskUpdateService
     */
    public void setRiskUpdateService(org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateService riskUpdateService) {
        this.riskUpdateService = riskUpdateService;
    }


    /**
     * Gets the reserved value for this RequestMessage.
     * 
     * @return reserved
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved[] getReserved() {
        return reserved;
    }


    /**
     * Sets the reserved value for this RequestMessage.
     * 
     * @param reserved
     */
    public void setReserved(org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved[] reserved) {
        this.reserved = reserved;
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved getReserved(int i) {
        return this.reserved[i];
    }

    public void setReserved(int i, org.broadleafcommerce.vendor.cybersource.service.api.RequestReserved _value) {
        this.reserved[i] = _value;
    }


    /**
     * Gets the deviceFingerprintID value for this RequestMessage.
     * 
     * @return deviceFingerprintID
     */
    public java.lang.String getDeviceFingerprintID() {
        return deviceFingerprintID;
    }


    /**
     * Sets the deviceFingerprintID value for this RequestMessage.
     * 
     * @param deviceFingerprintID
     */
    public void setDeviceFingerprintID(java.lang.String deviceFingerprintID) {
        this.deviceFingerprintID = deviceFingerprintID;
    }


    /**
     * Gets the payPalRefundService value for this RequestMessage.
     * 
     * @return payPalRefundService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundService getPayPalRefundService() {
        return payPalRefundService;
    }


    /**
     * Sets the payPalRefundService value for this RequestMessage.
     * 
     * @param payPalRefundService
     */
    public void setPayPalRefundService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundService payPalRefundService) {
        this.payPalRefundService = payPalRefundService;
    }


    /**
     * Gets the payPalAuthReversalService value for this RequestMessage.
     * 
     * @return payPalAuthReversalService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalService getPayPalAuthReversalService() {
        return payPalAuthReversalService;
    }


    /**
     * Sets the payPalAuthReversalService value for this RequestMessage.
     * 
     * @param payPalAuthReversalService
     */
    public void setPayPalAuthReversalService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalService payPalAuthReversalService) {
        this.payPalAuthReversalService = payPalAuthReversalService;
    }


    /**
     * Gets the payPalDoCaptureService value for this RequestMessage.
     * 
     * @return payPalDoCaptureService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureService getPayPalDoCaptureService() {
        return payPalDoCaptureService;
    }


    /**
     * Sets the payPalDoCaptureService value for this RequestMessage.
     * 
     * @param payPalDoCaptureService
     */
    public void setPayPalDoCaptureService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureService payPalDoCaptureService) {
        this.payPalDoCaptureService = payPalDoCaptureService;
    }


    /**
     * Gets the payPalEcDoPaymentService value for this RequestMessage.
     * 
     * @return payPalEcDoPaymentService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentService getPayPalEcDoPaymentService() {
        return payPalEcDoPaymentService;
    }


    /**
     * Sets the payPalEcDoPaymentService value for this RequestMessage.
     * 
     * @param payPalEcDoPaymentService
     */
    public void setPayPalEcDoPaymentService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentService payPalEcDoPaymentService) {
        this.payPalEcDoPaymentService = payPalEcDoPaymentService;
    }


    /**
     * Gets the payPalEcGetDetailsService value for this RequestMessage.
     * 
     * @return payPalEcGetDetailsService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsService getPayPalEcGetDetailsService() {
        return payPalEcGetDetailsService;
    }


    /**
     * Sets the payPalEcGetDetailsService value for this RequestMessage.
     * 
     * @param payPalEcGetDetailsService
     */
    public void setPayPalEcGetDetailsService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsService payPalEcGetDetailsService) {
        this.payPalEcGetDetailsService = payPalEcGetDetailsService;
    }


    /**
     * Gets the payPalEcSetService value for this RequestMessage.
     * 
     * @return payPalEcSetService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetService getPayPalEcSetService() {
        return payPalEcSetService;
    }


    /**
     * Sets the payPalEcSetService value for this RequestMessage.
     * 
     * @param payPalEcSetService
     */
    public void setPayPalEcSetService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetService payPalEcSetService) {
        this.payPalEcSetService = payPalEcSetService;
    }


    /**
     * Gets the payPalEcOrderSetupService value for this RequestMessage.
     * 
     * @return payPalEcOrderSetupService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupService getPayPalEcOrderSetupService() {
        return payPalEcOrderSetupService;
    }


    /**
     * Sets the payPalEcOrderSetupService value for this RequestMessage.
     * 
     * @param payPalEcOrderSetupService
     */
    public void setPayPalEcOrderSetupService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupService payPalEcOrderSetupService) {
        this.payPalEcOrderSetupService = payPalEcOrderSetupService;
    }


    /**
     * Gets the payPalAuthorizationService value for this RequestMessage.
     * 
     * @return payPalAuthorizationService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationService getPayPalAuthorizationService() {
        return payPalAuthorizationService;
    }


    /**
     * Sets the payPalAuthorizationService value for this RequestMessage.
     * 
     * @param payPalAuthorizationService
     */
    public void setPayPalAuthorizationService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationService payPalAuthorizationService) {
        this.payPalAuthorizationService = payPalAuthorizationService;
    }


    /**
     * Gets the payPalUpdateAgreementService value for this RequestMessage.
     * 
     * @return payPalUpdateAgreementService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementService getPayPalUpdateAgreementService() {
        return payPalUpdateAgreementService;
    }


    /**
     * Sets the payPalUpdateAgreementService value for this RequestMessage.
     * 
     * @param payPalUpdateAgreementService
     */
    public void setPayPalUpdateAgreementService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementService payPalUpdateAgreementService) {
        this.payPalUpdateAgreementService = payPalUpdateAgreementService;
    }


    /**
     * Gets the payPalCreateAgreementService value for this RequestMessage.
     * 
     * @return payPalCreateAgreementService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementService getPayPalCreateAgreementService() {
        return payPalCreateAgreementService;
    }


    /**
     * Sets the payPalCreateAgreementService value for this RequestMessage.
     * 
     * @param payPalCreateAgreementService
     */
    public void setPayPalCreateAgreementService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementService payPalCreateAgreementService) {
        this.payPalCreateAgreementService = payPalCreateAgreementService;
    }


    /**
     * Gets the payPalDoRefTransactionService value for this RequestMessage.
     * 
     * @return payPalDoRefTransactionService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionService getPayPalDoRefTransactionService() {
        return payPalDoRefTransactionService;
    }


    /**
     * Sets the payPalDoRefTransactionService value for this RequestMessage.
     * 
     * @param payPalDoRefTransactionService
     */
    public void setPayPalDoRefTransactionService(org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionService payPalDoRefTransactionService) {
        this.payPalDoRefTransactionService = payPalDoRefTransactionService;
    }


    /**
     * Gets the chinaPaymentService value for this RequestMessage.
     * 
     * @return chinaPaymentService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentService getChinaPaymentService() {
        return chinaPaymentService;
    }


    /**
     * Sets the chinaPaymentService value for this RequestMessage.
     * 
     * @param chinaPaymentService
     */
    public void setChinaPaymentService(org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentService chinaPaymentService) {
        this.chinaPaymentService = chinaPaymentService;
    }


    /**
     * Gets the chinaRefundService value for this RequestMessage.
     * 
     * @return chinaRefundService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundService getChinaRefundService() {
        return chinaRefundService;
    }


    /**
     * Sets the chinaRefundService value for this RequestMessage.
     * 
     * @param chinaRefundService
     */
    public void setChinaRefundService(org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundService chinaRefundService) {
        this.chinaRefundService = chinaRefundService;
    }


    /**
     * Gets the boletoPaymentService value for this RequestMessage.
     * 
     * @return boletoPaymentService
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentService getBoletoPaymentService() {
        return boletoPaymentService;
    }


    /**
     * Sets the boletoPaymentService value for this RequestMessage.
     * 
     * @param boletoPaymentService
     */
    public void setBoletoPaymentService(org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentService boletoPaymentService) {
        this.boletoPaymentService = boletoPaymentService;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RequestMessage)) return false;
        RequestMessage other = (RequestMessage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchantID==null && other.getMerchantID()==null) || 
             (this.merchantID!=null &&
              this.merchantID.equals(other.getMerchantID()))) &&
            ((this.merchantReferenceCode==null && other.getMerchantReferenceCode()==null) || 
             (this.merchantReferenceCode!=null &&
              this.merchantReferenceCode.equals(other.getMerchantReferenceCode()))) &&
            ((this.debtIndicator==null && other.getDebtIndicator()==null) || 
             (this.debtIndicator!=null &&
              this.debtIndicator.equals(other.getDebtIndicator()))) &&
            ((this.clientLibrary==null && other.getClientLibrary()==null) || 
             (this.clientLibrary!=null &&
              this.clientLibrary.equals(other.getClientLibrary()))) &&
            ((this.clientLibraryVersion==null && other.getClientLibraryVersion()==null) || 
             (this.clientLibraryVersion!=null &&
              this.clientLibraryVersion.equals(other.getClientLibraryVersion()))) &&
            ((this.clientEnvironment==null && other.getClientEnvironment()==null) || 
             (this.clientEnvironment!=null &&
              this.clientEnvironment.equals(other.getClientEnvironment()))) &&
            ((this.clientSecurityLibraryVersion==null && other.getClientSecurityLibraryVersion()==null) || 
             (this.clientSecurityLibraryVersion!=null &&
              this.clientSecurityLibraryVersion.equals(other.getClientSecurityLibraryVersion()))) &&
            ((this.clientApplication==null && other.getClientApplication()==null) || 
             (this.clientApplication!=null &&
              this.clientApplication.equals(other.getClientApplication()))) &&
            ((this.clientApplicationVersion==null && other.getClientApplicationVersion()==null) || 
             (this.clientApplicationVersion!=null &&
              this.clientApplicationVersion.equals(other.getClientApplicationVersion()))) &&
            ((this.clientApplicationUser==null && other.getClientApplicationUser()==null) || 
             (this.clientApplicationUser!=null &&
              this.clientApplicationUser.equals(other.getClientApplicationUser()))) &&
            ((this.routingCode==null && other.getRoutingCode()==null) || 
             (this.routingCode!=null &&
              this.routingCode.equals(other.getRoutingCode()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            ((this.returnURL==null && other.getReturnURL()==null) || 
             (this.returnURL!=null &&
              this.returnURL.equals(other.getReturnURL()))) &&
            ((this.invoiceHeader==null && other.getInvoiceHeader()==null) || 
             (this.invoiceHeader!=null &&
              this.invoiceHeader.equals(other.getInvoiceHeader()))) &&
            ((this.billTo==null && other.getBillTo()==null) || 
             (this.billTo!=null &&
              this.billTo.equals(other.getBillTo()))) &&
            ((this.shipTo==null && other.getShipTo()==null) || 
             (this.shipTo!=null &&
              this.shipTo.equals(other.getShipTo()))) &&
            ((this.shipFrom==null && other.getShipFrom()==null) || 
             (this.shipFrom!=null &&
              this.shipFrom.equals(other.getShipFrom()))) &&
            ((this.item==null && other.getItem()==null) || 
             (this.item!=null &&
              java.util.Arrays.equals(this.item, other.getItem()))) &&
            ((this.purchaseTotals==null && other.getPurchaseTotals()==null) || 
             (this.purchaseTotals!=null &&
              this.purchaseTotals.equals(other.getPurchaseTotals()))) &&
            ((this.fundingTotals==null && other.getFundingTotals()==null) || 
             (this.fundingTotals!=null &&
              this.fundingTotals.equals(other.getFundingTotals()))) &&
            ((this.dcc==null && other.getDcc()==null) || 
             (this.dcc!=null &&
              this.dcc.equals(other.getDcc()))) &&
            ((this.pos==null && other.getPos()==null) || 
             (this.pos!=null &&
              this.pos.equals(other.getPos()))) &&
            ((this.installment==null && other.getInstallment()==null) || 
             (this.installment!=null &&
              this.installment.equals(other.getInstallment()))) &&
            ((this.card==null && other.getCard()==null) || 
             (this.card!=null &&
              this.card.equals(other.getCard()))) &&
            ((this.check==null && other.getCheck()==null) || 
             (this.check!=null &&
              this.check.equals(other.getCheck()))) &&
            ((this.bml==null && other.getBml()==null) || 
             (this.bml!=null &&
              this.bml.equals(other.getBml()))) &&
            ((this.gecc==null && other.getGecc()==null) || 
             (this.gecc!=null &&
              this.gecc.equals(other.getGecc()))) &&
            ((this.ucaf==null && other.getUcaf()==null) || 
             (this.ucaf!=null &&
              this.ucaf.equals(other.getUcaf()))) &&
            ((this.fundTransfer==null && other.getFundTransfer()==null) || 
             (this.fundTransfer!=null &&
              this.fundTransfer.equals(other.getFundTransfer()))) &&
            ((this.bankInfo==null && other.getBankInfo()==null) || 
             (this.bankInfo!=null &&
              this.bankInfo.equals(other.getBankInfo()))) &&
            ((this.subscription==null && other.getSubscription()==null) || 
             (this.subscription!=null &&
              this.subscription.equals(other.getSubscription()))) &&
            ((this.recurringSubscriptionInfo==null && other.getRecurringSubscriptionInfo()==null) || 
             (this.recurringSubscriptionInfo!=null &&
              this.recurringSubscriptionInfo.equals(other.getRecurringSubscriptionInfo()))) &&
            ((this.decisionManager==null && other.getDecisionManager()==null) || 
             (this.decisionManager!=null &&
              this.decisionManager.equals(other.getDecisionManager()))) &&
            ((this.otherTax==null && other.getOtherTax()==null) || 
             (this.otherTax!=null &&
              this.otherTax.equals(other.getOtherTax()))) &&
            ((this.paypal==null && other.getPaypal()==null) || 
             (this.paypal!=null &&
              this.paypal.equals(other.getPaypal()))) &&
            ((this.merchantDefinedData==null && other.getMerchantDefinedData()==null) || 
             (this.merchantDefinedData!=null &&
              this.merchantDefinedData.equals(other.getMerchantDefinedData()))) &&
            ((this.merchantSecureData==null && other.getMerchantSecureData()==null) || 
             (this.merchantSecureData!=null &&
              this.merchantSecureData.equals(other.getMerchantSecureData()))) &&
            ((this.jpo==null && other.getJpo()==null) || 
             (this.jpo!=null &&
              this.jpo.equals(other.getJpo()))) &&
            ((this.orderRequestToken==null && other.getOrderRequestToken()==null) || 
             (this.orderRequestToken!=null &&
              this.orderRequestToken.equals(other.getOrderRequestToken()))) &&
            ((this.ccAuthService==null && other.getCcAuthService()==null) || 
             (this.ccAuthService!=null &&
              this.ccAuthService.equals(other.getCcAuthService()))) &&
            ((this.ccCaptureService==null && other.getCcCaptureService()==null) || 
             (this.ccCaptureService!=null &&
              this.ccCaptureService.equals(other.getCcCaptureService()))) &&
            ((this.ccCreditService==null && other.getCcCreditService()==null) || 
             (this.ccCreditService!=null &&
              this.ccCreditService.equals(other.getCcCreditService()))) &&
            ((this.ccAuthReversalService==null && other.getCcAuthReversalService()==null) || 
             (this.ccAuthReversalService!=null &&
              this.ccAuthReversalService.equals(other.getCcAuthReversalService()))) &&
            ((this.ccAutoAuthReversalService==null && other.getCcAutoAuthReversalService()==null) || 
             (this.ccAutoAuthReversalService!=null &&
              this.ccAutoAuthReversalService.equals(other.getCcAutoAuthReversalService()))) &&
            ((this.ccDCCService==null && other.getCcDCCService()==null) || 
             (this.ccDCCService!=null &&
              this.ccDCCService.equals(other.getCcDCCService()))) &&
            ((this.ecDebitService==null && other.getEcDebitService()==null) || 
             (this.ecDebitService!=null &&
              this.ecDebitService.equals(other.getEcDebitService()))) &&
            ((this.ecCreditService==null && other.getEcCreditService()==null) || 
             (this.ecCreditService!=null &&
              this.ecCreditService.equals(other.getEcCreditService()))) &&
            ((this.ecAuthenticateService==null && other.getEcAuthenticateService()==null) || 
             (this.ecAuthenticateService!=null &&
              this.ecAuthenticateService.equals(other.getEcAuthenticateService()))) &&
            ((this.payerAuthEnrollService==null && other.getPayerAuthEnrollService()==null) || 
             (this.payerAuthEnrollService!=null &&
              this.payerAuthEnrollService.equals(other.getPayerAuthEnrollService()))) &&
            ((this.payerAuthValidateService==null && other.getPayerAuthValidateService()==null) || 
             (this.payerAuthValidateService!=null &&
              this.payerAuthValidateService.equals(other.getPayerAuthValidateService()))) &&
            ((this.taxService==null && other.getTaxService()==null) || 
             (this.taxService!=null &&
              this.taxService.equals(other.getTaxService()))) &&
            ((this.afsService==null && other.getAfsService()==null) || 
             (this.afsService!=null &&
              this.afsService.equals(other.getAfsService()))) &&
            ((this.davService==null && other.getDavService()==null) || 
             (this.davService!=null &&
              this.davService.equals(other.getDavService()))) &&
            ((this.exportService==null && other.getExportService()==null) || 
             (this.exportService!=null &&
              this.exportService.equals(other.getExportService()))) &&
            ((this.fxRatesService==null && other.getFxRatesService()==null) || 
             (this.fxRatesService!=null &&
              this.fxRatesService.equals(other.getFxRatesService()))) &&
            ((this.bankTransferService==null && other.getBankTransferService()==null) || 
             (this.bankTransferService!=null &&
              this.bankTransferService.equals(other.getBankTransferService()))) &&
            ((this.bankTransferRefundService==null && other.getBankTransferRefundService()==null) || 
             (this.bankTransferRefundService!=null &&
              this.bankTransferRefundService.equals(other.getBankTransferRefundService()))) &&
            ((this.bankTransferRealTimeService==null && other.getBankTransferRealTimeService()==null) || 
             (this.bankTransferRealTimeService!=null &&
              this.bankTransferRealTimeService.equals(other.getBankTransferRealTimeService()))) &&
            ((this.directDebitMandateService==null && other.getDirectDebitMandateService()==null) || 
             (this.directDebitMandateService!=null &&
              this.directDebitMandateService.equals(other.getDirectDebitMandateService()))) &&
            ((this.directDebitService==null && other.getDirectDebitService()==null) || 
             (this.directDebitService!=null &&
              this.directDebitService.equals(other.getDirectDebitService()))) &&
            ((this.directDebitRefundService==null && other.getDirectDebitRefundService()==null) || 
             (this.directDebitRefundService!=null &&
              this.directDebitRefundService.equals(other.getDirectDebitRefundService()))) &&
            ((this.directDebitValidateService==null && other.getDirectDebitValidateService()==null) || 
             (this.directDebitValidateService!=null &&
              this.directDebitValidateService.equals(other.getDirectDebitValidateService()))) &&
            ((this.paySubscriptionCreateService==null && other.getPaySubscriptionCreateService()==null) || 
             (this.paySubscriptionCreateService!=null &&
              this.paySubscriptionCreateService.equals(other.getPaySubscriptionCreateService()))) &&
            ((this.paySubscriptionUpdateService==null && other.getPaySubscriptionUpdateService()==null) || 
             (this.paySubscriptionUpdateService!=null &&
              this.paySubscriptionUpdateService.equals(other.getPaySubscriptionUpdateService()))) &&
            ((this.paySubscriptionEventUpdateService==null && other.getPaySubscriptionEventUpdateService()==null) || 
             (this.paySubscriptionEventUpdateService!=null &&
              this.paySubscriptionEventUpdateService.equals(other.getPaySubscriptionEventUpdateService()))) &&
            ((this.paySubscriptionRetrieveService==null && other.getPaySubscriptionRetrieveService()==null) || 
             (this.paySubscriptionRetrieveService!=null &&
              this.paySubscriptionRetrieveService.equals(other.getPaySubscriptionRetrieveService()))) &&
            ((this.payPalPaymentService==null && other.getPayPalPaymentService()==null) || 
             (this.payPalPaymentService!=null &&
              this.payPalPaymentService.equals(other.getPayPalPaymentService()))) &&
            ((this.payPalCreditService==null && other.getPayPalCreditService()==null) || 
             (this.payPalCreditService!=null &&
              this.payPalCreditService.equals(other.getPayPalCreditService()))) &&
            ((this.voidService==null && other.getVoidService()==null) || 
             (this.voidService!=null &&
              this.voidService.equals(other.getVoidService()))) &&
            ((this.businessRules==null && other.getBusinessRules()==null) || 
             (this.businessRules!=null &&
              this.businessRules.equals(other.getBusinessRules()))) &&
            ((this.pinlessDebitService==null && other.getPinlessDebitService()==null) || 
             (this.pinlessDebitService!=null &&
              this.pinlessDebitService.equals(other.getPinlessDebitService()))) &&
            ((this.pinlessDebitValidateService==null && other.getPinlessDebitValidateService()==null) || 
             (this.pinlessDebitValidateService!=null &&
              this.pinlessDebitValidateService.equals(other.getPinlessDebitValidateService()))) &&
            ((this.pinlessDebitReversalService==null && other.getPinlessDebitReversalService()==null) || 
             (this.pinlessDebitReversalService!=null &&
              this.pinlessDebitReversalService.equals(other.getPinlessDebitReversalService()))) &&
            ((this.batch==null && other.getBatch()==null) || 
             (this.batch!=null &&
              this.batch.equals(other.getBatch()))) &&
            ((this.airlineData==null && other.getAirlineData()==null) || 
             (this.airlineData!=null &&
              this.airlineData.equals(other.getAirlineData()))) &&
            ((this.payPalButtonCreateService==null && other.getPayPalButtonCreateService()==null) || 
             (this.payPalButtonCreateService!=null &&
              this.payPalButtonCreateService.equals(other.getPayPalButtonCreateService()))) &&
            ((this.payPalPreapprovedPaymentService==null && other.getPayPalPreapprovedPaymentService()==null) || 
             (this.payPalPreapprovedPaymentService!=null &&
              this.payPalPreapprovedPaymentService.equals(other.getPayPalPreapprovedPaymentService()))) &&
            ((this.payPalPreapprovedUpdateService==null && other.getPayPalPreapprovedUpdateService()==null) || 
             (this.payPalPreapprovedUpdateService!=null &&
              this.payPalPreapprovedUpdateService.equals(other.getPayPalPreapprovedUpdateService()))) &&
            ((this.riskUpdateService==null && other.getRiskUpdateService()==null) || 
             (this.riskUpdateService!=null &&
              this.riskUpdateService.equals(other.getRiskUpdateService()))) &&
            ((this.reserved==null && other.getReserved()==null) || 
             (this.reserved!=null &&
              java.util.Arrays.equals(this.reserved, other.getReserved()))) &&
            ((this.deviceFingerprintID==null && other.getDeviceFingerprintID()==null) || 
             (this.deviceFingerprintID!=null &&
              this.deviceFingerprintID.equals(other.getDeviceFingerprintID()))) &&
            ((this.payPalRefundService==null && other.getPayPalRefundService()==null) || 
             (this.payPalRefundService!=null &&
              this.payPalRefundService.equals(other.getPayPalRefundService()))) &&
            ((this.payPalAuthReversalService==null && other.getPayPalAuthReversalService()==null) || 
             (this.payPalAuthReversalService!=null &&
              this.payPalAuthReversalService.equals(other.getPayPalAuthReversalService()))) &&
            ((this.payPalDoCaptureService==null && other.getPayPalDoCaptureService()==null) || 
             (this.payPalDoCaptureService!=null &&
              this.payPalDoCaptureService.equals(other.getPayPalDoCaptureService()))) &&
            ((this.payPalEcDoPaymentService==null && other.getPayPalEcDoPaymentService()==null) || 
             (this.payPalEcDoPaymentService!=null &&
              this.payPalEcDoPaymentService.equals(other.getPayPalEcDoPaymentService()))) &&
            ((this.payPalEcGetDetailsService==null && other.getPayPalEcGetDetailsService()==null) || 
             (this.payPalEcGetDetailsService!=null &&
              this.payPalEcGetDetailsService.equals(other.getPayPalEcGetDetailsService()))) &&
            ((this.payPalEcSetService==null && other.getPayPalEcSetService()==null) || 
             (this.payPalEcSetService!=null &&
              this.payPalEcSetService.equals(other.getPayPalEcSetService()))) &&
            ((this.payPalEcOrderSetupService==null && other.getPayPalEcOrderSetupService()==null) || 
             (this.payPalEcOrderSetupService!=null &&
              this.payPalEcOrderSetupService.equals(other.getPayPalEcOrderSetupService()))) &&
            ((this.payPalAuthorizationService==null && other.getPayPalAuthorizationService()==null) || 
             (this.payPalAuthorizationService!=null &&
              this.payPalAuthorizationService.equals(other.getPayPalAuthorizationService()))) &&
            ((this.payPalUpdateAgreementService==null && other.getPayPalUpdateAgreementService()==null) || 
             (this.payPalUpdateAgreementService!=null &&
              this.payPalUpdateAgreementService.equals(other.getPayPalUpdateAgreementService()))) &&
            ((this.payPalCreateAgreementService==null && other.getPayPalCreateAgreementService()==null) || 
             (this.payPalCreateAgreementService!=null &&
              this.payPalCreateAgreementService.equals(other.getPayPalCreateAgreementService()))) &&
            ((this.payPalDoRefTransactionService==null && other.getPayPalDoRefTransactionService()==null) || 
             (this.payPalDoRefTransactionService!=null &&
              this.payPalDoRefTransactionService.equals(other.getPayPalDoRefTransactionService()))) &&
            ((this.chinaPaymentService==null && other.getChinaPaymentService()==null) || 
             (this.chinaPaymentService!=null &&
              this.chinaPaymentService.equals(other.getChinaPaymentService()))) &&
            ((this.chinaRefundService==null && other.getChinaRefundService()==null) || 
             (this.chinaRefundService!=null &&
              this.chinaRefundService.equals(other.getChinaRefundService()))) &&
            ((this.boletoPaymentService==null && other.getBoletoPaymentService()==null) || 
             (this.boletoPaymentService!=null &&
              this.boletoPaymentService.equals(other.getBoletoPaymentService())));
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
        if (getMerchantID() != null) {
            _hashCode += getMerchantID().hashCode();
        }
        if (getMerchantReferenceCode() != null) {
            _hashCode += getMerchantReferenceCode().hashCode();
        }
        if (getDebtIndicator() != null) {
            _hashCode += getDebtIndicator().hashCode();
        }
        if (getClientLibrary() != null) {
            _hashCode += getClientLibrary().hashCode();
        }
        if (getClientLibraryVersion() != null) {
            _hashCode += getClientLibraryVersion().hashCode();
        }
        if (getClientEnvironment() != null) {
            _hashCode += getClientEnvironment().hashCode();
        }
        if (getClientSecurityLibraryVersion() != null) {
            _hashCode += getClientSecurityLibraryVersion().hashCode();
        }
        if (getClientApplication() != null) {
            _hashCode += getClientApplication().hashCode();
        }
        if (getClientApplicationVersion() != null) {
            _hashCode += getClientApplicationVersion().hashCode();
        }
        if (getClientApplicationUser() != null) {
            _hashCode += getClientApplicationUser().hashCode();
        }
        if (getRoutingCode() != null) {
            _hashCode += getRoutingCode().hashCode();
        }
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        if (getReturnURL() != null) {
            _hashCode += getReturnURL().hashCode();
        }
        if (getInvoiceHeader() != null) {
            _hashCode += getInvoiceHeader().hashCode();
        }
        if (getBillTo() != null) {
            _hashCode += getBillTo().hashCode();
        }
        if (getShipTo() != null) {
            _hashCode += getShipTo().hashCode();
        }
        if (getShipFrom() != null) {
            _hashCode += getShipFrom().hashCode();
        }
        if (getItem() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getItem());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getItem(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getPurchaseTotals() != null) {
            _hashCode += getPurchaseTotals().hashCode();
        }
        if (getFundingTotals() != null) {
            _hashCode += getFundingTotals().hashCode();
        }
        if (getDcc() != null) {
            _hashCode += getDcc().hashCode();
        }
        if (getPos() != null) {
            _hashCode += getPos().hashCode();
        }
        if (getInstallment() != null) {
            _hashCode += getInstallment().hashCode();
        }
        if (getCard() != null) {
            _hashCode += getCard().hashCode();
        }
        if (getCheck() != null) {
            _hashCode += getCheck().hashCode();
        }
        if (getBml() != null) {
            _hashCode += getBml().hashCode();
        }
        if (getGecc() != null) {
            _hashCode += getGecc().hashCode();
        }
        if (getUcaf() != null) {
            _hashCode += getUcaf().hashCode();
        }
        if (getFundTransfer() != null) {
            _hashCode += getFundTransfer().hashCode();
        }
        if (getBankInfo() != null) {
            _hashCode += getBankInfo().hashCode();
        }
        if (getSubscription() != null) {
            _hashCode += getSubscription().hashCode();
        }
        if (getRecurringSubscriptionInfo() != null) {
            _hashCode += getRecurringSubscriptionInfo().hashCode();
        }
        if (getDecisionManager() != null) {
            _hashCode += getDecisionManager().hashCode();
        }
        if (getOtherTax() != null) {
            _hashCode += getOtherTax().hashCode();
        }
        if (getPaypal() != null) {
            _hashCode += getPaypal().hashCode();
        }
        if (getMerchantDefinedData() != null) {
            _hashCode += getMerchantDefinedData().hashCode();
        }
        if (getMerchantSecureData() != null) {
            _hashCode += getMerchantSecureData().hashCode();
        }
        if (getJpo() != null) {
            _hashCode += getJpo().hashCode();
        }
        if (getOrderRequestToken() != null) {
            _hashCode += getOrderRequestToken().hashCode();
        }
        if (getCcAuthService() != null) {
            _hashCode += getCcAuthService().hashCode();
        }
        if (getCcCaptureService() != null) {
            _hashCode += getCcCaptureService().hashCode();
        }
        if (getCcCreditService() != null) {
            _hashCode += getCcCreditService().hashCode();
        }
        if (getCcAuthReversalService() != null) {
            _hashCode += getCcAuthReversalService().hashCode();
        }
        if (getCcAutoAuthReversalService() != null) {
            _hashCode += getCcAutoAuthReversalService().hashCode();
        }
        if (getCcDCCService() != null) {
            _hashCode += getCcDCCService().hashCode();
        }
        if (getEcDebitService() != null) {
            _hashCode += getEcDebitService().hashCode();
        }
        if (getEcCreditService() != null) {
            _hashCode += getEcCreditService().hashCode();
        }
        if (getEcAuthenticateService() != null) {
            _hashCode += getEcAuthenticateService().hashCode();
        }
        if (getPayerAuthEnrollService() != null) {
            _hashCode += getPayerAuthEnrollService().hashCode();
        }
        if (getPayerAuthValidateService() != null) {
            _hashCode += getPayerAuthValidateService().hashCode();
        }
        if (getTaxService() != null) {
            _hashCode += getTaxService().hashCode();
        }
        if (getAfsService() != null) {
            _hashCode += getAfsService().hashCode();
        }
        if (getDavService() != null) {
            _hashCode += getDavService().hashCode();
        }
        if (getExportService() != null) {
            _hashCode += getExportService().hashCode();
        }
        if (getFxRatesService() != null) {
            _hashCode += getFxRatesService().hashCode();
        }
        if (getBankTransferService() != null) {
            _hashCode += getBankTransferService().hashCode();
        }
        if (getBankTransferRefundService() != null) {
            _hashCode += getBankTransferRefundService().hashCode();
        }
        if (getBankTransferRealTimeService() != null) {
            _hashCode += getBankTransferRealTimeService().hashCode();
        }
        if (getDirectDebitMandateService() != null) {
            _hashCode += getDirectDebitMandateService().hashCode();
        }
        if (getDirectDebitService() != null) {
            _hashCode += getDirectDebitService().hashCode();
        }
        if (getDirectDebitRefundService() != null) {
            _hashCode += getDirectDebitRefundService().hashCode();
        }
        if (getDirectDebitValidateService() != null) {
            _hashCode += getDirectDebitValidateService().hashCode();
        }
        if (getPaySubscriptionCreateService() != null) {
            _hashCode += getPaySubscriptionCreateService().hashCode();
        }
        if (getPaySubscriptionUpdateService() != null) {
            _hashCode += getPaySubscriptionUpdateService().hashCode();
        }
        if (getPaySubscriptionEventUpdateService() != null) {
            _hashCode += getPaySubscriptionEventUpdateService().hashCode();
        }
        if (getPaySubscriptionRetrieveService() != null) {
            _hashCode += getPaySubscriptionRetrieveService().hashCode();
        }
        if (getPayPalPaymentService() != null) {
            _hashCode += getPayPalPaymentService().hashCode();
        }
        if (getPayPalCreditService() != null) {
            _hashCode += getPayPalCreditService().hashCode();
        }
        if (getVoidService() != null) {
            _hashCode += getVoidService().hashCode();
        }
        if (getBusinessRules() != null) {
            _hashCode += getBusinessRules().hashCode();
        }
        if (getPinlessDebitService() != null) {
            _hashCode += getPinlessDebitService().hashCode();
        }
        if (getPinlessDebitValidateService() != null) {
            _hashCode += getPinlessDebitValidateService().hashCode();
        }
        if (getPinlessDebitReversalService() != null) {
            _hashCode += getPinlessDebitReversalService().hashCode();
        }
        if (getBatch() != null) {
            _hashCode += getBatch().hashCode();
        }
        if (getAirlineData() != null) {
            _hashCode += getAirlineData().hashCode();
        }
        if (getPayPalButtonCreateService() != null) {
            _hashCode += getPayPalButtonCreateService().hashCode();
        }
        if (getPayPalPreapprovedPaymentService() != null) {
            _hashCode += getPayPalPreapprovedPaymentService().hashCode();
        }
        if (getPayPalPreapprovedUpdateService() != null) {
            _hashCode += getPayPalPreapprovedUpdateService().hashCode();
        }
        if (getRiskUpdateService() != null) {
            _hashCode += getRiskUpdateService().hashCode();
        }
        if (getReserved() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getReserved());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getReserved(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getDeviceFingerprintID() != null) {
            _hashCode += getDeviceFingerprintID().hashCode();
        }
        if (getPayPalRefundService() != null) {
            _hashCode += getPayPalRefundService().hashCode();
        }
        if (getPayPalAuthReversalService() != null) {
            _hashCode += getPayPalAuthReversalService().hashCode();
        }
        if (getPayPalDoCaptureService() != null) {
            _hashCode += getPayPalDoCaptureService().hashCode();
        }
        if (getPayPalEcDoPaymentService() != null) {
            _hashCode += getPayPalEcDoPaymentService().hashCode();
        }
        if (getPayPalEcGetDetailsService() != null) {
            _hashCode += getPayPalEcGetDetailsService().hashCode();
        }
        if (getPayPalEcSetService() != null) {
            _hashCode += getPayPalEcSetService().hashCode();
        }
        if (getPayPalEcOrderSetupService() != null) {
            _hashCode += getPayPalEcOrderSetupService().hashCode();
        }
        if (getPayPalAuthorizationService() != null) {
            _hashCode += getPayPalAuthorizationService().hashCode();
        }
        if (getPayPalUpdateAgreementService() != null) {
            _hashCode += getPayPalUpdateAgreementService().hashCode();
        }
        if (getPayPalCreateAgreementService() != null) {
            _hashCode += getPayPalCreateAgreementService().hashCode();
        }
        if (getPayPalDoRefTransactionService() != null) {
            _hashCode += getPayPalDoRefTransactionService().hashCode();
        }
        if (getChinaPaymentService() != null) {
            _hashCode += getChinaPaymentService().hashCode();
        }
        if (getChinaRefundService() != null) {
            _hashCode += getChinaRefundService().hashCode();
        }
        if (getBoletoPaymentService() != null) {
            _hashCode += getBoletoPaymentService().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RequestMessage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RequestMessage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantReferenceCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantReferenceCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("debtIndicator");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "debtIndicator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientLibrary");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientLibrary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientLibraryVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientLibraryVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientEnvironment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientEnvironment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientSecurityLibraryVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientSecurityLibraryVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientApplication");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientApplication"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientApplicationVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientApplicationVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("clientApplicationUser");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "clientApplicationUser"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("routingCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "routingCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("returnURL");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "returnURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invoiceHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "invoiceHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "InvoiceHeader"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billTo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "billTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BillTo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipTo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipTo"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ShipTo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipFrom");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "shipFrom"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ShipFrom"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("item");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "item"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Item"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseTotals");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaseTotals"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PurchaseTotals"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fundingTotals");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fundingTotals"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundingTotals"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dcc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "dcc"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DCC"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pos");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pos"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Pos"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("installment");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "installment"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Installment"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("card");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "card"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Card"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("check");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "check"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Check"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bml");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bml"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BML"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("gecc");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "gecc"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "GECC"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ucaf");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ucaf"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "UCAF"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fundTransfer");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fundTransfer"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FundTransfer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subscription");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "subscription"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Subscription"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("recurringSubscriptionInfo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "recurringSubscriptionInfo"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RecurringSubscriptionInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("decisionManager");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "decisionManager"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionManager"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("otherTax");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "otherTax"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "OtherTax"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paypal");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paypal"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPal"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantDefinedData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantDefinedData"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "MerchantDefinedData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantSecureData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantSecureData"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "MerchantSecureData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jpo");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "jpo"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "JPO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderRequestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "orderRequestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccAuthService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccAuthService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccCaptureService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccCaptureService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCaptureService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccCreditService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccCreditService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCreditService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccAuthReversalService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccAuthReversalService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReversalService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccAutoAuthReversalService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccAutoAuthReversalService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAutoAuthReversalService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccDCCService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccDCCService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCDCCService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecDebitService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ecDebitService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECDebitService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecCreditService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ecCreditService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECCreditService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecAuthenticateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ecAuthenticateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECAuthenticateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerAuthEnrollService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerAuthEnrollService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthEnrollService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerAuthValidateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerAuthValidateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthValidateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "taxService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "TaxService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("afsService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "afsService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AFSService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("davService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "davService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DAVService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exportService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "exportService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ExportService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxRatesService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxRatesService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXRatesService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRefundService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRefundService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRefundService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRealTimeService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRealTimeService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRealTimeService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitMandateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitMandateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitMandateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitRefundService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitRefundService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitRefundService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitValidateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitValidateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitValidateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionCreateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionCreateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionCreateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionUpdateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionUpdateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionUpdateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionEventUpdateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionEventUpdateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionEventUpdateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionRetrieveService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionRetrieveService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionRetrieveService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPaymentService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPaymentService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPaymentService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalCreditService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalCreditService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreditService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voidService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "voidService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "VoidService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("businessRules");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "businessRules"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BusinessRules"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitValidateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitValidateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitValidateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitReversalService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitReversalService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReversalService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("batch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "batch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "Batch"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("airlineData");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "airlineData"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AirlineData"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalButtonCreateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalButtonCreateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalButtonCreateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPreapprovedPaymentService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPreapprovedPaymentService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedPaymentService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPreapprovedUpdateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPreapprovedUpdateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedUpdateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riskUpdateService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "riskUpdateService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RiskUpdateService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserved");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reserved"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RequestReserved"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deviceFingerprintID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "deviceFingerprintID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalRefundService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalRefundService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalRefundService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalAuthReversalService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalAuthReversalService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthReversalService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalDoCaptureService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalDoCaptureService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoCaptureService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcDoPaymentService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcDoPaymentService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcDoPaymentService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcGetDetailsService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcGetDetailsService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcGetDetailsService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcSetService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcSetService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcSetService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcOrderSetupService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcOrderSetupService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcOrderSetupService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalAuthorizationService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalAuthorizationService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthorizationService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalUpdateAgreementService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalUpdateAgreementService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalUpdateAgreementService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalCreateAgreementService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalCreateAgreementService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreateAgreementService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalDoRefTransactionService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalDoRefTransactionService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoRefTransactionService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chinaPaymentService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "chinaPaymentService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaPaymentService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chinaRefundService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "chinaRefundService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaRefundService"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("boletoPaymentService");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boletoPaymentService"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BoletoPaymentService"));
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
