/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * @author Jeff Fischer
 * @see {@link BasicPersistenceModule}
 */
public interface DataFormatProvider {

    public SimpleDateFormat getSimpleDateFormatter();

    public DecimalFormat getDecimalFormatter();
    
    /**
     * Formats a aw value from an entity into its string representation used by the system. For instance, this might use
     * the {@link #getDecimalFormatter()} to ensure that BigDecimals only show 2 decimal places or dates are formatted
     * a certain way.
     * 
     * @param value
     * @return
     */
    public String formatValue(Object value);

}
