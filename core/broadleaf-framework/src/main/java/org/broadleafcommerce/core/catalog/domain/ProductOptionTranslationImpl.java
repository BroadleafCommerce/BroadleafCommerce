package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
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
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

/**
 * @author priyeshpatel
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PRODUCT_OPTION_TRANSLATION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "ProductOptionTranslationImpl_friendyName")
public class ProductOptionTranslationImpl implements java.io.Serializable,
        ProductOptionTranslation, LocaleIf {

    private static final long serialVersionUID = 1L;

    @Transient
    private static final Log LOG = LogFactory.getLog(ProductOptionTranslationImpl.class);

    @Id
    @GeneratedValue(generator = "ProductOptionTranslationID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "ProductOptionTranslationID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "ProductOptionTranslationID", allocationSize = 50)
    @Column(name = "TRANSLATION_ID")
    @AdminPresentation(friendlyName = "ProductOptionTranslationImpl_ID", order = 1, group = "ProductOptionTranslationImpl_Label", groupOrder = 1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "LABEL", nullable = false)
    @AdminPresentation(friendlyName = "ProductOptionImpl_Label", order = 3, group = "ProductOptionTranslationImpl_Label", prominent = true, groupOrder = 1)
    protected String label;

    @ManyToOne(targetEntity = LocaleImpl.class, optional = false)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "ProductOptionTranslationImpl_locale", order = 3, group = "ProductOptionTranslationImpl_Label", prominent = true, groupOrder = 1)
    protected Locale locale;

    @ManyToOne(targetEntity = ProductOptionImpl.class)
    @JoinColumn(name = "PRODUCT_OPTION_ID")
    @Index(name = "PRODUCT_OPTION_TRANSLATION_INDEX", columnNames = { "TRANSLATION_ID" })
    protected ProductOption productOption;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
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
    public ProductOption getProductOption() {
        return productOption;
    }

    @Override
    public void setProductOption(ProductOption productOption) {
        this.productOption = productOption;
    }
 
}
