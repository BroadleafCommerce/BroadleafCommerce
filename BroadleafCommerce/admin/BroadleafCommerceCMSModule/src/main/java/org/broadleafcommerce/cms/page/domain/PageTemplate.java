package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.locale.domain.Locale;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface PageTemplate extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getTemplateName();

    public void setTemplateName(String templateName);

    public String getTemplateDescription();

    public void setTemplateDescription(String templateDescription);

    public String getTemplatePath();

    public void setTemplatePath(String templatePath);

    public Locale getLocale();

    public void setLocale(Locale locale);

    public List<FieldGroup> getFieldGroups();

    public void setFieldGroups(List<FieldGroup> fieldGroups);
}
