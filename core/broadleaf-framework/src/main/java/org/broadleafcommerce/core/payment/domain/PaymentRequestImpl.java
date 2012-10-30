/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAYMENT_PROPERTIES")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PaymentInfoImpl_basePaymentInfo")
public class PaymentRequestImpl implements PaymentRequest {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PaymentRequestId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PaymentRequestId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PaymentRequestImpl", allocationSize = 50)
    @Column(name = "PAYMENT_ID")
    protected Long id;

    @Column(name = "MERCHANT_ID", nullable = false)
    @AdminPresentation(friendlyName = "PaymentRequestImpl_MerchantId", order = 2, group = "PaymentRequestImpl_Description", prominent = true)
    protected String merchantId;

    @Column(name = "PUBLIC_KEY", nullable = false)
    @AdminPresentation(friendlyName = "PaymentRequestImpl_MerchantId", order = 2, group = "PaymentRequestImpl_Description", prominent = true)
    protected String publicKey;

    @Column(name = "PRIVATE_KEY", nullable = false)
    @AdminPresentation(friendlyName = "PaymentRequestImpl_MerchantId", order = 2, group = "PaymentRequestImpl_Description", prominent = true)
    protected String privateKey;

    @Column(name = "REDIRECT_URL", nullable = false)
    @AdminPresentation(friendlyName = "PaymentRequestImpl_MerchantId", order = 2, group = "PaymentRequestImpl_Description", prominent = true)
    protected String redirectUrl;

    @Column(name = "ENVIRONMENT", nullable = false)
    @AdminPresentation(friendlyName = "PaymentRequestImpl_MerchantId", order = 2, group = "PaymentRequestImpl_Description", prominent = true)
    protected String environment;
   

    @Column(name = "KEY", nullable = false)
    @AdminPresentation(friendlyName = "PriceListImpl_Currency_Code", order=1, group = "PriceListImpl_Details")
    private String key;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PaymentRequestImpl other = (PaymentRequestImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (merchantId == null) {
            if (other.merchantId != null) {
                return false;
            }
        } else if (!merchantId.equals(other.merchantId)) {
            return false;
        }
        if (merchantId == null) {
            if (other.merchantId != null) {
                return false;
            }
        } else if (!merchantId.equals(other.merchantId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((merchantId == null) ? 0 : merchantId.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#getMerchantId()
     */
    @Override
    public String getMerchantId() {
        return merchantId;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#setMerchantId(java.lang.String)
     */
    @Override
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#getPublicKey()
     */
    @Override
    public String getPublicKey() {
        return publicKey;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#setPublicKey(java.lang.String)
     */
    @Override
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#getPrivateKey()
     */
    @Override
    public String getPrivateKey() {
        return privateKey;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#setPrivateKey(java.lang.String)
     */
    @Override
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#getRedirectUrl()
     */
    @Override
    public String getRedirectUrl() {
        return redirectUrl;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#setRedirectUrl(java.lang.String)
     */
    @Override
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#getEnvironment()
     */
    @Override
    public String getEnvironment() {
        return environment;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.PaymentRequest#setEnvironment(java.lang.String)
     */
    @Override
    public void setEnvironment(String environment) {
        this.environment = environment;
    }
    @Override
    public String getKey() {
        return key;
    }
    @Override
    public void setKey(String key) {
        this.key = key;
    }

}
