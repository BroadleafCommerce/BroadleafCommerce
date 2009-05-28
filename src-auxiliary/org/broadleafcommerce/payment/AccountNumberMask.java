package org.broadleafcommerce.payment;

import java.util.ArrayList;
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

    public static void main( String[] args ) {
        ArrayList<UnmaskRange> ranges = new ArrayList<UnmaskRange>();
        ranges.add(new UnmaskRange(UnmaskRange.BEGINNINGTYPE, 4));
        ranges.add(new UnmaskRange(UnmaskRange.ENDTYPE, 4));
        AccountNumberMask mask = new AccountNumberMask(ranges, 'X');
        System.out.println("Card: " + mask.mask( "1111111111111111" ) );
        System.out.println("Card: " + mask.mask( "111111111111111" ) );
    }
}
