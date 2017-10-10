/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.util.dao.TypedQueryBuilder;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.broadleafcommerce.profile.core.domain.CountrySubdivisionImpl;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Repository("blCountrySubdivisionDao")
public class CountrySubdivisionDaoImpl implements CountrySubdivisionDao {

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public CountrySubdivision findSubdivisionByAbbreviation(String abbreviation) {
        return (abbreviation == null)? null : em.find(CountrySubdivisionImpl.class, abbreviation);
    }

    @Override
    public CountrySubdivision findSubdivisionByCountryAndAltAbbreviation(@Nonnull String countryAbbreviation, @Nonnull String altAbbreviation) {
        TypedQuery<CountrySubdivision> query = new TypedQueryBuilder<CountrySubdivision>(CountrySubdivision.class, "cSub")
                .addRestriction("cSub.country.abbreviation", "=", countryAbbreviation)
                .addRestriction("cSub.alternateAbbreviation", "=", altAbbreviation)
                .toQuery(em);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public CountrySubdivision findSubdivisionByCountryAndName(@Nonnull String countryAbbreviation, @Nonnull String name) {
        TypedQuery<CountrySubdivision> query = new TypedQueryBuilder<CountrySubdivision>(CountrySubdivision.class, "cSub")
                .addRestriction("cSub.country.abbreviation", "=", countryAbbreviation)
                .addRestriction("cSub.name", "=", name)
                .toQuery(em);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountrySubdivision> findSubdivisions() {
        Query query = em.createNamedQuery("BC_FIND_COUNTRY_SUBDIVISIONS");
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CountrySubdivision> findSubdivisions(String countryAbbreviation) {
        Query query = em.createNamedQuery("BC_FIND_SUBDIVISIONS_BY_COUNTRY_ABBREVIATION");
        query.setParameter("countryAbbreviation", countryAbbreviation);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<CountrySubdivision> findSubdivisionsByCountryAndCategory(String countryAbbreviation, String category) {
        Query query = em.createNamedQuery("BC_FIND_SUBDIVISIONS_BY_COUNTRY_ABBREVIATION_AND_CATEGORY");
        query.setParameter("countryAbbreviation", countryAbbreviation);
        query.setParameter("category", category);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public CountrySubdivision create() {
        return (CountrySubdivision) entityConfiguration.createEntityInstance(CountrySubdivision.class.getName());
    }

    @Override
    public CountrySubdivision save(CountrySubdivision state) {
        return em.merge(state);
    }
}

