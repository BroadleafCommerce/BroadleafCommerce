package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Jerry Ocanas (jocanas)
 */

@Embeddable
public class Seo {

    @Column(name = "META_DESCRIPTION")
    @AdminPresentation(friendlyName = "Seo_Meta_Description", order=3, group = "Seo_Group",groupOrder=2)
    protected String metaDescription;

    @Column(name = "META_KEYWORDS")
    @AdminPresentation(friendlyName = "Seo_Meta_Keywords", order=4, group = "Seo_Group",groupOrder=2)
    protected String metaKeywords;

    @Column(name = "META_ROBOT")
    @AdminPresentation(friendlyName = "Seo_Meta_Robot", order=5, group = "Seo_Group",groupOrder=2)
    protected String metaRobot;

    @Column(name = "TITLE_FRAGMENT")
    @AdminPresentation(friendlyName = "Seo_Title_Fragment", order=6, group = "Seo_Group",groupOrder=2)
    protected String titleFragment;

    @Nullable
    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    @Nullable
    public String getMetaKeywords() {
        return metaKeywords;
    }

    public void setMetaKeywords(String metaKeywords) {
        this.metaKeywords = metaKeywords;
    }

    @Nullable
    public String getMetaRobot() {
        return metaRobot;
    }

    public void setMetaRobot(String metaRobot) {
        this.metaRobot = metaRobot;
    }

    @Nullable
    public String getTitleFragment() {
        return titleFragment;
    }

    public void setTitleFragment(String titleFragment) {
        this.titleFragment = titleFragment;
    }

}
