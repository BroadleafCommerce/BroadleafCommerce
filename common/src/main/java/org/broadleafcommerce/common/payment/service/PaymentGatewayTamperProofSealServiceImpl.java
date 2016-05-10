/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */

package org.broadleafcommerce.common.payment.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>This default implementation encodes the seal using a URL-Safe Base64 encoder
 * and 'HmacSHA1'</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPaymentGatewayTamperProofSealService")
public class PaymentGatewayTamperProofSealServiceImpl implements PaymentGatewayTamperProofSealService {

    @Override
    public String createTamperProofSeal(String secretKey, String customerId, String orderId)
            throws NoSuchAlgorithmException, InvalidKeyException {

        //Create a URL-Safe Base64 encoder as some of these may get passed back as URL GET parameters
        Base64 encoder = new Base64(true);
        Mac sha1Mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec publicKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        sha1Mac.init(publicKeySpec);
        String customerOrderString = customerId + orderId;
        byte[] publicBytes = sha1Mac.doFinal(customerOrderString.getBytes());
        String publicDigest = encoder.encodeToString(publicBytes);

        return publicDigest.replaceAll("\\r|\\n", "");
    }

    @Override
    public Boolean verifySeal(String seal, String secretKey, String customerId, String orderId)
            throws InvalidKeyException, NoSuchAlgorithmException {
        Boolean valid = false;
        String constructedSeal = createTamperProofSeal(secretKey, customerId, orderId);

        if (seal != null && seal.equals(constructedSeal)) {
            valid = true;
        }

        return valid;
    }

}
