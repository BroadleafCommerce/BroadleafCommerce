/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.email.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_EMAIL_TRACKING_OPENS")
public class EmailTrackingOpensImpl implements EmailTrackingOpens {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OpenId")
    @GenericGenerator(
        name="OpenId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="EmailTrackingOpensImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.email.domain.EmailTrackingOpensImpl")
        }
    )
    @Column(name = "OPEN_ID")
    protected Long id;

    @Column(name = "DATE_OPENED")
    protected Date dateOpened;

    @Column(name = "USER_AGENT")
    protected String userAgent;

    @ManyToOne(targetEntity = EmailTrackingImpl.class)
    @JoinColumn(name = "EMAIL_TRACKING_ID")
    @Index(name="TRACKINGOPEN_TRACKING", columnNames={"EMAIL_TRACKING_ID"})
    protected EmailTracking emailTracking;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#getDateOpened()
     */
    @Override
    public Date getDateOpened() {
        return dateOpened;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#setDateOpened(java.util.Date)
     */
    @Override
    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#getUserAgent()
     */
    @Override
    public String getUserAgent() {
        return userAgent;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#setUserAgent(java.lang.String)
     */
    @Override
    public void setUserAgent(String userAgent) {
        if (userAgent.length() > 255) {
            userAgent = userAgent.substring(0,254);
        }
        this.userAgent = userAgent;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#getEmailTracking()
     */
    @Override
    public EmailTracking getEmailTracking() {
        return emailTracking;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.common.email.domain.EmailTrackingOpens#setEmailTracking(org.broadleafcommerce.common.email.domain.EmailTrackingImpl)
     */
    @Override
    public void setEmailTracking(EmailTracking emailTracking) {
        this.emailTracking = emailTracking;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateOpened == null) ? 0 : dateOpened.hashCode());
        result = prime * result + ((emailTracking == null) ? 0 : emailTracking.hashCode());
        result = prime * result + ((userAgent == null) ? 0 : userAgent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        EmailTrackingOpensImpl other = (EmailTrackingOpensImpl) obj;

        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (dateOpened == null) {
            if (other.dateOpened != null)
                return false;
        } else if (!dateOpened.equals(other.dateOpened))
            return false;
        if (emailTracking == null) {
            if (other.emailTracking != null)
                return false;
        } else if (!emailTracking.equals(other.emailTracking))
            return false;
        if (userAgent == null) {
            if (other.userAgent != null)
                return false;
        } else if (!userAgent.equals(other.userAgent))
            return false;
        return true;
    }

}
