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
package org.broadleafcommerce.search.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "BLC_SEARCH_INTERCEPT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SearchInterceptImpl implements SearchIntercept {
    
    @Id
    @GeneratedValue(generator = "SearchInterceptId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SearchInterceptId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SearchInterceptImpl", allocationSize = 50)
    @Column(name = "SEARCH_INTERCEPT_ID")
    protected Long id;
    
    @Column(name = "TERM")
    @Index(name="SEARCHINTERCEPT_TERM_INDEX", columnNames={"TERM"})
    private String term;
    
    @Column(name = "REDIRECT")
    private String redirect;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.search.domain.SearchIntercept#getTerm()
     */
    public String getTerm() {
        return term;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.search.domain.SearchIntercept#setTerm(java.lang.String)
     */
    public void setTerm(String term) {
        this.term = term;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.search.domain.SearchIntercept#getRedirect()
     */
    public String getRedirect() {
        return redirect;
    }
    /* (non-Javadoc)
     * @see org.broadleafcommerce.search.domain.SearchIntercept#setRedirect(java.lang.String)
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
