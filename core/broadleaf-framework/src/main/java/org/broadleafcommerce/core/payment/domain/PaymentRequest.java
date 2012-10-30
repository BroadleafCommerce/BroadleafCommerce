
package org.broadleafcommerce.core.payment.domain;

public interface PaymentRequest {

    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract String getMerchantId();

    public abstract void setMerchantId(String merchantId);

    public abstract String getPublicKey();

    public abstract void setPublicKey(String publicKey);

    public abstract String getPrivateKey();

    public abstract void setPrivateKey(String privateKey);

    public abstract String getRedirectUrl();

    public abstract void setRedirectUrl(String redirectUrl);

    public abstract String getEnvironment();

    public abstract void setEnvironment(String environment);

    public void setKey(String key);
    
    public String getKey();
}