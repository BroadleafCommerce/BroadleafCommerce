/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
