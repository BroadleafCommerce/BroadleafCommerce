package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.openadmin.server.domain.SandBox;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface Page extends PageFolder {

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

    public Boolean getArchivedFlag();

    public void setArchivedFlag(Boolean archivedFlag);

    public SandBox getSandbox();

    public void setSandbox(SandBox sandbox);

    public Long getOriginalPageId();

    public void setOriginalPageId(Long originalPageId);

    public Page cloneEntity();

    public String getFullUrl();

    public void setFullUrl(String fullUrl);

    public SandBox getOriginalSandBox();

    public void setOriginalSandBox(SandBox originalSandBox);

}
