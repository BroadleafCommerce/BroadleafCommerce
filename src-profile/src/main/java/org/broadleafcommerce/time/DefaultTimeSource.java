package org.broadleafcommerce.time;

public class DefaultTimeSource implements TimeSource {

    public long timeInMillis() {
        return System.currentTimeMillis();
    }
}
