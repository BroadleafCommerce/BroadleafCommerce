package org.broadleafcommerce.gwt.client.datasource;

import org.broadleafcommerce.catalog.domain.Category;
import org.broadleafcommerce.catalog.domain.CategoryImpl;
import org.broadleafcommerce.gwt.client.service.catalog.CategoryService;
import org.broadleafcommerce.gwt.client.service.catalog.CategoryServiceAsync;
import org.broadleafcommerce.profile.service.CustomerService;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Data source for managing {@link CustomerDto customer}
 * data via {@link CustomerService}.
 */
public class CategoryDataSource extends GridServiceDataSource<Category, CategoryServiceAsync> {
    
    public static final String _ID = "id";
    public static final String _PARENT_ID = "parentId";
    public static final String _NAME = "name";
    public static final String _URL = "url";
    public static final String _URL_KEY = "urlKey";
    public static final String _ACTIVE_START_DATE = "activeStartDate";
    public static final String _ACTIVE_END_DATE = "activeEndDate";
    
    private static final CategoryServiceAsync service = GWT.create(CategoryService.class);
    
    // data source field definitions
    private static final DataSourceField id = new DataSourceTextField(_ID, "ID");
    private static final DataSourceField parentId = new DataSourceTextField(_PARENT_ID);
    private static final DataSourceField name = new DataSourceTextField(_NAME, "Name");
    private static final DataSourceField url = new DataSourceTextField(_URL, "URL");
    private static final DataSourceField urlKey = new DataSourceTextField(_URL_KEY, "URL Key");
    private static final DataSourceField activeStartDate = new DataSourceDateField(_ACTIVE_START_DATE, "Start Date");
    private static final DataSourceField activeEndDate = new DataSourceDateField(_ACTIVE_END_DATE, "End Date");
    
    static {
        id.setCanEdit(false);
        id.setPrimaryKey(true);
        parentId.setHidden(true);
    }
    
    public CategoryDataSource() {
    	super(service, id, parentId, name, activeStartDate, activeEndDate, url, urlKey);
    	name.setCanEdit(true);
    	name.setCanFilter(true);
    	url.setCanEdit(true);
    	urlKey.setCanEdit(true);
    	activeStartDate.setCanEdit(true);
    	activeEndDate.setCanEdit(true);
    }
    
    protected void copyValues(TreeNode from, Category to) {
        to.setId(Long.valueOf(from.getAttributeAsString(_ID)));
        Category parentCategory = to.getDefaultParentCategory();
        parentCategory.setId(Long.valueOf(from.getAttributeAsString(_PARENT_ID)));
        to.setDefaultParentCategory(parentCategory);
        to.setName(from.getAttributeAsString(_NAME));
        to.setUrl(from.getAttributeAsString(_URL));
        to.setUrlKey(from.getAttributeAsString(_URL_KEY));
        to.setActiveStartDate(from.getAttributeAsDate(_ACTIVE_START_DATE));
        to.setActiveEndDate(from.getAttributeAsDate(_ACTIVE_END_DATE));
    }
    
    protected void copyValues(Category from, TreeNode to) {
        to.setAttribute(_ID, String.valueOf(from.getId()));
        to.setAttribute(_PARENT_ID, from.getDefaultParentCategory()==null?null:String.valueOf(from.getDefaultParentCategory().getId()));
        to.setParentID(from.getDefaultParentCategory()==null?null:String.valueOf(from.getDefaultParentCategory().getId()));
        to.setAttribute(_NAME, from.getName());
        to.setAttribute(_URL, from.getUrl());
        to.setAttribute(_URL_KEY, from.getUrlKey());
        to.setAttribute(_ACTIVE_START_DATE, from.getActiveStartDate());
        to.setAttribute(_ACTIVE_END_DATE, from.getActiveEndDate());
    }
    
    protected Category newEntityInstance() {
    	//TODO go to the server to get a new instance of this entity
        return new CategoryImpl();
    }
    
}
