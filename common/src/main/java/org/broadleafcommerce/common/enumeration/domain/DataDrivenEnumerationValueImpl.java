package org.broadleafcommerce.common.enumeration.domain;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

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

/**
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_DATA_DRVN_ENUM_VAL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "DataDrivenEnumerationImpl_friendyName")
public class DataDrivenEnumerationValueImpl implements DataDrivenEnumerationValue {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DataDrivenEnumerationValueId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "DataDrivenEnumerationValueId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "DataDrivenEnumerationValueId", allocationSize = 50)
    @Column(name = "ENUM_VAL_ID")
    protected Long id;

    @ManyToOne(targetEntity = DataDrivenEnumerationImpl.class)
    @JoinColumn(name = "ENUM_TYPE")
    protected DataDrivenEnumeration type;

    @Column(name = "ENUM_KEY")
    @Index(name = "KEY_INDEX", columnNames = {"ENUM_KEY"})
    protected String key;

    @Column(name = "DISPLAY")
    protected String display;

    @Column(name = "HIDDEN")
    @Index(name = "HIDDEN_INDEX", columnNames = {"HIDDEN"})
    protected Boolean hidden;

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public DataDrivenEnumeration getType() {
        return type;
    }

    @Override
    public void setType(DataDrivenEnumeration type) {
        this.type = type;
    }
}
