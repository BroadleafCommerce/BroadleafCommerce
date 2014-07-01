package org.broadleafcommerce.common.site.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Jeff Fischer
 */
@Embeddable
public class SiteCatalogXrefPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = SiteImpl.class, optional = false)
    @JoinColumn(name = "SITE_ID")
    protected Site site = new SiteImpl();

    @ManyToOne(targetEntity = CatalogImpl.class, optional = false)
    @JoinColumn(name = "CATALOG_ID")
    protected Catalog catalog = new CatalogImpl();

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (SiteCatalogXrefPK.class.isAssignableFrom(obj.getClass())) {
            SiteCatalogXrefPK that = (SiteCatalogXrefPK) obj;
            return new EqualsBuilder()
                    .append(catalog, that.catalog)
                    .append(site, that.site)
                    .build();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(catalog)
            .append(site)
            .build();
    }

}
