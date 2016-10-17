package org.broadleafcommerce.core.catalog.domain;

//TODO 2204 Change this to use the Vendor domain object. Will require a weave into ProductImpl from MT.
/**
 * @author Jeff Fischer
 */
public interface VendorRelated {

    String getVendor();

    void setVendor(String vendor);

}
