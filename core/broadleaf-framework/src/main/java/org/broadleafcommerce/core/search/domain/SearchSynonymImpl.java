/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.search.domain;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_SEARCH_SYNONYM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SearchSynonymImpl implements SearchSynonym {
    
    @Id
    @GeneratedValue(generator = "SearchSynonymId")
    @GenericGenerator(
        name="SearchSynonymId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SearchSynonymImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.domain.SearchSynonymImpl")
        }
    )
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
    @Override
    public String getTerm() {
        return term;
    }
    @Override
    public void setTerm(String term) {
        this.term = term;
    }
    @Override
    public String[] getSynonyms() {
        return synonyms.split("|");
    }
    @Override
    public void setSynonyms(String[] synonyms) {
        this.synonyms = StringUtils.join(synonyms, '|');
    }
    
}
