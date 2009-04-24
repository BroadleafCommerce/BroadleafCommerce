package org.broadleafcommerce.marketing.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@Table(name="BLC_TARGET_CONTENT")
public class TargetContentImpl implements TargetContent, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator="targetContentId", strategy=GenerationType.TABLE)
    @TableGenerator(name="targetContentId", table="SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "targetContentImpl", allocationSize = 1)
    @Column(name="TARGET_CONTENT_ID")
    private Long id;

    @Column(name="PRIORITY", nullable=false)
    private int priority;

    @Column(name="CONTENT_TYPE", nullable=false)
    private String contentType;

    @Column(name="CONTENT_NAME", nullable=false)
    private String contentName;

    @Column(name="URL")
    private String url;

    @Column(name="CONTENT")
    private String content;

    @Column(name="ONLINE_DATE")
    private Date onlineDate;

    @Column(name="OFFLINE_DATE")
    private Date offlineDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(Date onlineDate) {
        this.onlineDate = onlineDate;
    }

    public Date getOfflineDate() {
        return offlineDate;
    }

    public void setOfflineDate(Date offlineDate) {
        this.offlineDate = offlineDate;
    }


}
