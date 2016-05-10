/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.common;

import org.broadleafcommerce.cms.file.service.StaticAssetService;


/**
 * Exception thrown by the {@link StaticAssetService} indicating that the asset requested does not exist.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class AssetNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6349160176427682630L;

    public AssetNotFoundException() {
        //do nothing
    }

    public AssetNotFoundException(Throwable cause) {
        super(cause);
    }

    public AssetNotFoundException(String message) {
        super(message);
    }

    public AssetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
