package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.validator.GenericValidator;
import org.broadleafcommerce.util.DateUtil;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.OrderBy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CATEGORY")
public class CategoryImpl implements Category, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "CATEGORY_ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "URL")
    private String url;

    @Column(name = "URL_KEY")
    private String urlKey;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "DEFAULT_PARENT_CATEGORY_ID")
    private Category defaultParentCategory;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "ACTIVE_START_DATE")
    private Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    private Date activeEndDate;

    @Column(name = "DISPLAY_TEMPLATE")
    private String displayTemplate;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = CategoryImpl.class)
    @JoinTable(name = "BLC_CATEGORY_XREF", joinColumns = @JoinColumn(name = "CATEGORY_ID"), inverseJoinColumns = @JoinColumn(name = "SUB_CATEGORY_ID", referencedColumnName = "CATEGORY_ID"))
    @OrderBy(clause = "DISPLAY_ORDER")
    private List<Category> allChildCategories;

    // @OneToMany(mappedBy = "category", targetEntity = BroadleafCategoryImage.class)
    @CollectionOfElements
    @JoinTable(name = "BLC_CATEGORY_IMAGE", joinColumns = @JoinColumn(name = "CATEGORY_ID"))
    @MapKey(columns = { @Column(name = "NAME", length = 5) })
    @Column(name = "URL")
    private Map<String, String> categoryImages;

    @Column(name = "LONG_DESCRIPTION")
    private String longDescription;

    @Transient
    private List<Category> childCategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getDefaultParentCategory() {
        return defaultParentCategory;
    }

    public void setDefaultParentCategory(Category defaultParentCategory) {
        this.defaultParentCategory = defaultParentCategory;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlKey() {
        if (GenericValidator.isBlankOrNull(urlKey)) {
            return String.valueOf(getId());
        }
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActiveStartDate() {
        return activeStartDate;
    }

    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    public Date getActiveEndDate() {
        return activeEndDate;
    }

    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    public boolean isActive() {
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }

    public String getDisplayTemplate() {
        return displayTemplate;
    }

    public void setDisplayTemplate(String displayTemplate) {
        this.displayTemplate = displayTemplate;
    }

    private List<Category> getAllChildCategories() {
        return allChildCategories;
    }

    public List<Category> getChildCategories() {
        if (childCategories == null) {
            childCategories = new ArrayList<Category>();
            List<Category> categories = getAllChildCategories();
            for (Category category : categories) {
                if (category.isActive()) {
                    childCategories.add(category);
                }
            }
        }
        return childCategories;

    }

    public boolean hasChildCategories() {
        return getChildCategories().size() > 0;
    }

    public void setAllChildCategories(List<Category> allChildCategories) {
        this.allChildCategories = allChildCategories;
    }

    public Map<String, String> getCategoryImages() {
        return categoryImages;
    }

    public String getCategoryImage(String imageKey) {
        return categoryImages.get(imageKey);
    }

    public void setCategoryImages(Map<String, String> categoryImages) {
        this.categoryImages = categoryImages;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
}
