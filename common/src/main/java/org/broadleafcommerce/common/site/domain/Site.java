/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.common.site.domain;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.site.service.type.SiteResolutionType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface Site extends Serializable, Status {

    /**
     * Unique/internal id for a site.
     * @return
     */
    public Long getId();

    /**
     * Sets the internal id for a site.
     * @param id
     */
    public void setId(Long id);

    /**
     * The display name for a site.
     * @return
     */
    public String getName();

    /**
     * Sets the displayName for a site.
     * @param name
     */
    public void setName(String name);

    /**
     * @deprecated use {@link #getSiteResolutionType()}
     * Intended to be used along with the #getSiteIdentifierValue()
     * by the SiteResolver to determine if this is the current site.
     *
     * @return
     */
    @Deprecated
    public String getSiteIdentifierType();

    /**
     * @deprecated Use {@link #setSiteResolutionType(SiteResolutionType)}
     * Sets the site identifier type.
     * @see #getSiteIdentifierType()
     * @param siteIdentifierType
     */
    @Deprecated
    public void setSiteIdentifierType(String siteIdentifierType);

    /**
     * Used along with {@link #getSiteResolutionType()} to determine the current
     * Site for a given request.
     *
     * @return
     */
    public String getSiteIdentifierValue();

    /**
     *
     * @param siteIdentifierValue
     */
    public void setSiteIdentifierValue(String siteIdentifierValue);

    /**
     * Intended to be used along with the #getSiteIdentifierValue()
     * by an implementation of SiteResolver to determine 
     * if this is the current site.   
     *
     * @return
     */
    public SiteResolutionType getSiteResolutionType();

    /** 
     * Sets the site resolution type.
     *
     * @param siteResolutionType
     * @see #getSiteResolutionType()
     */
    public void setSiteResolutionType(SiteResolutionType siteResolutionType);

    /**
     * Retrieve a list of product, category and offer groupings that
     * this site has access to
     *
     * @return a list of catalog groupings
     * @deprecated Not used by Broadleaf - scheduled to remove on or after 3.3
     */
    @Deprecated
    public List<Catalog> getCatalogs();

    /**
     * Set the list of product, category and offer groupings that
     * this site has access to
     *
     * @param catalogs a list of catalog groupings
     * @deprecated Not used by Broadleaf - scheduled to remove on or after 3.3
     */
    @Deprecated
    public void setCatalogs(List<Catalog> catalogs);

    /**
     * used for default locale
     *
     * @return Locale
     */
    public Locale getDefaultLocale();

    /**
     * Sets the site default locale.
     *
     * @param defaultLocale
     */
    public void setDefaultLocale(Locale defaultLocale);

    /**
     * Retrieve an deep copy of this site. Not bound by
     * entity manager scope.
     *
     * @return a deep copy of this site
     */
    public Site clone();

    public ArchiveStatus getArchiveStatus();

    public boolean isDeactivated();

    public void setDeactivated(boolean deactivated);

    /**
     * This method will return true when the given site was created based on a template.
     * 
     * @return whether or not this site is a TemplateSite
     * @deprecated Not used by Broadleaf - scheduled to remove on or after 3.3     
     */
    @Deprecated
    public boolean isTemplateSite();
}
