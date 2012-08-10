package org.broadleafcommerce.core.searchRedirect.domain;

import java.io.Serializable;
import java.util.Date;

public interface SearchRedirect extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getSearchTerm();

    public void setSearchTerm(String searchTerm);

    public String getUrl();

    public void setUrl(String url);

    public Integer getSearchPriority() ;
    
    public void setSearchPriority(Integer searchPriority);
    public Date getActiveStartDate() ;

    public void setActiveStartDate(Date activeStartDate);

    public Date getActiveEndDate() ;

    public void setActiveEndDate(Date activeEndDate);

    boolean isActive();
}