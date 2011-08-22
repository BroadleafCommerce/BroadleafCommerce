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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_MESSAGE")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class ContentMessageImpl implements ContentMessage {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MESSAGE_KEY")
    protected String messageKey;

    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "KEY_EDITABLE_FLAG")
    protected Character keyEditableFlag;

    @Column(name = "DELETED_FLAG")
    protected Character deleted;

    @OneToMany(mappedBy = "contentMessage", targetEntity = ContentMessageValueImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "languageCode")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    protected Map<String,ContentMessageValue> contentMessageValues = new HashMap<String,ContentMessageValue>();

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Character getKeyEditableFlag() {
        return keyEditableFlag;
    }

    @Override
    public void setKeyEditableFlag(Character keyEditableFlag) {
        this.keyEditableFlag = keyEditableFlag;
    }

    @Override
    public Character getDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Character deleted) {
        this.deleted = deleted;
    }

    @Override
    public void addContentValue(ContentMessageValue contentMessageValue) {
        if (! getContentMessageValues().containsKey(contentMessageValue.getLanguageCode())) {
            getContentMessageValues().put(contentMessageValue.getLanguageCode(), contentMessageValue);
            if (contentMessageValue.getContentMessage() != null) {
                contentMessageValue.getContentMessage().getContentMessageValues().remove(contentMessageValue.getLanguageCode());
            }
            contentMessageValue.setContentMessage(this);
        }
    }

    @Override
    public Map<String,ContentMessageValue> getContentMessageValues() {
        return contentMessageValues;
    }
}
