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
package org.broadleafcommerce.common.web.resource;

import org.broadleafcommerce.common.classloader.release.ThreadLocalManager;
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

    /**
     * Creates a BroadleafRequestContext with supported values populated
     * @see #establishThinRequestContextInternal(boolean, boolean)
     */
    public void establishThinRequestContext() {
        establishThinRequestContextInternal(true, true);
    }

    /**
     * Creates a BroadleafRequestContext without a Sandbox
     * @see #establishThinRequestContextInternal(boolean, boolean)
     */
    public void establishThinRequestContextWithoutSandBox() {
        establishThinRequestContextInternal(true, false);
    }

    /**
     * Creates a BroadleafRequestContext without a Theme or Sandbox
     * @see #establishThinRequestContextInternal(boolean, boolean)
     */
    public void establishThinRequestContextWithoutThemeOrSandbox() {
        establishThinRequestContextInternal(false, false);
    }

    /**
     * Adds request and site to the BroadleafRequestContext
     * 
     * If includeTheme is true then also adds the Theme.
     * If includeSandBox is true then also adds the SandBox.
     * 
     * @param includeTheme
     * @param includeSandBox
     */
    protected void establishThinRequestContextInternal(boolean includeTheme, boolean includeSandBox) {
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
            if (includeSandBox) {
                brc.setSandBox(sbResolver.resolveSandBox(wr, brc.getNonPersistentSite()));
            }
            brc.setDeployBehavior(deployBehaviorUtil.isProductionSandBoxMode() ? DeployBehavior.CLONE_PARENT : DeployBehavior.OVERWRITE_PARENT);
        }

        if (includeTheme) {
            if (brc.getTheme() == null) {
                brc.setTheme(themeResolver.resolveTheme(wr));
            }
        }
    }

    public void clearThinRequestContext() {
        ThreadLocalManager.remove();
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
