package org.broadleafcommerce.payment;

import java.util.Arrays;

public class AccountNumberMask {

    private int startPosition;
    private int endPosition;
    private char maskCharacter;

    public AccountNumberMask(int startPosition, int endPosition, char maskCharacter) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.maskCharacter = maskCharacter;
    }

    public String mask (String accountNumber) {
        if (accountNumber == null) {
            throw new RuntimeException("account number is null");
        }
        if (startPosition >= endPosition) {
            throw new RuntimeException("Start position must be less than end position");
        }
        if (startPosition >= accountNumber.length() || endPosition > accountNumber.length()) {
            throw new RuntimeException("startPosition must be less than account number length and end position must be less than or equal to account number length");
        }
        char[] characters = accountNumber.toCharArray();
        char[] newCharacters = new char[characters.length];
        //do initial phase
        if (startPosition > 0) {
            System.arraycopy(characters, 0, newCharacters, 0, startPosition);
        }
        //do mask phase
        Arrays.fill(newCharacters, startPosition, endPosition, maskCharacter);
        //do final phase
        if (endPosition < accountNumber.length()) {
            System.arraycopy(characters, endPosition, newCharacters, endPosition, accountNumber.length() - endPosition);
        }

        return new String(newCharacters);
    }

    public static void main( String[] args ) {
        AccountNumberMask mask = new AccountNumberMask(4, 12, 'X');
        System.out.println("Card: " + mask.mask( "1111111111111111" ) );
    }
}
