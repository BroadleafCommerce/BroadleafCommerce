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
@Table(name = "BLC_STATE")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class StateImpl implements State, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ABBREVIATION")
    private String abbreviation;

    @Column(name = "NAME")
    private String name;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof StateImpl)) return false;

        StateImpl item = (StateImpl) other;

        if (name != null && item.name != null ? !name.equals(item.name) : name != item.name) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result *= 31;

        return result;
    }
}
