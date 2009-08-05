/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.email.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * @author jfischer
 *
 */
@Entity
@Table(name = "BLC_EMAIL_TRACKING")
public class EmailTrackingImpl implements EmailTracking {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "EmailTrackingId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "EmailTrackingId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "EmailTrackingImpl", allocationSize = 50)
    @Column(name = "EMAIL_TRACKING_ID")
    protected Long id;

    @Column(name = "EMAIL_ADDRESS")
    protected String emailAddress;

    @Column(name = "DATE_SENT")
    protected Date dateSent;

    @Column(name = "TYPE")
    protected String type;

    @OneToMany(mappedBy = "emailTracking", targetEntity = EmailTrackingClicksImpl.class)
    protected Set<EmailTrackingClicks> emailTrackingClicks = new HashSet<EmailTrackingClicks>();

    @OneToMany(mappedBy = "emailTracking", targetEntity = EmailTrackingOpensImpl.class)
    protected Set<EmailTrackingOpens> emailTrackingOpens = new HashSet<EmailTrackingOpens>();

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#getEmailAddress()
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#setEmailAddress(java.lang.String)
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#getDateSent()
     */
    public Date getDateSent() {
        return dateSent;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#setDateSent(java.util.Date)
     */
    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.email.domain.EmailTracking#setType(java.lang.String)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the emailTrackingClicks
     */
    public Set<EmailTrackingClicks> getEmailTrackingClicks() {
        return emailTrackingClicks;
    }

    /**
     * @param emailTrackingClicks the emailTrackingClicks to set
     */
    public void setEmailTrackingClicks(Set<EmailTrackingClicks> emailTrackingClicks) {
        this.emailTrackingClicks = emailTrackingClicks;
    }

    /**
     * @return the emailTrackingOpens
     */
    public Set<EmailTrackingOpens> getEmailTrackingOpens() {
        return emailTrackingOpens;
    }

    /**
     * @param emailTrackingOpens the emailTrackingOpens to set
     */
    public void setEmailTrackingOpens(Set<EmailTrackingOpens> emailTrackingOpens) {
        this.emailTrackingOpens = emailTrackingOpens;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateSent == null) ? 0 : dateSent.hashCode());
        result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
        result = prime * result + ((emailTrackingClicks == null) ? 0 : emailTrackingClicks.hashCode());
        result = prime * result + ((emailTrackingOpens == null) ? 0 : emailTrackingOpens.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailTrackingImpl other = (EmailTrackingImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (dateSent == null) {
            if (other.dateSent != null)
                return false;
        } else if (!dateSent.equals(other.dateSent))
            return false;
        if (emailAddress == null) {
            if (other.emailAddress != null)
                return false;
        } else if (!emailAddress.equals(other.emailAddress))
            return false;
        if (emailTrackingClicks == null) {
            if (other.emailTrackingClicks != null)
                return false;
        } else if (!emailTrackingClicks.equals(other.emailTrackingClicks))
            return false;
        if (emailTrackingOpens == null) {
            if (other.emailTrackingOpens != null)
                return false;
        } else if (!emailTrackingOpens.equals(other.emailTrackingOpens))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
