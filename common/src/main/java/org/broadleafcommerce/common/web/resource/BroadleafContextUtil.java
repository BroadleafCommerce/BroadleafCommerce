/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.resource;

import org.broadleafcommerce.common.util.DeployBehaviorUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafSandBoxResolver;
import org.broadleafcommerce.common.web.BroadleafSiteResolver;
import org.broadleafcommerce.common.web.BroadleafThemeResolver;
import org.broadleafcommerce.common.web.DeployBehavior;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * Some resource handlers need a valid site, theme, or sandbox to be available when serving request.
 * 
 * <p>
 * This component provides the {@link #establishThinRequestContext()} method for that purpose.  
 *
 * @author bpolster
 *
 */
@Service("blBroadleafContextUtil")
public class BroadleafContextUtil {
    
    @javax.annotation.Resource(name = "blSiteResolver")
    protected BroadleafSiteResolver siteResolver;
    
    @javax.annotation.Resource(name = "blSandBoxResolver")
    protected BroadleafSandBoxResolver sbResolver;
    
    @javax.annotation.Resource(name = "blThemeResolver")
    protected BroadleafThemeResolver themeResolver;

    @javax.annotation.Resource(name = "blDeployBehaviorUtil")
    protected DeployBehaviorUtil deployBehaviorUtil;

    protected boolean versioningEnabled = false;

    public void establishThinRequestContext(boolean includeTheme) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();

        if (brc.getRequest() == null) {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = req.getSession(false);
            SecurityContext ctx = readSecurityContextFromSession(session);
            if (ctx != null) {
                SecurityContextHolder.setContext(ctx);
            }
            brc.setRequest(req);
        }

        WebRequest wr = brc.getWebRequest();

        if (brc.getNonPersistentSite() == null) {
            brc.setNonPersistentSite(siteResolver.resolveSite(wr, true));
            brc.setSandBox(sbResolver.resolveSandBox(wr, brc.getNonPersistentSite()));
            brc.setDeployBehavior(deployBehaviorUtil.isProductionSandBoxMode() ? DeployBehavior.CLONE_PARENT : DeployBehavior.OVERWRITE_PARENT);
        }

        if (includeTheme) {
            if (brc.getTheme() == null) {
                brc.setTheme(themeResolver.resolveTheme(wr));
            }
        }
    }

    public void clearThinRequestContext() {
        BroadleafRequestContext.setBroadleafRequestContext(null);
    }

    protected String getContextName(HttpServletRequest request) {
        String contextName = request.getServerName();
        int pos = contextName.indexOf('.');
        if (pos >= 0) {
            contextName = contextName.substring(0, contextName.indexOf('.'));
        }
        return contextName;
    }

    // **NOTE** This method is lifted from HttpSessionSecurityContextRepository
    protected SecurityContext readSecurityContextFromSession(HttpSession httpSession) {
        if (httpSession == null) {
            return null;
        }

        Object ctxFromSession = httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (ctxFromSession == null) {
            return null;
        }

        if (!(ctxFromSession instanceof SecurityContext)) {
            return null;
        }

        return (SecurityContext) ctxFromSession;
    }
    
    

}
