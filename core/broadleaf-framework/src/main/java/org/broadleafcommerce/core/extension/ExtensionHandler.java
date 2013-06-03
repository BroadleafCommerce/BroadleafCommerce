package org.broadleafcommerce.core.extension;


/**
 * An extension handler represents a generic pattern used in BroadleafCommerce when an out-of-box service
 * with complex logic provides implementation hooks.  
 * 
 * The pattern is primarily used internally by Broadleaf as a mechanism to provide extension points for 
 * Broadleaf modules.
 * 
 * Consumers of BroadleafCommerce framework typically would not need to use this pattern and instead would opt. 
 * for more typical extension patterns including overriding or extending the actual component for which 
 * alternate behavior is desired.
 * 
 * ExtensionHandler api methods should always return an instance of {@link ExtensionResultStatusType}.
 * 
 * @author bpolster
 */
public interface ExtensionHandler {

    /**
     * Determines the priority of this extension handler.
     * @return
     */
    public int getPriority();

    /**
     * If false, the ExtensionManager should skip this Handler.
     * @return
     */
    public boolean isEnabled();
}
