package org.broadleafcommerce.profile.web;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomerCookie {
    @Resource
    private String salt;

    private static CustomerCookie instance;

    private CustomerCookie() {}

    public synchronized static CustomerCookie getInstance() {
        if (instance == null) {
            instance = new CustomerCookie();
        }

        return instance;
    }

    public String read(HttpServletRequest request) {
        return CookieUtils.getCookieValue(request, CookieUtils.CUSTOMER_COOKIE_NAME);
    }

    public Long getCustomerIdFromCookie(HttpServletRequest request) {
        String value = read(request);
        return Long.valueOf(value.substring(0, value.indexOf("|")));
    }

    public void write(HttpServletResponse response, Long customerId) {
        try {
            CookieUtils.setCookieValue(response, CookieUtils.CUSTOMER_COOKIE_NAME, customerId + "|" + getHash(customerId.toString()));
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
