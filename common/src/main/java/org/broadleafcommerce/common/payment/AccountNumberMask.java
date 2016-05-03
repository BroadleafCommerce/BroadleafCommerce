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
package org.broadleafcommerce.common.payment;

import java.util.Arrays;
import java.util.List;

public class AccountNumberMask {

    private List<UnmaskRange> ranges;
    private char maskCharacter;

    public AccountNumberMask(List<UnmaskRange> ranges, char maskCharacter) {
        this.ranges = ranges;
        this.maskCharacter = maskCharacter;
    }

    public String mask (String accountNumber) {
        if (accountNumber == null) {
            throw new RuntimeException("account number is null");
        }
        char[] characters = accountNumber.toCharArray();
        char[] newCharacters = new char[characters.length];
        //do mask phase
        Arrays.fill(newCharacters, 0, newCharacters.length, maskCharacter);
        for (UnmaskRange range : ranges) {
            if (range.getPositionType() == UnmaskRange.BEGINNINGTYPE) {
                System.arraycopy(characters, 0, newCharacters, 0, range.getLength());
            } else {
                System.arraycopy(characters, characters.length - range.getLength(), newCharacters, newCharacters.length - range.getLength(), range.getLength());
            }
        }

        return new String(newCharacters);
    }

}
