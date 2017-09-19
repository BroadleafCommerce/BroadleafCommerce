/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.search.index;

import java.util.List;

/**
 * Simple interface to allow batch reads to the end of the batch.
 * 
 * @author Kelly Tisdell
 *
 * @param <T>
 */
public interface BatchReader<T> {

    /**
     * Reads a list of data in batch.  Each call to this method will advance a cursor and will return the next batch.  The 
     * size of the batch depends on the implementation.  A call to reset will reset the cursor to the beginning.  When there 
     * is no more to read, this method MUST return an empty list or a null value.
     * 
     * @return
     */
    public List<T> readBatch();
    
    /**
     * Resets the cursor to the beginning so that subsequent calls to readBatch() will return values from the beginning.
     */
    public void reset();
    
    /**
     * Indicates if the cursor is at the end of the batch and no more items can be returned until the reset method is called. 
     * If this method returns true, the readBatch method should return null or an empty list.
     * @return
     */
    public boolean isComplete();
}
