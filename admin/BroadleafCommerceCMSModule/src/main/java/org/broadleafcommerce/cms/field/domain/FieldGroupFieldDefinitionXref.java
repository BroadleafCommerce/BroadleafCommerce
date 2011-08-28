package org.broadleafcommerce.cms.field.domain;

/**
 * Created by jfischer
 */
public interface FieldGroupFieldDefinitionXref {
    FieldGroupFieldDefinitionXrefImpl.FieldGroupFieldDefinitionXrefPk getXref();

    void setXref(FieldGroupFieldDefinitionXrefImpl.FieldGroupFieldDefinitionXrefPk xref);

    Long getDisplayOrder();

    void setDisplayOrder(Long displayOrder);

    FieldGroup getFieldGroup();

    void setFieldGroup(FieldGroup fieldGroup);

    FieldDefinition getFieldDefinition();

    void setFieldDefinition(FieldDefinition fieldDefinition);
}
