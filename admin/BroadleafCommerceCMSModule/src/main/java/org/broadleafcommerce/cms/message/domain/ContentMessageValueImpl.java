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
package org.broadleafcommerce.cms.message.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_MESSAGE_VALUE")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class ContentMessageValueImpl implements ContentMessageValue {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MESSAGE_VALUE_ID")
    protected Long id;

    @ManyToOne(targetEntity = ContentMessageImpl.class)
    @JoinColumn(name = "MESSAGE_KEY")
    protected ContentMessage contentMessage;

    @Column(name = "LANGUAGE_CODE")
    protected String languageCode;

    @Column(name = "MESSAGE_VALUE")
    protected String messageValue;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ContentMessage getContentMessage() {
        return contentMessage;
    }

    @Override
    public void setContentMessage(ContentMessage contentMessage) {
        this.contentMessage = contentMessage;
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public String getMessageValue() {
        return messageValue;
    }

    @Override
    public void setMessageValue(String messageValue) {
        this.messageValue = messageValue;
    }
}
