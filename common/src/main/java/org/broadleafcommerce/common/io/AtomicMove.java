/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.common.io;

import java.io.File;
import java.io.IOException;

/**
 * Utility class that utilizes NIO support for atomic filesystem move operations. The attempts to guarantee no intra-jvm
 * or extra-jvm clashing on the same file. This is useful when it's possible multiple operations may be trying to create
 * the same file.
 *
 * @author Jeff Fischer
 */
public interface AtomicMove {

    void replaceExisting(File src, File dest) throws IOException;

}
