/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.util.tenant;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
import org.broadleafcommerce.common.site.domain.Site;

import java.util.Stack;

/**
 * A thread local context to store the unique name for this request thread.
 *
 * @author Jeff Fischer
 */
public class IdentityUtilContext extends Stack<IdentityUtilContext> {

    private static final long serialVersionUID = 1819548808605962648L;

    private static final ThreadLocal<IdentityUtilContext> IDENTITYUTILCONTEXT = ThreadLocalManager.createThreadLocal(IdentityUtilContext.class);

    protected Site identifier;
    
    public static IdentityUtilContext getUtilContext() {
        IdentityUtilContext anyIdentityUtilContext = IDENTITYUTILCONTEXT.get();
        if (anyIdentityUtilContext != null) {
            return anyIdentityUtilContext.peek();
        }
        return anyIdentityUtilContext;
    }

    public static void setUtilContext(IdentityUtilContext identityUtilContext) {
        IdentityUtilContext anyIdentityUtilContext = IDENTITYUTILCONTEXT.get();
        if (anyIdentityUtilContext != null) {
            if (identityUtilContext == null) {
                anyIdentityUtilContext.pop();
                return;
            } else {
                anyIdentityUtilContext.push(identityUtilContext);
                return;
            }
        }
        if (identityUtilContext == null) {
            ThreadLocalManager.remove(IDENTITYUTILCONTEXT);
        } else {
            identityUtilContext.push(identityUtilContext);
            IDENTITYUTILCONTEXT.set(identityUtilContext);
        }
    }

    public Site getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Site identifier) {
        this.identifier = identifier;
    }
}
