package org.broadleafcommerce.core.extension;


/**
 * Base {@link ExtensionHandler} class that provide basic extension handler properties including
 * priority (which drives the execution order of handlers) and enabled (which if false informs the
 * manager to skip this handler).
 * 
 * @author bpolster
 */
public abstract class AbstractExtensionHandler implements ExtensionHandler {

    private int priority;
    private boolean enabled = true;

    /**
     * Determines the priority of this extension handler.
     * @return
     */
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
