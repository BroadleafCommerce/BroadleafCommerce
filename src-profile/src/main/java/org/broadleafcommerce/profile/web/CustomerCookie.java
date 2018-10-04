/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.web;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomerCookie {

    private static CustomerCookie instance;

    @Resource
    private String salt;
    private CookieUtils cookieUtils;

    private CustomerCookie(CookieUtils cookieUtils) {
        this.cookieUtils = cookieUtils;
    }

    public synchronized static CustomerCookie getInstance(CookieUtils cookieUtils) {
        if (instance == null) {
            instance = new CustomerCookie(cookieUtils);
        }

        return instance;
    }

    public String read(HttpServletRequest request) {
        return cookieUtils.getCookieValue(request, CookieUtils.CUSTOMER_COOKIE_NAME);
    }

    public Long getCustomerIdFromCookie(HttpServletRequest request) {
        String value = read(request);
        return Long.valueOf(value.substring(0, value.indexOf("|")));
    }

    public void write(HttpServletResponse response, Long customerId) {
        try {
            cookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, customerId + "|" + getHash(customerId.toString()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isValid(HttpServletRequest request) {
        String value = read(request);
        if (value != null) {
            String values[] = value.split("|");
            String customerId = values[0];
            String encryptedCustomerId = values[1];

            try {
                byte[] hash;
                hash = getHash(customerId);

                return hash.equals(encryptedCustomerId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public byte[] getHash(String plaintext) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt.getBytes("UTF-8"));

        return digest.digest(plaintext.getBytes("UTF-8"));
    }

}
