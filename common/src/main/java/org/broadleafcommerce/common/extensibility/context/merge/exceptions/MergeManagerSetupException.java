/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.extensibility.context.merge.exceptions;

/**
 * This exception is thrown when a problem is encountered during
 * the MergeManager initialization
 * 
 * @author jfischer
 *
 */
public class MergeManagerSetupException extends Exception {

    private static final long serialVersionUID = 1L;
    
    public MergeManagerSetupException() {
        super();
    }

    public MergeManagerSetupException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public MergeManagerSetupException(String arg0) {
        super(arg0);
    }

    public MergeManagerSetupException(Throwable arg0) {
        super(arg0);
    }

}
