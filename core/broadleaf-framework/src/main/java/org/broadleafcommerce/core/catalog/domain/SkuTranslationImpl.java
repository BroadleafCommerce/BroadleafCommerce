package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

/**
 * @author priyeshpatel
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SKU_TRANSLATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "SKUTranslationImpl_friendyName")
public class SkuTranslationImpl implements java.io.Serializable,
        SkuTranslation, LocaleIf {

    private static final long serialVersionUID = 1L;
    @Transient
    private static final Log LOG = LogFactory.getLog(SkuImpl.class);
    @Id
    @GeneratedValue(generator = "SkuTranslationID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SkuTranslationID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SkuTranslationID", allocationSize = 50)
    @Column(name = "TRANSLATION_ID")
    @AdminPresentation(friendlyName = "SkuTranslationImpl_ID", order = 1, group = "SkuTranslationImpl_description", groupOrder = 1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "DESCRIPTION", nullable = false)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Description", order = 3, group = "SkuTranslationImpl_description", prominent = true, groupOrder = 1)
    protected String description;
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Column(name = "LONG_DESCRIPTION", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Long_Description", order=6, group = "SkuTranslationImpl_description", largeEntry=true,fieldType=SupportedFieldType.HTML_BASIC)
    protected String longDescription;
    @ManyToOne(targetEntity = LocaleImpl.class, optional = false)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "SkuTranslationImpl_locale", order = 3, group = "SkuTranslationImpl_description", prominent = true, groupOrder = 1)
    protected Locale locale;
    @ManyToOne(targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID")
    @Index(name = "SKU_TRANSLATION_INDEX", columnNames = { "TRANSLATION_ID" })
    protected Sku sku;
    @Column(name = "NAME", nullable = false)
    @AdminPresentation(friendlyName = "SkuTranslationImpl_name", order = 3, group = "SkuTranslationImpl_description", prominent = true, groupOrder = 1)
    protected String name;
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    @Override
    public Sku getSku() {
        return sku;
    }

    @Override
    public void setSku(Sku Sku) {
        this.sku = Sku;
    }
    @Override
    public String getLongDescription() {
        return longDescription;
    }

    @Override
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
