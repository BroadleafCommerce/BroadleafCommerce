/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.common.site.domain;

import org.broadleafcommerce.common.sandbox.domain.SandBox;

import java.io.Serializable;

/**
 * Created by bpolster.
 */
public interface Site extends Serializable {

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
     * Intended to be used along with the #getSiteIdentifierValue()
     * by the SiteResolver to determine if this is the current site.
     *
     * Example type usage could be HOSTNAME, IP-ADDRESS, URL-PARAMETER.
     * Custom SiteResolvers can be written to determine the current Site.
     *
     * @return
     */
    public String getSiteIdentifierType();

    /**
     * Sets the site identifier type.
     * @see #getSiteIdentifierType()
     * @param siteIdentifierType
     */
    public void setSiteIdentifierType(String siteIdentifierType);

    /**
     * Used along with #getSiteIdentiferType() to determine the current
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
     * If null, then this is a single-site installation.    Otherwise,
     * each site must define it's production sandbox so that data can
     * be properly segmented.
     *
     * @return
     */
    public SandBox getProductionSandbox();

    /**
     * Sets the production sandbox.   Typically configured via the
     * database.
     *
     * @see #getProductionSandbox();
     * @param sandbox
     */
    public void setProductionSandbox(SandBox sandbox);
}
