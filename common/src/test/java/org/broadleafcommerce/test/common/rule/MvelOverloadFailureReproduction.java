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
package org.broadleafcommerce.test.common.rule;

/**
 * Run the test case as a java main application in a new JVM. This seems to be required to cause the variability in the ordering
 * of the call to Class#getMethods on SelectizeCollectionUtils. For our case to cause the compiled expression corruption, we
 * must end up with an invocation of Class#getMethods that returns the #intersection(String, Iterable) Method positioned
 * in the methods array before the #intersection(Iterable, Iterable) version of the method. Once you have a JVM in place, the
 * ordering seems consistent, so you have to start a new JVM to hope to see the variable ordering phenomenon.
 * See {@link MvelTestUtils} for the examples of the overloaded static intersection method implementations.
 * </p>
 * See {@link Class#getMethods()} for mention of the undetermined ordering behavior.
 *
 * @author Jeff Fischer
 */
public class MvelOverloadFailureReproduction {

    public static void main(String[] items) {
        MvelTestUtils.exerciseFailure();
    }

}
