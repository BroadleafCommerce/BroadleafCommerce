/**
 * ReplyMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.broadleafcommerce.vendor.cybersource.service.api;

public class ReplyMessage  implements java.io.Serializable {
    private java.lang.String merchantReferenceCode;

    private java.lang.String requestID;

    private java.lang.String decision;

    private java.math.BigInteger reasonCode;

    private java.lang.String[] missingField;

    private java.lang.String[] invalidField;

    private java.lang.String requestToken;

    private org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals purchaseTotals;

    private org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch[] deniedPartiesMatch;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply ccAuthReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureReply ccCaptureReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCCreditReply ccCreditReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalReply ccAuthReversalReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalReply ccAutoAuthReversalReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.CCDCCReply ccDCCReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ECDebitReply ecDebitReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ECCreditReply ecCreditReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateReply ecAuthenticateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollReply payerAuthEnrollReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateReply payerAuthValidateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.TaxReply taxReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.AFSReply afsReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.DAVReply davReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ExportReply exportReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.FXRatesReply fxRatesReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankTransferReply bankTransferReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundReply bankTransferRefundReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeReply bankTransferRealTimeReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateReply directDebitMandateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitReply directDebitReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateReply directDebitValidateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundReply directDebitRefundReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateReply paySubscriptionCreateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateReply paySubscriptionUpdateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateReply paySubscriptionEventUpdateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveReply paySubscriptionRetrieveReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentReply payPalPaymentReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditReply payPalCreditReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.VoidReply voidReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReply pinlessDebitReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateReply pinlessDebitValidateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalReply pinlessDebitReversalReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateReply payPalButtonCreateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentReply payPalPreapprovedPaymentReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateReply payPalPreapprovedUpdateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateReply riskUpdateReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.DecisionReply decisionReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ReplyReserved reserved;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundReply payPalRefundReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalReply payPalAuthReversalReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureReply payPalDoCaptureReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentReply payPalEcDoPaymentReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsReply payPalEcGetDetailsReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetReply payPalEcSetReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationReply payPalAuthorizationReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupReply payPalEcOrderSetupReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementReply payPalUpdateAgreementReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementReply payPalCreateAgreementReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionReply payPalDoRefTransactionReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentReply chinaPaymentReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundReply chinaRefundReply;

    private org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentReply boletoPaymentReply;

    public ReplyMessage() {
    }

    public ReplyMessage(
           java.lang.String merchantReferenceCode,
           java.lang.String requestID,
           java.lang.String decision,
           java.math.BigInteger reasonCode,
           java.lang.String[] missingField,
           java.lang.String[] invalidField,
           java.lang.String requestToken,
           org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals purchaseTotals,
           org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch[] deniedPartiesMatch,
           org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply ccAuthReply,
           org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureReply ccCaptureReply,
           org.broadleafcommerce.vendor.cybersource.service.api.CCCreditReply ccCreditReply,
           org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalReply ccAuthReversalReply,
           org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalReply ccAutoAuthReversalReply,
           org.broadleafcommerce.vendor.cybersource.service.api.CCDCCReply ccDCCReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ECDebitReply ecDebitReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ECCreditReply ecCreditReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateReply ecAuthenticateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollReply payerAuthEnrollReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateReply payerAuthValidateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.TaxReply taxReply,
           org.broadleafcommerce.vendor.cybersource.service.api.AFSReply afsReply,
           org.broadleafcommerce.vendor.cybersource.service.api.DAVReply davReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ExportReply exportReply,
           org.broadleafcommerce.vendor.cybersource.service.api.FXRatesReply fxRatesReply,
           org.broadleafcommerce.vendor.cybersource.service.api.BankTransferReply bankTransferReply,
           org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundReply bankTransferRefundReply,
           org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeReply bankTransferRealTimeReply,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateReply directDebitMandateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitReply directDebitReply,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateReply directDebitValidateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundReply directDebitRefundReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateReply paySubscriptionCreateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateReply paySubscriptionUpdateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateReply paySubscriptionEventUpdateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveReply paySubscriptionRetrieveReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentReply payPalPaymentReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditReply payPalCreditReply,
           org.broadleafcommerce.vendor.cybersource.service.api.VoidReply voidReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReply pinlessDebitReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateReply pinlessDebitValidateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalReply pinlessDebitReversalReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateReply payPalButtonCreateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentReply payPalPreapprovedPaymentReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateReply payPalPreapprovedUpdateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateReply riskUpdateReply,
           org.broadleafcommerce.vendor.cybersource.service.api.DecisionReply decisionReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ReplyReserved reserved,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundReply payPalRefundReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalReply payPalAuthReversalReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureReply payPalDoCaptureReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentReply payPalEcDoPaymentReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsReply payPalEcGetDetailsReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetReply payPalEcSetReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationReply payPalAuthorizationReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupReply payPalEcOrderSetupReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementReply payPalUpdateAgreementReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementReply payPalCreateAgreementReply,
           org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionReply payPalDoRefTransactionReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentReply chinaPaymentReply,
           org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundReply chinaRefundReply,
           org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentReply boletoPaymentReply) {
           this.merchantReferenceCode = merchantReferenceCode;
           this.requestID = requestID;
           this.decision = decision;
           this.reasonCode = reasonCode;
           this.missingField = missingField;
           this.invalidField = invalidField;
           this.requestToken = requestToken;
           this.purchaseTotals = purchaseTotals;
           this.deniedPartiesMatch = deniedPartiesMatch;
           this.ccAuthReply = ccAuthReply;
           this.ccCaptureReply = ccCaptureReply;
           this.ccCreditReply = ccCreditReply;
           this.ccAuthReversalReply = ccAuthReversalReply;
           this.ccAutoAuthReversalReply = ccAutoAuthReversalReply;
           this.ccDCCReply = ccDCCReply;
           this.ecDebitReply = ecDebitReply;
           this.ecCreditReply = ecCreditReply;
           this.ecAuthenticateReply = ecAuthenticateReply;
           this.payerAuthEnrollReply = payerAuthEnrollReply;
           this.payerAuthValidateReply = payerAuthValidateReply;
           this.taxReply = taxReply;
           this.afsReply = afsReply;
           this.davReply = davReply;
           this.exportReply = exportReply;
           this.fxRatesReply = fxRatesReply;
           this.bankTransferReply = bankTransferReply;
           this.bankTransferRefundReply = bankTransferRefundReply;
           this.bankTransferRealTimeReply = bankTransferRealTimeReply;
           this.directDebitMandateReply = directDebitMandateReply;
           this.directDebitReply = directDebitReply;
           this.directDebitValidateReply = directDebitValidateReply;
           this.directDebitRefundReply = directDebitRefundReply;
           this.paySubscriptionCreateReply = paySubscriptionCreateReply;
           this.paySubscriptionUpdateReply = paySubscriptionUpdateReply;
           this.paySubscriptionEventUpdateReply = paySubscriptionEventUpdateReply;
           this.paySubscriptionRetrieveReply = paySubscriptionRetrieveReply;
           this.payPalPaymentReply = payPalPaymentReply;
           this.payPalCreditReply = payPalCreditReply;
           this.voidReply = voidReply;
           this.pinlessDebitReply = pinlessDebitReply;
           this.pinlessDebitValidateReply = pinlessDebitValidateReply;
           this.pinlessDebitReversalReply = pinlessDebitReversalReply;
           this.payPalButtonCreateReply = payPalButtonCreateReply;
           this.payPalPreapprovedPaymentReply = payPalPreapprovedPaymentReply;
           this.payPalPreapprovedUpdateReply = payPalPreapprovedUpdateReply;
           this.riskUpdateReply = riskUpdateReply;
           this.decisionReply = decisionReply;
           this.reserved = reserved;
           this.payPalRefundReply = payPalRefundReply;
           this.payPalAuthReversalReply = payPalAuthReversalReply;
           this.payPalDoCaptureReply = payPalDoCaptureReply;
           this.payPalEcDoPaymentReply = payPalEcDoPaymentReply;
           this.payPalEcGetDetailsReply = payPalEcGetDetailsReply;
           this.payPalEcSetReply = payPalEcSetReply;
           this.payPalAuthorizationReply = payPalAuthorizationReply;
           this.payPalEcOrderSetupReply = payPalEcOrderSetupReply;
           this.payPalUpdateAgreementReply = payPalUpdateAgreementReply;
           this.payPalCreateAgreementReply = payPalCreateAgreementReply;
           this.payPalDoRefTransactionReply = payPalDoRefTransactionReply;
           this.chinaPaymentReply = chinaPaymentReply;
           this.chinaRefundReply = chinaRefundReply;
           this.boletoPaymentReply = boletoPaymentReply;
    }


    /**
     * Gets the merchantReferenceCode value for this ReplyMessage.
     * 
     * @return merchantReferenceCode
     */
    public java.lang.String getMerchantReferenceCode() {
        return merchantReferenceCode;
    }


    /**
     * Sets the merchantReferenceCode value for this ReplyMessage.
     * 
     * @param merchantReferenceCode
     */
    public void setMerchantReferenceCode(java.lang.String merchantReferenceCode) {
        this.merchantReferenceCode = merchantReferenceCode;
    }


    /**
     * Gets the requestID value for this ReplyMessage.
     * 
     * @return requestID
     */
    public java.lang.String getRequestID() {
        return requestID;
    }


    /**
     * Sets the requestID value for this ReplyMessage.
     * 
     * @param requestID
     */
    public void setRequestID(java.lang.String requestID) {
        this.requestID = requestID;
    }


    /**
     * Gets the decision value for this ReplyMessage.
     * 
     * @return decision
     */
    public java.lang.String getDecision() {
        return decision;
    }


    /**
     * Sets the decision value for this ReplyMessage.
     * 
     * @param decision
     */
    public void setDecision(java.lang.String decision) {
        this.decision = decision;
    }


    /**
     * Gets the reasonCode value for this ReplyMessage.
     * 
     * @return reasonCode
     */
    public java.math.BigInteger getReasonCode() {
        return reasonCode;
    }


    /**
     * Sets the reasonCode value for this ReplyMessage.
     * 
     * @param reasonCode
     */
    public void setReasonCode(java.math.BigInteger reasonCode) {
        this.reasonCode = reasonCode;
    }


    /**
     * Gets the missingField value for this ReplyMessage.
     * 
     * @return missingField
     */
    public java.lang.String[] getMissingField() {
        return missingField;
    }


    /**
     * Sets the missingField value for this ReplyMessage.
     * 
     * @param missingField
     */
    public void setMissingField(java.lang.String[] missingField) {
        this.missingField = missingField;
    }

    public java.lang.String getMissingField(int i) {
        return this.missingField[i];
    }

    public void setMissingField(int i, java.lang.String _value) {
        this.missingField[i] = _value;
    }


    /**
     * Gets the invalidField value for this ReplyMessage.
     * 
     * @return invalidField
     */
    public java.lang.String[] getInvalidField() {
        return invalidField;
    }


    /**
     * Sets the invalidField value for this ReplyMessage.
     * 
     * @param invalidField
     */
    public void setInvalidField(java.lang.String[] invalidField) {
        this.invalidField = invalidField;
    }

    public java.lang.String getInvalidField(int i) {
        return this.invalidField[i];
    }

    public void setInvalidField(int i, java.lang.String _value) {
        this.invalidField[i] = _value;
    }


    /**
     * Gets the requestToken value for this ReplyMessage.
     * 
     * @return requestToken
     */
    public java.lang.String getRequestToken() {
        return requestToken;
    }


    /**
     * Sets the requestToken value for this ReplyMessage.
     * 
     * @param requestToken
     */
    public void setRequestToken(java.lang.String requestToken) {
        this.requestToken = requestToken;
    }


    /**
     * Gets the purchaseTotals value for this ReplyMessage.
     * 
     * @return purchaseTotals
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals getPurchaseTotals() {
        return purchaseTotals;
    }


    /**
     * Sets the purchaseTotals value for this ReplyMessage.
     * 
     * @param purchaseTotals
     */
    public void setPurchaseTotals(org.broadleafcommerce.vendor.cybersource.service.api.PurchaseTotals purchaseTotals) {
        this.purchaseTotals = purchaseTotals;
    }


    /**
     * Gets the deniedPartiesMatch value for this ReplyMessage.
     * 
     * @return deniedPartiesMatch
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch[] getDeniedPartiesMatch() {
        return deniedPartiesMatch;
    }


    /**
     * Sets the deniedPartiesMatch value for this ReplyMessage.
     * 
     * @param deniedPartiesMatch
     */
    public void setDeniedPartiesMatch(org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch[] deniedPartiesMatch) {
        this.deniedPartiesMatch = deniedPartiesMatch;
    }

    public org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch getDeniedPartiesMatch(int i) {
        return this.deniedPartiesMatch[i];
    }

    public void setDeniedPartiesMatch(int i, org.broadleafcommerce.vendor.cybersource.service.api.DeniedPartiesMatch _value) {
        this.deniedPartiesMatch[i] = _value;
    }


    /**
     * Gets the ccAuthReply value for this ReplyMessage.
     * 
     * @return ccAuthReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply getCcAuthReply() {
        return ccAuthReply;
    }


    /**
     * Sets the ccAuthReply value for this ReplyMessage.
     * 
     * @param ccAuthReply
     */
    public void setCcAuthReply(org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReply ccAuthReply) {
        this.ccAuthReply = ccAuthReply;
    }


    /**
     * Gets the ccCaptureReply value for this ReplyMessage.
     * 
     * @return ccCaptureReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureReply getCcCaptureReply() {
        return ccCaptureReply;
    }


    /**
     * Sets the ccCaptureReply value for this ReplyMessage.
     * 
     * @param ccCaptureReply
     */
    public void setCcCaptureReply(org.broadleafcommerce.vendor.cybersource.service.api.CCCaptureReply ccCaptureReply) {
        this.ccCaptureReply = ccCaptureReply;
    }


    /**
     * Gets the ccCreditReply value for this ReplyMessage.
     * 
     * @return ccCreditReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCCreditReply getCcCreditReply() {
        return ccCreditReply;
    }


    /**
     * Sets the ccCreditReply value for this ReplyMessage.
     * 
     * @param ccCreditReply
     */
    public void setCcCreditReply(org.broadleafcommerce.vendor.cybersource.service.api.CCCreditReply ccCreditReply) {
        this.ccCreditReply = ccCreditReply;
    }


    /**
     * Gets the ccAuthReversalReply value for this ReplyMessage.
     * 
     * @return ccAuthReversalReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalReply getCcAuthReversalReply() {
        return ccAuthReversalReply;
    }


    /**
     * Sets the ccAuthReversalReply value for this ReplyMessage.
     * 
     * @param ccAuthReversalReply
     */
    public void setCcAuthReversalReply(org.broadleafcommerce.vendor.cybersource.service.api.CCAuthReversalReply ccAuthReversalReply) {
        this.ccAuthReversalReply = ccAuthReversalReply;
    }


    /**
     * Gets the ccAutoAuthReversalReply value for this ReplyMessage.
     * 
     * @return ccAutoAuthReversalReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalReply getCcAutoAuthReversalReply() {
        return ccAutoAuthReversalReply;
    }


    /**
     * Sets the ccAutoAuthReversalReply value for this ReplyMessage.
     * 
     * @param ccAutoAuthReversalReply
     */
    public void setCcAutoAuthReversalReply(org.broadleafcommerce.vendor.cybersource.service.api.CCAutoAuthReversalReply ccAutoAuthReversalReply) {
        this.ccAutoAuthReversalReply = ccAutoAuthReversalReply;
    }


    /**
     * Gets the ccDCCReply value for this ReplyMessage.
     * 
     * @return ccDCCReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.CCDCCReply getCcDCCReply() {
        return ccDCCReply;
    }


    /**
     * Sets the ccDCCReply value for this ReplyMessage.
     * 
     * @param ccDCCReply
     */
    public void setCcDCCReply(org.broadleafcommerce.vendor.cybersource.service.api.CCDCCReply ccDCCReply) {
        this.ccDCCReply = ccDCCReply;
    }


    /**
     * Gets the ecDebitReply value for this ReplyMessage.
     * 
     * @return ecDebitReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ECDebitReply getEcDebitReply() {
        return ecDebitReply;
    }


    /**
     * Sets the ecDebitReply value for this ReplyMessage.
     * 
     * @param ecDebitReply
     */
    public void setEcDebitReply(org.broadleafcommerce.vendor.cybersource.service.api.ECDebitReply ecDebitReply) {
        this.ecDebitReply = ecDebitReply;
    }


    /**
     * Gets the ecCreditReply value for this ReplyMessage.
     * 
     * @return ecCreditReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ECCreditReply getEcCreditReply() {
        return ecCreditReply;
    }


    /**
     * Sets the ecCreditReply value for this ReplyMessage.
     * 
     * @param ecCreditReply
     */
    public void setEcCreditReply(org.broadleafcommerce.vendor.cybersource.service.api.ECCreditReply ecCreditReply) {
        this.ecCreditReply = ecCreditReply;
    }


    /**
     * Gets the ecAuthenticateReply value for this ReplyMessage.
     * 
     * @return ecAuthenticateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateReply getEcAuthenticateReply() {
        return ecAuthenticateReply;
    }


    /**
     * Sets the ecAuthenticateReply value for this ReplyMessage.
     * 
     * @param ecAuthenticateReply
     */
    public void setEcAuthenticateReply(org.broadleafcommerce.vendor.cybersource.service.api.ECAuthenticateReply ecAuthenticateReply) {
        this.ecAuthenticateReply = ecAuthenticateReply;
    }


    /**
     * Gets the payerAuthEnrollReply value for this ReplyMessage.
     * 
     * @return payerAuthEnrollReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollReply getPayerAuthEnrollReply() {
        return payerAuthEnrollReply;
    }


    /**
     * Sets the payerAuthEnrollReply value for this ReplyMessage.
     * 
     * @param payerAuthEnrollReply
     */
    public void setPayerAuthEnrollReply(org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthEnrollReply payerAuthEnrollReply) {
        this.payerAuthEnrollReply = payerAuthEnrollReply;
    }


    /**
     * Gets the payerAuthValidateReply value for this ReplyMessage.
     * 
     * @return payerAuthValidateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateReply getPayerAuthValidateReply() {
        return payerAuthValidateReply;
    }


    /**
     * Sets the payerAuthValidateReply value for this ReplyMessage.
     * 
     * @param payerAuthValidateReply
     */
    public void setPayerAuthValidateReply(org.broadleafcommerce.vendor.cybersource.service.api.PayerAuthValidateReply payerAuthValidateReply) {
        this.payerAuthValidateReply = payerAuthValidateReply;
    }


    /**
     * Gets the taxReply value for this ReplyMessage.
     * 
     * @return taxReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.TaxReply getTaxReply() {
        return taxReply;
    }


    /**
     * Sets the taxReply value for this ReplyMessage.
     * 
     * @param taxReply
     */
    public void setTaxReply(org.broadleafcommerce.vendor.cybersource.service.api.TaxReply taxReply) {
        this.taxReply = taxReply;
    }


    /**
     * Gets the afsReply value for this ReplyMessage.
     * 
     * @return afsReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.AFSReply getAfsReply() {
        return afsReply;
    }


    /**
     * Sets the afsReply value for this ReplyMessage.
     * 
     * @param afsReply
     */
    public void setAfsReply(org.broadleafcommerce.vendor.cybersource.service.api.AFSReply afsReply) {
        this.afsReply = afsReply;
    }


    /**
     * Gets the davReply value for this ReplyMessage.
     * 
     * @return davReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DAVReply getDavReply() {
        return davReply;
    }


    /**
     * Sets the davReply value for this ReplyMessage.
     * 
     * @param davReply
     */
    public void setDavReply(org.broadleafcommerce.vendor.cybersource.service.api.DAVReply davReply) {
        this.davReply = davReply;
    }


    /**
     * Gets the exportReply value for this ReplyMessage.
     * 
     * @return exportReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ExportReply getExportReply() {
        return exportReply;
    }


    /**
     * Sets the exportReply value for this ReplyMessage.
     * 
     * @param exportReply
     */
    public void setExportReply(org.broadleafcommerce.vendor.cybersource.service.api.ExportReply exportReply) {
        this.exportReply = exportReply;
    }


    /**
     * Gets the fxRatesReply value for this ReplyMessage.
     * 
     * @return fxRatesReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.FXRatesReply getFxRatesReply() {
        return fxRatesReply;
    }


    /**
     * Sets the fxRatesReply value for this ReplyMessage.
     * 
     * @param fxRatesReply
     */
    public void setFxRatesReply(org.broadleafcommerce.vendor.cybersource.service.api.FXRatesReply fxRatesReply) {
        this.fxRatesReply = fxRatesReply;
    }


    /**
     * Gets the bankTransferReply value for this ReplyMessage.
     * 
     * @return bankTransferReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankTransferReply getBankTransferReply() {
        return bankTransferReply;
    }


    /**
     * Sets the bankTransferReply value for this ReplyMessage.
     * 
     * @param bankTransferReply
     */
    public void setBankTransferReply(org.broadleafcommerce.vendor.cybersource.service.api.BankTransferReply bankTransferReply) {
        this.bankTransferReply = bankTransferReply;
    }


    /**
     * Gets the bankTransferRefundReply value for this ReplyMessage.
     * 
     * @return bankTransferRefundReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundReply getBankTransferRefundReply() {
        return bankTransferRefundReply;
    }


    /**
     * Sets the bankTransferRefundReply value for this ReplyMessage.
     * 
     * @param bankTransferRefundReply
     */
    public void setBankTransferRefundReply(org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRefundReply bankTransferRefundReply) {
        this.bankTransferRefundReply = bankTransferRefundReply;
    }


    /**
     * Gets the bankTransferRealTimeReply value for this ReplyMessage.
     * 
     * @return bankTransferRealTimeReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeReply getBankTransferRealTimeReply() {
        return bankTransferRealTimeReply;
    }


    /**
     * Sets the bankTransferRealTimeReply value for this ReplyMessage.
     * 
     * @param bankTransferRealTimeReply
     */
    public void setBankTransferRealTimeReply(org.broadleafcommerce.vendor.cybersource.service.api.BankTransferRealTimeReply bankTransferRealTimeReply) {
        this.bankTransferRealTimeReply = bankTransferRealTimeReply;
    }


    /**
     * Gets the directDebitMandateReply value for this ReplyMessage.
     * 
     * @return directDebitMandateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateReply getDirectDebitMandateReply() {
        return directDebitMandateReply;
    }


    /**
     * Sets the directDebitMandateReply value for this ReplyMessage.
     * 
     * @param directDebitMandateReply
     */
    public void setDirectDebitMandateReply(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitMandateReply directDebitMandateReply) {
        this.directDebitMandateReply = directDebitMandateReply;
    }


    /**
     * Gets the directDebitReply value for this ReplyMessage.
     * 
     * @return directDebitReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitReply getDirectDebitReply() {
        return directDebitReply;
    }


    /**
     * Sets the directDebitReply value for this ReplyMessage.
     * 
     * @param directDebitReply
     */
    public void setDirectDebitReply(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitReply directDebitReply) {
        this.directDebitReply = directDebitReply;
    }


    /**
     * Gets the directDebitValidateReply value for this ReplyMessage.
     * 
     * @return directDebitValidateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateReply getDirectDebitValidateReply() {
        return directDebitValidateReply;
    }


    /**
     * Sets the directDebitValidateReply value for this ReplyMessage.
     * 
     * @param directDebitValidateReply
     */
    public void setDirectDebitValidateReply(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitValidateReply directDebitValidateReply) {
        this.directDebitValidateReply = directDebitValidateReply;
    }


    /**
     * Gets the directDebitRefundReply value for this ReplyMessage.
     * 
     * @return directDebitRefundReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundReply getDirectDebitRefundReply() {
        return directDebitRefundReply;
    }


    /**
     * Sets the directDebitRefundReply value for this ReplyMessage.
     * 
     * @param directDebitRefundReply
     */
    public void setDirectDebitRefundReply(org.broadleafcommerce.vendor.cybersource.service.api.DirectDebitRefundReply directDebitRefundReply) {
        this.directDebitRefundReply = directDebitRefundReply;
    }


    /**
     * Gets the paySubscriptionCreateReply value for this ReplyMessage.
     * 
     * @return paySubscriptionCreateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateReply getPaySubscriptionCreateReply() {
        return paySubscriptionCreateReply;
    }


    /**
     * Sets the paySubscriptionCreateReply value for this ReplyMessage.
     * 
     * @param paySubscriptionCreateReply
     */
    public void setPaySubscriptionCreateReply(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionCreateReply paySubscriptionCreateReply) {
        this.paySubscriptionCreateReply = paySubscriptionCreateReply;
    }


    /**
     * Gets the paySubscriptionUpdateReply value for this ReplyMessage.
     * 
     * @return paySubscriptionUpdateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateReply getPaySubscriptionUpdateReply() {
        return paySubscriptionUpdateReply;
    }


    /**
     * Sets the paySubscriptionUpdateReply value for this ReplyMessage.
     * 
     * @param paySubscriptionUpdateReply
     */
    public void setPaySubscriptionUpdateReply(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionUpdateReply paySubscriptionUpdateReply) {
        this.paySubscriptionUpdateReply = paySubscriptionUpdateReply;
    }


    /**
     * Gets the paySubscriptionEventUpdateReply value for this ReplyMessage.
     * 
     * @return paySubscriptionEventUpdateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateReply getPaySubscriptionEventUpdateReply() {
        return paySubscriptionEventUpdateReply;
    }


    /**
     * Sets the paySubscriptionEventUpdateReply value for this ReplyMessage.
     * 
     * @param paySubscriptionEventUpdateReply
     */
    public void setPaySubscriptionEventUpdateReply(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionEventUpdateReply paySubscriptionEventUpdateReply) {
        this.paySubscriptionEventUpdateReply = paySubscriptionEventUpdateReply;
    }


    /**
     * Gets the paySubscriptionRetrieveReply value for this ReplyMessage.
     * 
     * @return paySubscriptionRetrieveReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveReply getPaySubscriptionRetrieveReply() {
        return paySubscriptionRetrieveReply;
    }


    /**
     * Sets the paySubscriptionRetrieveReply value for this ReplyMessage.
     * 
     * @param paySubscriptionRetrieveReply
     */
    public void setPaySubscriptionRetrieveReply(org.broadleafcommerce.vendor.cybersource.service.api.PaySubscriptionRetrieveReply paySubscriptionRetrieveReply) {
        this.paySubscriptionRetrieveReply = paySubscriptionRetrieveReply;
    }


    /**
     * Gets the payPalPaymentReply value for this ReplyMessage.
     * 
     * @return payPalPaymentReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentReply getPayPalPaymentReply() {
        return payPalPaymentReply;
    }


    /**
     * Sets the payPalPaymentReply value for this ReplyMessage.
     * 
     * @param payPalPaymentReply
     */
    public void setPayPalPaymentReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalPaymentReply payPalPaymentReply) {
        this.payPalPaymentReply = payPalPaymentReply;
    }


    /**
     * Gets the payPalCreditReply value for this ReplyMessage.
     * 
     * @return payPalCreditReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditReply getPayPalCreditReply() {
        return payPalCreditReply;
    }


    /**
     * Sets the payPalCreditReply value for this ReplyMessage.
     * 
     * @param payPalCreditReply
     */
    public void setPayPalCreditReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreditReply payPalCreditReply) {
        this.payPalCreditReply = payPalCreditReply;
    }


    /**
     * Gets the voidReply value for this ReplyMessage.
     * 
     * @return voidReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.VoidReply getVoidReply() {
        return voidReply;
    }


    /**
     * Sets the voidReply value for this ReplyMessage.
     * 
     * @param voidReply
     */
    public void setVoidReply(org.broadleafcommerce.vendor.cybersource.service.api.VoidReply voidReply) {
        this.voidReply = voidReply;
    }


    /**
     * Gets the pinlessDebitReply value for this ReplyMessage.
     * 
     * @return pinlessDebitReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReply getPinlessDebitReply() {
        return pinlessDebitReply;
    }


    /**
     * Sets the pinlessDebitReply value for this ReplyMessage.
     * 
     * @param pinlessDebitReply
     */
    public void setPinlessDebitReply(org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReply pinlessDebitReply) {
        this.pinlessDebitReply = pinlessDebitReply;
    }


    /**
     * Gets the pinlessDebitValidateReply value for this ReplyMessage.
     * 
     * @return pinlessDebitValidateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateReply getPinlessDebitValidateReply() {
        return pinlessDebitValidateReply;
    }


    /**
     * Sets the pinlessDebitValidateReply value for this ReplyMessage.
     * 
     * @param pinlessDebitValidateReply
     */
    public void setPinlessDebitValidateReply(org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitValidateReply pinlessDebitValidateReply) {
        this.pinlessDebitValidateReply = pinlessDebitValidateReply;
    }


    /**
     * Gets the pinlessDebitReversalReply value for this ReplyMessage.
     * 
     * @return pinlessDebitReversalReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalReply getPinlessDebitReversalReply() {
        return pinlessDebitReversalReply;
    }


    /**
     * Sets the pinlessDebitReversalReply value for this ReplyMessage.
     * 
     * @param pinlessDebitReversalReply
     */
    public void setPinlessDebitReversalReply(org.broadleafcommerce.vendor.cybersource.service.api.PinlessDebitReversalReply pinlessDebitReversalReply) {
        this.pinlessDebitReversalReply = pinlessDebitReversalReply;
    }


    /**
     * Gets the payPalButtonCreateReply value for this ReplyMessage.
     * 
     * @return payPalButtonCreateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateReply getPayPalButtonCreateReply() {
        return payPalButtonCreateReply;
    }


    /**
     * Sets the payPalButtonCreateReply value for this ReplyMessage.
     * 
     * @param payPalButtonCreateReply
     */
    public void setPayPalButtonCreateReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalButtonCreateReply payPalButtonCreateReply) {
        this.payPalButtonCreateReply = payPalButtonCreateReply;
    }


    /**
     * Gets the payPalPreapprovedPaymentReply value for this ReplyMessage.
     * 
     * @return payPalPreapprovedPaymentReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentReply getPayPalPreapprovedPaymentReply() {
        return payPalPreapprovedPaymentReply;
    }


    /**
     * Sets the payPalPreapprovedPaymentReply value for this ReplyMessage.
     * 
     * @param payPalPreapprovedPaymentReply
     */
    public void setPayPalPreapprovedPaymentReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedPaymentReply payPalPreapprovedPaymentReply) {
        this.payPalPreapprovedPaymentReply = payPalPreapprovedPaymentReply;
    }


    /**
     * Gets the payPalPreapprovedUpdateReply value for this ReplyMessage.
     * 
     * @return payPalPreapprovedUpdateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateReply getPayPalPreapprovedUpdateReply() {
        return payPalPreapprovedUpdateReply;
    }


    /**
     * Sets the payPalPreapprovedUpdateReply value for this ReplyMessage.
     * 
     * @param payPalPreapprovedUpdateReply
     */
    public void setPayPalPreapprovedUpdateReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalPreapprovedUpdateReply payPalPreapprovedUpdateReply) {
        this.payPalPreapprovedUpdateReply = payPalPreapprovedUpdateReply;
    }


    /**
     * Gets the riskUpdateReply value for this ReplyMessage.
     * 
     * @return riskUpdateReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateReply getRiskUpdateReply() {
        return riskUpdateReply;
    }


    /**
     * Sets the riskUpdateReply value for this ReplyMessage.
     * 
     * @param riskUpdateReply
     */
    public void setRiskUpdateReply(org.broadleafcommerce.vendor.cybersource.service.api.RiskUpdateReply riskUpdateReply) {
        this.riskUpdateReply = riskUpdateReply;
    }


    /**
     * Gets the decisionReply value for this ReplyMessage.
     * 
     * @return decisionReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.DecisionReply getDecisionReply() {
        return decisionReply;
    }


    /**
     * Sets the decisionReply value for this ReplyMessage.
     * 
     * @param decisionReply
     */
    public void setDecisionReply(org.broadleafcommerce.vendor.cybersource.service.api.DecisionReply decisionReply) {
        this.decisionReply = decisionReply;
    }


    /**
     * Gets the reserved value for this ReplyMessage.
     * 
     * @return reserved
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ReplyReserved getReserved() {
        return reserved;
    }


    /**
     * Sets the reserved value for this ReplyMessage.
     * 
     * @param reserved
     */
    public void setReserved(org.broadleafcommerce.vendor.cybersource.service.api.ReplyReserved reserved) {
        this.reserved = reserved;
    }


    /**
     * Gets the payPalRefundReply value for this ReplyMessage.
     * 
     * @return payPalRefundReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundReply getPayPalRefundReply() {
        return payPalRefundReply;
    }


    /**
     * Sets the payPalRefundReply value for this ReplyMessage.
     * 
     * @param payPalRefundReply
     */
    public void setPayPalRefundReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalRefundReply payPalRefundReply) {
        this.payPalRefundReply = payPalRefundReply;
    }


    /**
     * Gets the payPalAuthReversalReply value for this ReplyMessage.
     * 
     * @return payPalAuthReversalReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalReply getPayPalAuthReversalReply() {
        return payPalAuthReversalReply;
    }


    /**
     * Sets the payPalAuthReversalReply value for this ReplyMessage.
     * 
     * @param payPalAuthReversalReply
     */
    public void setPayPalAuthReversalReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthReversalReply payPalAuthReversalReply) {
        this.payPalAuthReversalReply = payPalAuthReversalReply;
    }


    /**
     * Gets the payPalDoCaptureReply value for this ReplyMessage.
     * 
     * @return payPalDoCaptureReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureReply getPayPalDoCaptureReply() {
        return payPalDoCaptureReply;
    }


    /**
     * Sets the payPalDoCaptureReply value for this ReplyMessage.
     * 
     * @param payPalDoCaptureReply
     */
    public void setPayPalDoCaptureReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoCaptureReply payPalDoCaptureReply) {
        this.payPalDoCaptureReply = payPalDoCaptureReply;
    }


    /**
     * Gets the payPalEcDoPaymentReply value for this ReplyMessage.
     * 
     * @return payPalEcDoPaymentReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentReply getPayPalEcDoPaymentReply() {
        return payPalEcDoPaymentReply;
    }


    /**
     * Sets the payPalEcDoPaymentReply value for this ReplyMessage.
     * 
     * @param payPalEcDoPaymentReply
     */
    public void setPayPalEcDoPaymentReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcDoPaymentReply payPalEcDoPaymentReply) {
        this.payPalEcDoPaymentReply = payPalEcDoPaymentReply;
    }


    /**
     * Gets the payPalEcGetDetailsReply value for this ReplyMessage.
     * 
     * @return payPalEcGetDetailsReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsReply getPayPalEcGetDetailsReply() {
        return payPalEcGetDetailsReply;
    }


    /**
     * Sets the payPalEcGetDetailsReply value for this ReplyMessage.
     * 
     * @param payPalEcGetDetailsReply
     */
    public void setPayPalEcGetDetailsReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcGetDetailsReply payPalEcGetDetailsReply) {
        this.payPalEcGetDetailsReply = payPalEcGetDetailsReply;
    }


    /**
     * Gets the payPalEcSetReply value for this ReplyMessage.
     * 
     * @return payPalEcSetReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetReply getPayPalEcSetReply() {
        return payPalEcSetReply;
    }


    /**
     * Sets the payPalEcSetReply value for this ReplyMessage.
     * 
     * @param payPalEcSetReply
     */
    public void setPayPalEcSetReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcSetReply payPalEcSetReply) {
        this.payPalEcSetReply = payPalEcSetReply;
    }


    /**
     * Gets the payPalAuthorizationReply value for this ReplyMessage.
     * 
     * @return payPalAuthorizationReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationReply getPayPalAuthorizationReply() {
        return payPalAuthorizationReply;
    }


    /**
     * Sets the payPalAuthorizationReply value for this ReplyMessage.
     * 
     * @param payPalAuthorizationReply
     */
    public void setPayPalAuthorizationReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalAuthorizationReply payPalAuthorizationReply) {
        this.payPalAuthorizationReply = payPalAuthorizationReply;
    }


    /**
     * Gets the payPalEcOrderSetupReply value for this ReplyMessage.
     * 
     * @return payPalEcOrderSetupReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupReply getPayPalEcOrderSetupReply() {
        return payPalEcOrderSetupReply;
    }


    /**
     * Sets the payPalEcOrderSetupReply value for this ReplyMessage.
     * 
     * @param payPalEcOrderSetupReply
     */
    public void setPayPalEcOrderSetupReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalEcOrderSetupReply payPalEcOrderSetupReply) {
        this.payPalEcOrderSetupReply = payPalEcOrderSetupReply;
    }


    /**
     * Gets the payPalUpdateAgreementReply value for this ReplyMessage.
     * 
     * @return payPalUpdateAgreementReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementReply getPayPalUpdateAgreementReply() {
        return payPalUpdateAgreementReply;
    }


    /**
     * Sets the payPalUpdateAgreementReply value for this ReplyMessage.
     * 
     * @param payPalUpdateAgreementReply
     */
    public void setPayPalUpdateAgreementReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalUpdateAgreementReply payPalUpdateAgreementReply) {
        this.payPalUpdateAgreementReply = payPalUpdateAgreementReply;
    }


    /**
     * Gets the payPalCreateAgreementReply value for this ReplyMessage.
     * 
     * @return payPalCreateAgreementReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementReply getPayPalCreateAgreementReply() {
        return payPalCreateAgreementReply;
    }


    /**
     * Sets the payPalCreateAgreementReply value for this ReplyMessage.
     * 
     * @param payPalCreateAgreementReply
     */
    public void setPayPalCreateAgreementReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalCreateAgreementReply payPalCreateAgreementReply) {
        this.payPalCreateAgreementReply = payPalCreateAgreementReply;
    }


    /**
     * Gets the payPalDoRefTransactionReply value for this ReplyMessage.
     * 
     * @return payPalDoRefTransactionReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionReply getPayPalDoRefTransactionReply() {
        return payPalDoRefTransactionReply;
    }


    /**
     * Sets the payPalDoRefTransactionReply value for this ReplyMessage.
     * 
     * @param payPalDoRefTransactionReply
     */
    public void setPayPalDoRefTransactionReply(org.broadleafcommerce.vendor.cybersource.service.api.PayPalDoRefTransactionReply payPalDoRefTransactionReply) {
        this.payPalDoRefTransactionReply = payPalDoRefTransactionReply;
    }


    /**
     * Gets the chinaPaymentReply value for this ReplyMessage.
     * 
     * @return chinaPaymentReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentReply getChinaPaymentReply() {
        return chinaPaymentReply;
    }


    /**
     * Sets the chinaPaymentReply value for this ReplyMessage.
     * 
     * @param chinaPaymentReply
     */
    public void setChinaPaymentReply(org.broadleafcommerce.vendor.cybersource.service.api.ChinaPaymentReply chinaPaymentReply) {
        this.chinaPaymentReply = chinaPaymentReply;
    }


    /**
     * Gets the chinaRefundReply value for this ReplyMessage.
     * 
     * @return chinaRefundReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundReply getChinaRefundReply() {
        return chinaRefundReply;
    }


    /**
     * Sets the chinaRefundReply value for this ReplyMessage.
     * 
     * @param chinaRefundReply
     */
    public void setChinaRefundReply(org.broadleafcommerce.vendor.cybersource.service.api.ChinaRefundReply chinaRefundReply) {
        this.chinaRefundReply = chinaRefundReply;
    }


    /**
     * Gets the boletoPaymentReply value for this ReplyMessage.
     * 
     * @return boletoPaymentReply
     */
    public org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentReply getBoletoPaymentReply() {
        return boletoPaymentReply;
    }


    /**
     * Sets the boletoPaymentReply value for this ReplyMessage.
     * 
     * @param boletoPaymentReply
     */
    public void setBoletoPaymentReply(org.broadleafcommerce.vendor.cybersource.service.api.BoletoPaymentReply boletoPaymentReply) {
        this.boletoPaymentReply = boletoPaymentReply;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReplyMessage)) return false;
        ReplyMessage other = (ReplyMessage) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.merchantReferenceCode==null && other.getMerchantReferenceCode()==null) || 
             (this.merchantReferenceCode!=null &&
              this.merchantReferenceCode.equals(other.getMerchantReferenceCode()))) &&
            ((this.requestID==null && other.getRequestID()==null) || 
             (this.requestID!=null &&
              this.requestID.equals(other.getRequestID()))) &&
            ((this.decision==null && other.getDecision()==null) || 
             (this.decision!=null &&
              this.decision.equals(other.getDecision()))) &&
            ((this.reasonCode==null && other.getReasonCode()==null) || 
             (this.reasonCode!=null &&
              this.reasonCode.equals(other.getReasonCode()))) &&
            ((this.missingField==null && other.getMissingField()==null) || 
             (this.missingField!=null &&
              java.util.Arrays.equals(this.missingField, other.getMissingField()))) &&
            ((this.invalidField==null && other.getInvalidField()==null) || 
             (this.invalidField!=null &&
              java.util.Arrays.equals(this.invalidField, other.getInvalidField()))) &&
            ((this.requestToken==null && other.getRequestToken()==null) || 
             (this.requestToken!=null &&
              this.requestToken.equals(other.getRequestToken()))) &&
            ((this.purchaseTotals==null && other.getPurchaseTotals()==null) || 
             (this.purchaseTotals!=null &&
              this.purchaseTotals.equals(other.getPurchaseTotals()))) &&
            ((this.deniedPartiesMatch==null && other.getDeniedPartiesMatch()==null) || 
             (this.deniedPartiesMatch!=null &&
              java.util.Arrays.equals(this.deniedPartiesMatch, other.getDeniedPartiesMatch()))) &&
            ((this.ccAuthReply==null && other.getCcAuthReply()==null) || 
             (this.ccAuthReply!=null &&
              this.ccAuthReply.equals(other.getCcAuthReply()))) &&
            ((this.ccCaptureReply==null && other.getCcCaptureReply()==null) || 
             (this.ccCaptureReply!=null &&
              this.ccCaptureReply.equals(other.getCcCaptureReply()))) &&
            ((this.ccCreditReply==null && other.getCcCreditReply()==null) || 
             (this.ccCreditReply!=null &&
              this.ccCreditReply.equals(other.getCcCreditReply()))) &&
            ((this.ccAuthReversalReply==null && other.getCcAuthReversalReply()==null) || 
             (this.ccAuthReversalReply!=null &&
              this.ccAuthReversalReply.equals(other.getCcAuthReversalReply()))) &&
            ((this.ccAutoAuthReversalReply==null && other.getCcAutoAuthReversalReply()==null) || 
             (this.ccAutoAuthReversalReply!=null &&
              this.ccAutoAuthReversalReply.equals(other.getCcAutoAuthReversalReply()))) &&
            ((this.ccDCCReply==null && other.getCcDCCReply()==null) || 
             (this.ccDCCReply!=null &&
              this.ccDCCReply.equals(other.getCcDCCReply()))) &&
            ((this.ecDebitReply==null && other.getEcDebitReply()==null) || 
             (this.ecDebitReply!=null &&
              this.ecDebitReply.equals(other.getEcDebitReply()))) &&
            ((this.ecCreditReply==null && other.getEcCreditReply()==null) || 
             (this.ecCreditReply!=null &&
              this.ecCreditReply.equals(other.getEcCreditReply()))) &&
            ((this.ecAuthenticateReply==null && other.getEcAuthenticateReply()==null) || 
             (this.ecAuthenticateReply!=null &&
              this.ecAuthenticateReply.equals(other.getEcAuthenticateReply()))) &&
            ((this.payerAuthEnrollReply==null && other.getPayerAuthEnrollReply()==null) || 
             (this.payerAuthEnrollReply!=null &&
              this.payerAuthEnrollReply.equals(other.getPayerAuthEnrollReply()))) &&
            ((this.payerAuthValidateReply==null && other.getPayerAuthValidateReply()==null) || 
             (this.payerAuthValidateReply!=null &&
              this.payerAuthValidateReply.equals(other.getPayerAuthValidateReply()))) &&
            ((this.taxReply==null && other.getTaxReply()==null) || 
             (this.taxReply!=null &&
              this.taxReply.equals(other.getTaxReply()))) &&
            ((this.afsReply==null && other.getAfsReply()==null) || 
             (this.afsReply!=null &&
              this.afsReply.equals(other.getAfsReply()))) &&
            ((this.davReply==null && other.getDavReply()==null) || 
             (this.davReply!=null &&
              this.davReply.equals(other.getDavReply()))) &&
            ((this.exportReply==null && other.getExportReply()==null) || 
             (this.exportReply!=null &&
              this.exportReply.equals(other.getExportReply()))) &&
            ((this.fxRatesReply==null && other.getFxRatesReply()==null) || 
             (this.fxRatesReply!=null &&
              this.fxRatesReply.equals(other.getFxRatesReply()))) &&
            ((this.bankTransferReply==null && other.getBankTransferReply()==null) || 
             (this.bankTransferReply!=null &&
              this.bankTransferReply.equals(other.getBankTransferReply()))) &&
            ((this.bankTransferRefundReply==null && other.getBankTransferRefundReply()==null) || 
             (this.bankTransferRefundReply!=null &&
              this.bankTransferRefundReply.equals(other.getBankTransferRefundReply()))) &&
            ((this.bankTransferRealTimeReply==null && other.getBankTransferRealTimeReply()==null) || 
             (this.bankTransferRealTimeReply!=null &&
              this.bankTransferRealTimeReply.equals(other.getBankTransferRealTimeReply()))) &&
            ((this.directDebitMandateReply==null && other.getDirectDebitMandateReply()==null) || 
             (this.directDebitMandateReply!=null &&
              this.directDebitMandateReply.equals(other.getDirectDebitMandateReply()))) &&
            ((this.directDebitReply==null && other.getDirectDebitReply()==null) || 
             (this.directDebitReply!=null &&
              this.directDebitReply.equals(other.getDirectDebitReply()))) &&
            ((this.directDebitValidateReply==null && other.getDirectDebitValidateReply()==null) || 
             (this.directDebitValidateReply!=null &&
              this.directDebitValidateReply.equals(other.getDirectDebitValidateReply()))) &&
            ((this.directDebitRefundReply==null && other.getDirectDebitRefundReply()==null) || 
             (this.directDebitRefundReply!=null &&
              this.directDebitRefundReply.equals(other.getDirectDebitRefundReply()))) &&
            ((this.paySubscriptionCreateReply==null && other.getPaySubscriptionCreateReply()==null) || 
             (this.paySubscriptionCreateReply!=null &&
              this.paySubscriptionCreateReply.equals(other.getPaySubscriptionCreateReply()))) &&
            ((this.paySubscriptionUpdateReply==null && other.getPaySubscriptionUpdateReply()==null) || 
             (this.paySubscriptionUpdateReply!=null &&
              this.paySubscriptionUpdateReply.equals(other.getPaySubscriptionUpdateReply()))) &&
            ((this.paySubscriptionEventUpdateReply==null && other.getPaySubscriptionEventUpdateReply()==null) || 
             (this.paySubscriptionEventUpdateReply!=null &&
              this.paySubscriptionEventUpdateReply.equals(other.getPaySubscriptionEventUpdateReply()))) &&
            ((this.paySubscriptionRetrieveReply==null && other.getPaySubscriptionRetrieveReply()==null) || 
             (this.paySubscriptionRetrieveReply!=null &&
              this.paySubscriptionRetrieveReply.equals(other.getPaySubscriptionRetrieveReply()))) &&
            ((this.payPalPaymentReply==null && other.getPayPalPaymentReply()==null) || 
             (this.payPalPaymentReply!=null &&
              this.payPalPaymentReply.equals(other.getPayPalPaymentReply()))) &&
            ((this.payPalCreditReply==null && other.getPayPalCreditReply()==null) || 
             (this.payPalCreditReply!=null &&
              this.payPalCreditReply.equals(other.getPayPalCreditReply()))) &&
            ((this.voidReply==null && other.getVoidReply()==null) || 
             (this.voidReply!=null &&
              this.voidReply.equals(other.getVoidReply()))) &&
            ((this.pinlessDebitReply==null && other.getPinlessDebitReply()==null) || 
             (this.pinlessDebitReply!=null &&
              this.pinlessDebitReply.equals(other.getPinlessDebitReply()))) &&
            ((this.pinlessDebitValidateReply==null && other.getPinlessDebitValidateReply()==null) || 
             (this.pinlessDebitValidateReply!=null &&
              this.pinlessDebitValidateReply.equals(other.getPinlessDebitValidateReply()))) &&
            ((this.pinlessDebitReversalReply==null && other.getPinlessDebitReversalReply()==null) || 
             (this.pinlessDebitReversalReply!=null &&
              this.pinlessDebitReversalReply.equals(other.getPinlessDebitReversalReply()))) &&
            ((this.payPalButtonCreateReply==null && other.getPayPalButtonCreateReply()==null) || 
             (this.payPalButtonCreateReply!=null &&
              this.payPalButtonCreateReply.equals(other.getPayPalButtonCreateReply()))) &&
            ((this.payPalPreapprovedPaymentReply==null && other.getPayPalPreapprovedPaymentReply()==null) || 
             (this.payPalPreapprovedPaymentReply!=null &&
              this.payPalPreapprovedPaymentReply.equals(other.getPayPalPreapprovedPaymentReply()))) &&
            ((this.payPalPreapprovedUpdateReply==null && other.getPayPalPreapprovedUpdateReply()==null) || 
             (this.payPalPreapprovedUpdateReply!=null &&
              this.payPalPreapprovedUpdateReply.equals(other.getPayPalPreapprovedUpdateReply()))) &&
            ((this.riskUpdateReply==null && other.getRiskUpdateReply()==null) || 
             (this.riskUpdateReply!=null &&
              this.riskUpdateReply.equals(other.getRiskUpdateReply()))) &&
            ((this.decisionReply==null && other.getDecisionReply()==null) || 
             (this.decisionReply!=null &&
              this.decisionReply.equals(other.getDecisionReply()))) &&
            ((this.reserved==null && other.getReserved()==null) || 
             (this.reserved!=null &&
              this.reserved.equals(other.getReserved()))) &&
            ((this.payPalRefundReply==null && other.getPayPalRefundReply()==null) || 
             (this.payPalRefundReply!=null &&
              this.payPalRefundReply.equals(other.getPayPalRefundReply()))) &&
            ((this.payPalAuthReversalReply==null && other.getPayPalAuthReversalReply()==null) || 
             (this.payPalAuthReversalReply!=null &&
              this.payPalAuthReversalReply.equals(other.getPayPalAuthReversalReply()))) &&
            ((this.payPalDoCaptureReply==null && other.getPayPalDoCaptureReply()==null) || 
             (this.payPalDoCaptureReply!=null &&
              this.payPalDoCaptureReply.equals(other.getPayPalDoCaptureReply()))) &&
            ((this.payPalEcDoPaymentReply==null && other.getPayPalEcDoPaymentReply()==null) || 
             (this.payPalEcDoPaymentReply!=null &&
              this.payPalEcDoPaymentReply.equals(other.getPayPalEcDoPaymentReply()))) &&
            ((this.payPalEcGetDetailsReply==null && other.getPayPalEcGetDetailsReply()==null) || 
             (this.payPalEcGetDetailsReply!=null &&
              this.payPalEcGetDetailsReply.equals(other.getPayPalEcGetDetailsReply()))) &&
            ((this.payPalEcSetReply==null && other.getPayPalEcSetReply()==null) || 
             (this.payPalEcSetReply!=null &&
              this.payPalEcSetReply.equals(other.getPayPalEcSetReply()))) &&
            ((this.payPalAuthorizationReply==null && other.getPayPalAuthorizationReply()==null) || 
             (this.payPalAuthorizationReply!=null &&
              this.payPalAuthorizationReply.equals(other.getPayPalAuthorizationReply()))) &&
            ((this.payPalEcOrderSetupReply==null && other.getPayPalEcOrderSetupReply()==null) || 
             (this.payPalEcOrderSetupReply!=null &&
              this.payPalEcOrderSetupReply.equals(other.getPayPalEcOrderSetupReply()))) &&
            ((this.payPalUpdateAgreementReply==null && other.getPayPalUpdateAgreementReply()==null) || 
             (this.payPalUpdateAgreementReply!=null &&
              this.payPalUpdateAgreementReply.equals(other.getPayPalUpdateAgreementReply()))) &&
            ((this.payPalCreateAgreementReply==null && other.getPayPalCreateAgreementReply()==null) || 
             (this.payPalCreateAgreementReply!=null &&
              this.payPalCreateAgreementReply.equals(other.getPayPalCreateAgreementReply()))) &&
            ((this.payPalDoRefTransactionReply==null && other.getPayPalDoRefTransactionReply()==null) || 
             (this.payPalDoRefTransactionReply!=null &&
              this.payPalDoRefTransactionReply.equals(other.getPayPalDoRefTransactionReply()))) &&
            ((this.chinaPaymentReply==null && other.getChinaPaymentReply()==null) || 
             (this.chinaPaymentReply!=null &&
              this.chinaPaymentReply.equals(other.getChinaPaymentReply()))) &&
            ((this.chinaRefundReply==null && other.getChinaRefundReply()==null) || 
             (this.chinaRefundReply!=null &&
              this.chinaRefundReply.equals(other.getChinaRefundReply()))) &&
            ((this.boletoPaymentReply==null && other.getBoletoPaymentReply()==null) || 
             (this.boletoPaymentReply!=null &&
              this.boletoPaymentReply.equals(other.getBoletoPaymentReply())));
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
        if (getMerchantReferenceCode() != null) {
            _hashCode += getMerchantReferenceCode().hashCode();
        }
        if (getRequestID() != null) {
            _hashCode += getRequestID().hashCode();
        }
        if (getDecision() != null) {
            _hashCode += getDecision().hashCode();
        }
        if (getReasonCode() != null) {
            _hashCode += getReasonCode().hashCode();
        }
        if (getMissingField() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMissingField());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMissingField(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getInvalidField() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getInvalidField());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getInvalidField(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRequestToken() != null) {
            _hashCode += getRequestToken().hashCode();
        }
        if (getPurchaseTotals() != null) {
            _hashCode += getPurchaseTotals().hashCode();
        }
        if (getDeniedPartiesMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDeniedPartiesMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDeniedPartiesMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCcAuthReply() != null) {
            _hashCode += getCcAuthReply().hashCode();
        }
        if (getCcCaptureReply() != null) {
            _hashCode += getCcCaptureReply().hashCode();
        }
        if (getCcCreditReply() != null) {
            _hashCode += getCcCreditReply().hashCode();
        }
        if (getCcAuthReversalReply() != null) {
            _hashCode += getCcAuthReversalReply().hashCode();
        }
        if (getCcAutoAuthReversalReply() != null) {
            _hashCode += getCcAutoAuthReversalReply().hashCode();
        }
        if (getCcDCCReply() != null) {
            _hashCode += getCcDCCReply().hashCode();
        }
        if (getEcDebitReply() != null) {
            _hashCode += getEcDebitReply().hashCode();
        }
        if (getEcCreditReply() != null) {
            _hashCode += getEcCreditReply().hashCode();
        }
        if (getEcAuthenticateReply() != null) {
            _hashCode += getEcAuthenticateReply().hashCode();
        }
        if (getPayerAuthEnrollReply() != null) {
            _hashCode += getPayerAuthEnrollReply().hashCode();
        }
        if (getPayerAuthValidateReply() != null) {
            _hashCode += getPayerAuthValidateReply().hashCode();
        }
        if (getTaxReply() != null) {
            _hashCode += getTaxReply().hashCode();
        }
        if (getAfsReply() != null) {
            _hashCode += getAfsReply().hashCode();
        }
        if (getDavReply() != null) {
            _hashCode += getDavReply().hashCode();
        }
        if (getExportReply() != null) {
            _hashCode += getExportReply().hashCode();
        }
        if (getFxRatesReply() != null) {
            _hashCode += getFxRatesReply().hashCode();
        }
        if (getBankTransferReply() != null) {
            _hashCode += getBankTransferReply().hashCode();
        }
        if (getBankTransferRefundReply() != null) {
            _hashCode += getBankTransferRefundReply().hashCode();
        }
        if (getBankTransferRealTimeReply() != null) {
            _hashCode += getBankTransferRealTimeReply().hashCode();
        }
        if (getDirectDebitMandateReply() != null) {
            _hashCode += getDirectDebitMandateReply().hashCode();
        }
        if (getDirectDebitReply() != null) {
            _hashCode += getDirectDebitReply().hashCode();
        }
        if (getDirectDebitValidateReply() != null) {
            _hashCode += getDirectDebitValidateReply().hashCode();
        }
        if (getDirectDebitRefundReply() != null) {
            _hashCode += getDirectDebitRefundReply().hashCode();
        }
        if (getPaySubscriptionCreateReply() != null) {
            _hashCode += getPaySubscriptionCreateReply().hashCode();
        }
        if (getPaySubscriptionUpdateReply() != null) {
            _hashCode += getPaySubscriptionUpdateReply().hashCode();
        }
        if (getPaySubscriptionEventUpdateReply() != null) {
            _hashCode += getPaySubscriptionEventUpdateReply().hashCode();
        }
        if (getPaySubscriptionRetrieveReply() != null) {
            _hashCode += getPaySubscriptionRetrieveReply().hashCode();
        }
        if (getPayPalPaymentReply() != null) {
            _hashCode += getPayPalPaymentReply().hashCode();
        }
        if (getPayPalCreditReply() != null) {
            _hashCode += getPayPalCreditReply().hashCode();
        }
        if (getVoidReply() != null) {
            _hashCode += getVoidReply().hashCode();
        }
        if (getPinlessDebitReply() != null) {
            _hashCode += getPinlessDebitReply().hashCode();
        }
        if (getPinlessDebitValidateReply() != null) {
            _hashCode += getPinlessDebitValidateReply().hashCode();
        }
        if (getPinlessDebitReversalReply() != null) {
            _hashCode += getPinlessDebitReversalReply().hashCode();
        }
        if (getPayPalButtonCreateReply() != null) {
            _hashCode += getPayPalButtonCreateReply().hashCode();
        }
        if (getPayPalPreapprovedPaymentReply() != null) {
            _hashCode += getPayPalPreapprovedPaymentReply().hashCode();
        }
        if (getPayPalPreapprovedUpdateReply() != null) {
            _hashCode += getPayPalPreapprovedUpdateReply().hashCode();
        }
        if (getRiskUpdateReply() != null) {
            _hashCode += getRiskUpdateReply().hashCode();
        }
        if (getDecisionReply() != null) {
            _hashCode += getDecisionReply().hashCode();
        }
        if (getReserved() != null) {
            _hashCode += getReserved().hashCode();
        }
        if (getPayPalRefundReply() != null) {
            _hashCode += getPayPalRefundReply().hashCode();
        }
        if (getPayPalAuthReversalReply() != null) {
            _hashCode += getPayPalAuthReversalReply().hashCode();
        }
        if (getPayPalDoCaptureReply() != null) {
            _hashCode += getPayPalDoCaptureReply().hashCode();
        }
        if (getPayPalEcDoPaymentReply() != null) {
            _hashCode += getPayPalEcDoPaymentReply().hashCode();
        }
        if (getPayPalEcGetDetailsReply() != null) {
            _hashCode += getPayPalEcGetDetailsReply().hashCode();
        }
        if (getPayPalEcSetReply() != null) {
            _hashCode += getPayPalEcSetReply().hashCode();
        }
        if (getPayPalAuthorizationReply() != null) {
            _hashCode += getPayPalAuthorizationReply().hashCode();
        }
        if (getPayPalEcOrderSetupReply() != null) {
            _hashCode += getPayPalEcOrderSetupReply().hashCode();
        }
        if (getPayPalUpdateAgreementReply() != null) {
            _hashCode += getPayPalUpdateAgreementReply().hashCode();
        }
        if (getPayPalCreateAgreementReply() != null) {
            _hashCode += getPayPalCreateAgreementReply().hashCode();
        }
        if (getPayPalDoRefTransactionReply() != null) {
            _hashCode += getPayPalDoRefTransactionReply().hashCode();
        }
        if (getChinaPaymentReply() != null) {
            _hashCode += getChinaPaymentReply().hashCode();
        }
        if (getChinaRefundReply() != null) {
            _hashCode += getChinaRefundReply().hashCode();
        }
        if (getBoletoPaymentReply() != null) {
            _hashCode += getBoletoPaymentReply().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ReplyMessage.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ReplyMessage"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("merchantReferenceCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "merchantReferenceCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestID");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "requestID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("decision");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "decision"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reasonCode");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reasonCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("missingField");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "missingField"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("invalidField");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "invalidField"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("requestToken");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "requestToken"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("purchaseTotals");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "purchaseTotals"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PurchaseTotals"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deniedPartiesMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "deniedPartiesMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DeniedPartiesMatch"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccAuthReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccAuthReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccCaptureReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccCaptureReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCaptureReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccCreditReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccCreditReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCCreditReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccAuthReversalReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccAuthReversalReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAuthReversalReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccAutoAuthReversalReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccAutoAuthReversalReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCAutoAuthReversalReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccDCCReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ccDCCReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "CCDCCReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecDebitReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ecDebitReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECDebitReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecCreditReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ecCreditReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECCreditReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ecAuthenticateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ecAuthenticateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ECAuthenticateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerAuthEnrollReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerAuthEnrollReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthEnrollReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payerAuthValidateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payerAuthValidateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayerAuthValidateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "taxReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "TaxReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("afsReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "afsReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "AFSReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("davReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "davReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DAVReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exportReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "exportReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ExportReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fxRatesReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "fxRatesReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "FXRatesReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRefundReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRefundReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRefundReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bankTransferRealTimeReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "bankTransferRealTimeReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BankTransferRealTimeReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitMandateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitMandateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitMandateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitValidateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitValidateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitValidateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("directDebitRefundReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "directDebitRefundReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DirectDebitRefundReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionCreateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionCreateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionCreateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionUpdateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionUpdateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionUpdateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionEventUpdateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionEventUpdateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionEventUpdateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paySubscriptionRetrieveReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "paySubscriptionRetrieveReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PaySubscriptionRetrieveReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPaymentReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPaymentReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPaymentReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalCreditReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalCreditReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreditReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("voidReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "voidReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "VoidReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitValidateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitValidateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitValidateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pinlessDebitReversalReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "pinlessDebitReversalReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PinlessDebitReversalReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalButtonCreateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalButtonCreateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalButtonCreateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPreapprovedPaymentReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPreapprovedPaymentReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedPaymentReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalPreapprovedUpdateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalPreapprovedUpdateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalPreapprovedUpdateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("riskUpdateReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "riskUpdateReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "RiskUpdateReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("decisionReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "decisionReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "DecisionReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserved");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "reserved"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ReplyReserved"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalRefundReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalRefundReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalRefundReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalAuthReversalReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalAuthReversalReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthReversalReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalDoCaptureReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalDoCaptureReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoCaptureReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcDoPaymentReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcDoPaymentReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcDoPaymentReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcGetDetailsReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcGetDetailsReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcGetDetailsReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcSetReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcSetReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcSetReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalAuthorizationReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalAuthorizationReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalAuthorizationReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalEcOrderSetupReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalEcOrderSetupReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalEcOrderSetupReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalUpdateAgreementReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalUpdateAgreementReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalUpdateAgreementReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalCreateAgreementReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalCreateAgreementReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalCreateAgreementReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payPalDoRefTransactionReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "payPalDoRefTransactionReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "PayPalDoRefTransactionReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chinaPaymentReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "chinaPaymentReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaPaymentReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chinaRefundReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "chinaRefundReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "ChinaRefundReply"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("boletoPaymentReply");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "boletoPaymentReply"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:schemas-cybersource-com:transaction-data-1.49", "BoletoPaymentReply"));
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
