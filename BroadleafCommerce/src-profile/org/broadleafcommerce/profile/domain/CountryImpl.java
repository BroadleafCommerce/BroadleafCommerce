package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_COUNTRY")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CountryImpl implements Country, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ABBREVIATION")
    private String abbreviation;

    @Column(name = "NAME")
    private String name;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String Abbreviation) {
        this.abbreviation = Abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
