/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
