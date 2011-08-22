package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.cms.site.domain.Site;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface Page {
    public Long getId();

    public void setId(Long id);

    public PageFolder getParentFolder();

    public void setParentFolder(PageFolder parentFolder);

    public Site getSite();

    public void setSite(Site site);

    public String getFullUrl();

    public void setFullUrl(String fullUrl);

    public String getPageFileName();

    public void setPageFileName(String pageFileName);

    public PageTemplate getPageTemplate();

    public void setPageTemplate(PageTemplate pageTemplate);

    public String getMetaKeywords();

    public void setMetaKeywords(String metaKeywords);

    public String getMetaDescription();

    public void setMetaDescription(String metaDescription);

    public Map<String, PageField> getPageFields();

    public void setPageFields(Map<String, PageField> pageFields);

    public Boolean getDeletedFlag();

    public void setDeletedFlag(Boolean deletedFlag);
}
