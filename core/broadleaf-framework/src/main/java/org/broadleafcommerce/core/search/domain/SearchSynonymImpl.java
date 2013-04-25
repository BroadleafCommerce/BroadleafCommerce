/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.search.domain;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "BLC_SEARCH_SYNONYM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SearchSynonymImpl implements SearchSynonym {
    
    @Id
    @GeneratedValue(generator = "SearchSynonymId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SearchSynonymId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SearchSynonymImpl", allocationSize = 50)
    @Column(name = "SEARCH_SYNONYM_ID")
    private Long id;
    
    @Column(name = "TERM")
    @Index(name="SEARCHSYNONYM_TERM_INDEX", columnNames={"TERM"})
    private String term;
    
    @Column(name = "SYNONYMS")
    private String synonyms;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String[] getSynonyms() {
        return synonyms.split("|");
    }
    public void setSynonyms(String[] synonyms) {
        this.synonyms = StringUtils.join(synonyms, '|');
    }
    
}