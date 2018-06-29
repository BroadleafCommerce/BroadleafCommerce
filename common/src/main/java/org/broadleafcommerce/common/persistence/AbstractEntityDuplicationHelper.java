package org.broadleafcommerce.common.persistence;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements {@link #getCopyHints()} and {@link #addCopyHint(String, String)}, 
 * using a {@link HashMap} as the underlying data structure for storing the hints. Also provides a
 * helper method {@link #getCopySuffix()} for the usual use-case of changing the name of the 
 * duplicated entity.
 * 
 * @author Nathan Moore (nathanmoore).
 */
public abstract class AbstractEntityDuplicationHelper<T> implements EntityDuplicationHelper<T> {
    
    protected Map<String, String> copyHints = new HashMap<>();
    
    protected final Environment env;
    
    public AbstractEntityDuplicationHelper(final Environment environment) {
        this.env = environment;
    }

    /**
     * Defaults to " - Copy" but can be overridden using 
     * {@code admin.entity.duplication.suffix.default}. 
     * 
     * @return suffix to append to the name/identifier of the entity copy
     */
    protected String getCopySuffix() {
        return env.getProperty("admin.entity.duplication.suffix.default", String.class, " - Copy");
    }
    
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
