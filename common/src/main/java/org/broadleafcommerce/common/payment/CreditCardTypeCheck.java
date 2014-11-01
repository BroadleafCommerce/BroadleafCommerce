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
package org.broadleafcommerce.common.payment;

import java.util.ArrayList;

public class CreditCardTypeCheck {

    public static CreditCardType getCreditCardType(String pan) {
        if (pan.matches("(51)?(52)?(53)?(54)?(55)?[0-9]{14}")) {
            return CreditCardType.MASTERCARD;
        }
        if (pan.matches("4[0-9]{15}") || pan.matches("4[0-9]{12}")) {
            return CreditCardType.VISA;
        }
        if (pan.matches("(34)?(37)?[0-9]{13}")) {
            return CreditCardType.AMEX;
        }
        if (pan.matches("(300)?(301)?(302)?(303)?(304)?(305)?[0-9]{11}") || pan.matches("(36)?(38)?[0-9]{12}")) {
            return CreditCardType.DINERSCLUB_CARTEBLANCHE;
        }
        if (pan.matches("6011[0-9]{12}")) {
            return CreditCardType.DISCOVER;
        }
        if (pan.matches("(2014)?(2149)?[0-9]{11}")) {
            return CreditCardType.ENROUTE;
        }
        if (pan.matches("3[0-9]{15}") || pan.matches("(2131)?(1800)?[0-9]{11}")) {
            return CreditCardType.JCB;
        }

        ArrayList<UnmaskRange> ranges = new ArrayList<UnmaskRange>();
        ranges.add(new UnmaskRange(UnmaskRange.BEGINNINGTYPE, 4));
        AccountNumberMask mask = new AccountNumberMask(ranges, 'X');
        throw new RuntimeException("Unable to determine credit card type for pan :" + mask.mask(pan));
    }

}
