package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.cms.page.domain.PageTemplate;

/**
 * Created by jfischer
 */
public interface PageTemplateFieldGroupXref {
    PageTemplateFieldGroupXrefImpl.PageTemplateFieldGroupXrefPk getXref();

    void setXref(PageTemplateFieldGroupXrefImpl.PageTemplateFieldGroupXrefPk xref);

    Long getDisplayOrder();

    void setDisplayOrder(Long displayOrder);

    PageTemplate getPageTemplate();

    void setPageTemplate(PageTemplate category);

    FieldGroup getFieldGroup();

    void setFieldGroup(FieldGroup fieldGroup);
}
