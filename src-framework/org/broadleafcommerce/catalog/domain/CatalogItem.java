package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.broadleafcommerce.common.domain.Auditable;

@Entity
@Table(name = "CATALOG_ITEM")
public class CatalogItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "CATALOG_ITEM_ID")
    private Long id;
    
    @Embedded
    private Auditable auditable;

    @OneToMany(mappedBy = "catalogItem")
    @MapKey(name="name")
    private Map<String, ItemAttribute> itemAttributes;

    @OneToMany(mappedBy = "catalogItem")
    private Set<CatalogItemListAssociation> catalogItemListAssociations;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NAME")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, ItemAttribute> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, ItemAttribute> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public Set<CatalogItemListAssociation> getCatalogItemListAssociations() {
        return catalogItemListAssociations;
    }

    public void setCatalogItemListAssociations(Set<CatalogItemListAssociation> catalogItemListAssociations) {
        this.catalogItemListAssociations = catalogItemListAssociations;
    }

    public Auditable getAuditable() {
        return auditable;
    }

    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }
}
