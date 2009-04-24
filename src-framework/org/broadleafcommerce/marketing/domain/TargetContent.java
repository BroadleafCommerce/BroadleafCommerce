package org.broadleafcommerce.marketing.domain;

import java.util.Date;

public interface TargetContent {


    public Long getId();

    public void setId(Long id);

    public int getPriority();

    public void setPriority(int priority);

    public String getContentType();

    public void setContentType(String contentType);

    public String getContentName();

    public void setContentName(String contentName);

    public String getUrl();

    public void setUrl(String url);

    public String getContent();

    public void setContent(String content);

    public Date getOnlineDate();

    public void setOnlineDate(Date onlineDate);

    public Date getOfflineDate();

    public void setOfflineDate(Date offlineDate);
}
