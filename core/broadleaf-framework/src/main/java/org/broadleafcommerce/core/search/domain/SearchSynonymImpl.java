/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.domain;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "BLC_SEARCH_SYNONYM", indexes = {
        @Index(name = "SEARCHSYNONYM_TERM_INDEX", columnList = "TERM")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blSearchElements")
public class SearchSynonymImpl implements SearchSynonym {

    @Id
    @GeneratedValue(generator = "SearchSynonymId")
    @GenericGenerator(
            name = "SearchSynonymId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "SearchSynonymImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.core.search.domain.SearchSynonymImpl")
            }
    )
    @Column(name = "SEARCH_SYNONYM_ID")
    private Long id;

    @Column(name = "TERM")
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
        return synonyms.split("\\|");
    }

    @Override
    public void setSynonyms(String[] synonyms) {
        this.synonyms = StringUtils.join(synonyms, '|');
    }

}
