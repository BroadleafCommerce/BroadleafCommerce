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
package org.broadleafcommerce.common.web.expression;

import org.apache.commons.beanutils.PropertyUtils;
import org.broadleafcommerce.common.crossapp.service.CrossAppAuthService;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * Exposes the {@link BroadleafRequestContext} to the Thymeleaf expression context
 * 
 * @author Andre Azzolini (apazzolini)
 */
@Component("blBRCVariableExpression")
@ConditionalOnTemplating
public class BRCVariableExpression implements BroadleafVariableExpression {
    
    @Autowired(required = false)
    @Qualifier("blCrossAppAuthService")
    protected CrossAppAuthService crossAppAuthService;

    @Override
    public String getName() {
        return "brc";
    }
    
    public SandBox getSandbox() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            return brc.getSandBox();
        }
        return null;
    }

    public Site getSite() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            return brc.getNonPersistentSite();
        }
        return null;
    }

    public Site getCurrentProfile() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            return brc.getCurrentProfile();
        }
        return null;
    }

    public Catalog getCurrentCatalog() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            return brc.getCurrentCatalog();
        }
        return null;
    }
    
    public Date getCurrentTime() {
        return SystemTime.asDate(true);
    }
    
    public Object get(String propertyName) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            try {
                return PropertyUtils.getProperty(brc, propertyName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public boolean isCsrMode() {
        return crossAppAuthService == null ? false : crossAppAuthService.hasCsrPermission();
    }

    public boolean isQuoteMode() {
        return crossAppAuthService == null ? false : crossAppAuthService.hasQuotePermission();
    }

    public boolean isSandboxMode() {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        return (brc == null) ? false : (brc.getSandBox() != null);
    }
    
    public Object getAdditionalProperty(String propertyName) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null) {
            return brc.getAdditionalProperties().get(propertyName);
        }
        return null;
    }

}
