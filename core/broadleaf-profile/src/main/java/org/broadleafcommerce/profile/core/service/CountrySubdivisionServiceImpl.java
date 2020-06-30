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
package org.broadleafcommerce.profile.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.core.dao.CountrySubdivisionDao;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blCountrySubdivisionService")
public class CountrySubdivisionServiceImpl implements CountrySubdivisionService {

    private static final Log LOG = LogFactory.getLog(CountrySubdivisionServiceImpl.class);

    protected Map<String, Long> missCache = new ConcurrentHashMap<>();

    @Resource(name="blCountrySubdivisionDao")
    protected CountrySubdivisionDao countrySubdivisionDao;

    @Override
    public List<CountrySubdivision> findSubdivisions() {
        return countrySubdivisionDao.findSubdivisions();
    }

    @Override
    public List<CountrySubdivision> findSubdivisions(String countryAbbreviation) {
        return countrySubdivisionDao.findSubdivisions(countryAbbreviation);
    }

    @Override
    public List<CountrySubdivision> findSubdivisionsByCountryAndCategory(String countryAbbreviation, String category) {
        return countrySubdivisionDao.findSubdivisionsByCountryAndCategory(countryAbbreviation, category);
    }

    @Override
    public CountrySubdivision findSubdivisionByAbbreviation(String abbreviation) {
        return countrySubdivisionDao.findSubdivisionByAbbreviation(abbreviation);
    }

    @Override
    public CountrySubdivision findSubdivisionByCountryAndAltAbbreviation(String countryAbbreviation, String altAbbreviation) {
        String cacheKey = getMissCacheKey(countryAbbreviation, altAbbreviation);
        Long numMisses = missCache.get(cacheKey);
        if (numMisses != null) {
            missCache.put(cacheKey, ++numMisses);
            if (numMisses % 10 == 0) {
                LOG.error(String.format("No CountrySubdivision record for abbreviation = %s and alt abbreviation = %s. Missed " + numMisses + " times", countryAbbreviation, altAbbreviation));
            }
            return null;
        }
        CountrySubdivision retVal = countrySubdivisionDao.findSubdivisionByCountryAndAltAbbreviation(countryAbbreviation, altAbbreviation);
        if (retVal == null) {
            missCache.put(cacheKey, 1L);
        }
        return retVal;
    }

    @Override
    public CountrySubdivision findSubdivisionByCountryAndName(String countryAbbreviation, String name) {
        return countrySubdivisionDao.findSubdivisionByCountryAndName(countryAbbreviation, name);
    }

    @Override
    @Transactional("blTransactionManager")
    public CountrySubdivision save(CountrySubdivision subdivision) {
        return countrySubdivisionDao.save(subdivision);
    }

    protected String getMissCacheKey(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key);
        }
        return sb.toString();
    }
}
