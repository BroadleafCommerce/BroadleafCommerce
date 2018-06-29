package org.broadleafcommerce.common.persistence;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.util.Map;

/**
 * Provides additional metadata and performs final modifications for an entity before persistence.
 * 
 * In order to perform duplication using {@link EntityDuplicator}, an 
 * {@code EntityDuplicationHelper} must be made for a specific entity.
 * 
 * @author Nathan Moore (nathanmoore).
 */
public interface EntityDuplicationHelper<T> {

    boolean canHandle(MultiTenantCloneable candidate);

    /**
     * @return Hints used to fine tune copying - generally support for hints is included in 
     * {@link org.broadleafcommerce.common.copy.MultiTenantCloneable#createOrRetrieveCopyInstance(org.broadleafcommerce.common.copy.MultiTenantCopyContext)} implementations.
     */
    Map<String, String> getCopyHints();
    
    void addCopyHint(final String name, final String hint);

    void modifyInitialDuplicateState(T copy);
}
