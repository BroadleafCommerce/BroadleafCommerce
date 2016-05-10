/*
 * #%L
 * broadleaf-enterprise
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
package org.broadleafcommerce.openadmin.server.service.module;

import org.broadleafcommerce.openadmin.dto.PersistencePackage;

import java.util.Map;

/**
 * Useful in concert with the enterprise module. Keeps track of references for ADD operations across sandboxes.
 *
 * @author Jeff Fischer
 */
public interface ReconstituteForAddHandler {

    /**
     * Must rematch up any items that are dependent on a previous add. For example,
     * if you add a product, and then add a cross sale product, the cross sale will
     * have an incorrect reference to the product state before the replay unless
     * you update the cross sale to the new product value.
     *
     * @param library holds any previous adds
     * @param replayPackage the change request
     */
    void reconstitutePreviousAddForReplay(Map<Class<?>, Map<String,String>> library, PersistencePackage replayPackage);

    /**
     * Must rematch up any items that are dependent on a previous add. For example,
     * if you add an offer and promote, and then another user edits a property on this
     * add, the edit will have an incorrect reference to the offer state before the
     * replay unless you update the edit to the new offer value.
     *
     * @param library holds any previous adds
     * @param replayPackage the change request
     */
    void reconstitutePreviousAddForUpdateReplay(Map<Class<?>, Map<String, String>> library, PersistencePackage replayPackage);

}
