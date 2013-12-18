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
package org.broadleafcommerce.common.util;

public class Mod43CheckDigitUtil {

    private final static String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-. $/+%";

    public static boolean isValidCheckedValue(String value) {
        boolean valid = false;
        if (value != null && !"".equals(value)) {
            String code = value.substring(0, value.length() - 1);
            char checkDigit = value.substring(value.length() - 1).charAt(0);
            try {
                if (generateCheckDigit(code) == checkDigit) {
                    valid = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return valid;
    }

    public static char generateCheckDigit(String data) {
        // MOD 43 check digit - take the acsii value of each digit, sum them up, divide by 43. the remainder is the check digit (in ascii)
        int sum = 0;
        for (int i = 0; i < data.length(); ++i) {
            sum += CHARSET.indexOf(data.charAt(i));
        }
        int remainder = sum % 43;
        return CHARSET.charAt(remainder);
    }

    public static void main(String[] args) {
        try {
            System.out.println(generateCheckDigit("TEACH000012345"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(isValidCheckedValue("TEACH000012345B"));
    }
}
