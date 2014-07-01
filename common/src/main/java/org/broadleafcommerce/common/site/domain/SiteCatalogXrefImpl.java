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
package org.broadleafcommerce.common.site.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


@Entity
@Table(name = "BLC_SITE_CATALOG")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SiteCatalogXrefImpl implements Serializable, SiteCatalogXref {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected SiteCatalogXrefPK siteCatalogXrefPK = new SiteCatalogXrefPK();
    
    public SiteCatalogXrefPK getSiteCatalogXrefPK() {
        return siteCatalogXrefPK;
    }

    public void setSiteCatalogXrefPK(SiteCatalogXrefPK siteCatalogXrefPK) {
        this.siteCatalogXrefPK = siteCatalogXrefPK;
    }
    
    @Override
    public Site getSite() {
        return getSiteCatalogXrefPK().getSite();
    }
    
    @Override
    public void setSite(Site site) {
        getSiteCatalogXrefPK().setSite(site);
    }
    
    @Override
    public Catalog getCatalog() {
        return getSiteCatalogXrefPK().getCatalog();
    }
    
    @Override
    public void setCatalog(Catalog catalog) {
        getSiteCatalogXrefPK().setCatalog(catalog);
    }
    
}
