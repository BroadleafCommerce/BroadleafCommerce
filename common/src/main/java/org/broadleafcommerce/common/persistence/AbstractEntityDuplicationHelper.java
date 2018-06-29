package org.broadleafcommerce.common.persistence;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements {@link #getCopyHints()} and {@link #addCopyHint(String, String)}, 
 * using a {@link HashMap} as the underlying data structure for storing the hints.
 * 
 * @author Nathan Moore (nathanmoore).
 */
public abstract class AbstractEntityDuplicationHelper<T> implements EntityDuplicationHelper<T> {
    
    protected Map<String, String> copyHints = new HashMap<>();
    
    @Override 
    public abstract boolean canHandle(final MultiTenantCloneable candidate);

    @Override 
    public Map<String, String> getCopyHints() {
        return copyHints;
    }

    @Override 
    public void addCopyHint(final String name, final String hint) {
        copyHints.put(name, hint);
    }

    @Override 
    public abstract void modifyInitialDuplicateState(final T copy);
}
