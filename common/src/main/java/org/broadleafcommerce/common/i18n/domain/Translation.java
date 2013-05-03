
package org.broadleafcommerce.common.i18n.domain;

/**
 * This domain object represents a translated value for a given property on an entity for a specific locale.
 * 
 * @author Andre Azzolini (apazzolini)
 * @see TranslatedEntity
 */
public interface Translation {
    
    public Long getId();

    public void setId(Long id);

    public TranslatedEntity getEntityType();

    public void setEntityType(TranslatedEntity entityType);

    public String getEntityId();

    public void setEntityId(String entityId);

    public String getFieldName();

    public void setFieldName(String fieldName);

    public String getLocaleCode();

    public void setLocaleCode(String localeCode);

    public String getTranslatedValue();

    public void setTranslatedValue(String translatedValue);

}