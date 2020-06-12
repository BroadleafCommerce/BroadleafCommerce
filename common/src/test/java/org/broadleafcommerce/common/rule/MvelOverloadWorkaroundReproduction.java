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
package org.broadleafcommerce.common.rule;

/**
 * Run the test case as a java main application in a new JVM. This seems to be required to cause the variability in the ordering
 * of the call to Class#getMethods on SelectizeCollectionUtils. In this case, we should be using the refactor of
 * {@link SelectizeCollectionUtils} that no longer has method overloading for #intersection, which should avoid the mvel
 * issue altogether.
 * </p>
 * See {@link Class#getMethods()} for mention of the undetermined ordering behavior.
 *
 * @author Jeff Fischer
 */
public class MvelOverloadWorkaroundReproduction {

    public static void main(String[] items) {
        MvelTestUtils.exerciseWorkaround();
    }

}
