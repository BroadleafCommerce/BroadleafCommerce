package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE")
@Table(name = "CATALOG_ITEM_LIST")
public class CatalogItemList implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "CATALOG_ITEM_LIST_ID")
    private Long id;

    @OneToMany(mappedBy = "catalogItemList")
    private Set<CatalogItemListAssociation> catalogItemListAssociations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CatalogItemListAssociation> getCatalogItemListAssociations() {
        return catalogItemListAssociations;
    }

    public void setCatalogItemListAssociations(Set<CatalogItemListAssociation> catalogItemListAssociations) {
        this.catalogItemListAssociations = catalogItemListAssociations;
    }

}
