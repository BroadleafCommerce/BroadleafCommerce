package org.broadleafcommerce.core.catalog.domain.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.profile.util.DateUtil;
import org.broadleafcommerce.profile.util.UrlUtil;

@MappedSuperclass
public abstract class CategoryMappedSuperclass implements Category {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(CategoryMappedSuperclass.class);
	
	/** The id. */
    @Id
    @GeneratedValue(generator = "CategoryId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CategoryId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CategoryImpl", allocationSize = 50)
    @Column(name = "CATEGORY_ID")
    @AdminPresentation(friendlyName="Category ID", group="Primary Key", hidden=true)
    protected Long id;

    /** The name. */
    @Column(name = "NAME", nullable=false)
    @AdminPresentation(friendlyName="Category Name", order=1, group="Description", prominent=true)
    protected String name;

    /** The url. */
    @Column(name = "URL")
    @AdminPresentation(friendlyName="Category Url", order=2, group="Description")
    protected String url;

    /** The url key. */
    @Column(name = "URL_KEY")
    @AdminPresentation(friendlyName="Category Url Key", order=3, group="Description")
    protected String urlKey;
    
    /** The description. */
    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName="Category Description", order=5, group="Description", largeEntry=true)
    protected String description;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName="Category Active Start Date", order=7, group="Active Date Range")
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    @AdminPresentation(friendlyName="Category Active End Date", order=8, group="Active Date Range")
    protected Date activeEndDate;

    /** The display template. */
    @Column(name = "DISPLAY_TEMPLATE")
    @AdminPresentation(friendlyName="Category Display Template", order=4, group="Description")
    protected String displayTemplate;
    
    /** The long description. */
    @Column(name = "LONG_DESCRIPTION")
    @AdminPresentation(friendlyName="Category Long Description", order=6, group="Description", largeEntry=true)
    protected String longDescription;
    
    @Transient
    protected Map<String, List<Category>> childCategoryURLMap;
    
    @Transient
    protected List<Category> childCategories = new ArrayList<Category>();
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getId()
     */
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#setId(java.lang.Long)
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setName(java.lang.String)
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getUrl()
     */
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setUrl(java.lang.String)
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getUrlKey()
     */
    public String getUrlKey() {
        if ((urlKey == null || "".equals(urlKey.trim())) && getName() != null) {
            return UrlUtil.generateUrlKey(getName());
        }
        return urlKey;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getGeneratedUrl()
     */
    public String getGeneratedUrl() {
        return buildLink(null, this, false);
    }

    /**
     * Builds the link.
     * @param link the link
     * @param category the category
     * @param ignoreTopLevel the ignore top level
     * @return the string
     */
    private String buildLink(final String link, final Category category, final boolean ignoreTopLevel) {
        if (category == null || (ignoreTopLevel && category.getDefaultParentCategory() == null)) {
            return link;
        }
        
        String lLink;
    	if (link == null) {
    		lLink = category.getUrlKey();
        } else {
        	lLink = category.getUrlKey() + "/" + link;
        }
        return buildLink(lLink, category.getDefaultParentCategory(), ignoreTopLevel);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setUrlKey(java.lang.String)
     */
    public void setUrlKey(final String urlKey) {
        this.urlKey = urlKey;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setDescription(java.lang
     * .String)
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getActiveStartDate()
     */
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setActiveStartDate(java
     * .util.Date)
     */
    public void setActiveStartDate(final Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getActiveEndDate()
     */
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setActiveEndDate(java.util
     * .Date)
     */
    public void setActiveEndDate(final Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#isActive()
     */
    public boolean isActive() {
        if (LOG.isDebugEnabled()) {
            if (!DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false)) {
                LOG.debug("category, " + id + ", inactive due to date");
            }
        }
        return DateUtil.isActive(getActiveStartDate(), getActiveEndDate(), false);
    }

    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getDisplayTemplate()
     */
    public String getDisplayTemplate() {
        return displayTemplate;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setDisplayTemplate(java
     * .lang.String)
     */
    public void setDisplayTemplate(final String displayTemplate) {
        this.displayTemplate = displayTemplate;
    }
    
    /*
     * (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.Category#getLongDescription()
     */
    public String getLongDescription() {
        return longDescription;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.broadleafcommerce.core.catalog.domain.Category#setLongDescription(java.lang.String)
     */
    public void setLongDescription(final String longDescription) {
        this.longDescription = longDescription;
    }
}
