package org.broadleafcommerce.payment.service.type;

import java.util.Hashtable;
import java.util.Map;


public class BLCTransactionType {

    private static final Map<String, BLCTransactionType> types = new Hashtable<String, BLCTransactionType>();

    public static BLCTransactionType AUTHORIZE  = new BLCTransactionType("AUTHORIZE");
    public static BLCTransactionType DEBIT = new BLCTransactionType("DEBIT");
    public static BLCTransactionType AUTHORIZEANDDEBIT = new BLCTransactionType("AUTHORIZEANDDEBIT");
    public static BLCTransactionType CREDIT = new BLCTransactionType("CREDIT");
    public static BLCTransactionType VOIDPAYMENT = new BLCTransactionType("VOIDPAYMENT");
    public static BLCTransactionType BALANCE = new BLCTransactionType("BALANCE");

    //TODO make any other type in BLC behave like this
    public static BLCTransactionType getInstance(String type) {
        return types.get(type);
    }

    private final String type;

    protected BLCTransactionType(String type) {
        this.type = type;
        types.put(type, this);
    }

    public String getType() {
        return type;
    }

    public static void main(String[] items) {
        System.out.println(BLCTransactionType.AUTHORIZE.getType());
    }
}
