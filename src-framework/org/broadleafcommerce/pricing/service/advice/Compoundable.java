package org.broadleafcommerce.pricing.service.advice;

public interface Compoundable<T> {

    public void clearCache();

    public T getLatestItem();

}
