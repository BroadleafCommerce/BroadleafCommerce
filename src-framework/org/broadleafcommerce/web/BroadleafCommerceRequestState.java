package org.broadleafcommerce.web;

import javax.servlet.http.HttpServletRequest;

import org.broadleafcommerce.catalog.domain.Category;

/**
* This class is used to persist objects to the request.
 */
public class BroadleafCommerceRequestState {

	private Category category;
	private String catalogPrefix;

	public static BroadleafCommerceRequestState getRequestState(HttpServletRequest request) {
    	BroadleafCommerceRequestState state = (BroadleafCommerceRequestState) request.getAttribute(BroadleafCommerceRequestState.class.getName());

        if (state == null) {
            state = new BroadleafCommerceRequestState();
            request.setAttribute(BroadleafCommerceRequestState.class.getName(), state);
        }

        return state;
    }

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

    public String getCatalogPrefix() {
		return catalogPrefix;
	}

	public void setCatalogPrefix(String catalogPrefix) {
		this.catalogPrefix = catalogPrefix;
	}
}
