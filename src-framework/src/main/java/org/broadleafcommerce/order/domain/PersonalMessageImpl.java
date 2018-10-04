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
package org.broadleafcommerce.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PERSONAL_MESSAGE")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class PersonalMessageImpl implements PersonalMessage {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PersonalMessageId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PersonalMessageId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PersonalMessageImpl", allocationSize = 50)
    @Column(name = "PERSONAL_MESSAGE_ID")
    protected Long id;

    @Column(name = "MESSAGE_TO")
    protected String messageTo;

    @Column(name = "MESSAGE_FROM")
    protected String messageFrom;

    @Column(name = "MESSAGE")
    protected String message;

    @Column(name = "OCCASION")
    protected String occasion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((messageFrom == null) ? 0 : messageFrom.hashCode());
        result = prime * result + ((messageTo == null) ? 0 : messageTo.hashCode());
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
        PersonalMessageImpl other = (PersonalMessageImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (messageFrom == null) {
            if (other.messageFrom != null)
                return false;
        } else if (!messageFrom.equals(other.messageFrom))
            return false;
        if (messageTo == null) {
            if (other.messageTo != null)
                return false;
        } else if (!messageTo.equals(other.messageTo))
            return false;
        return true;
    }

}
