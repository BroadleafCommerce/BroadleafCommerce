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

import org.broadleafcommerce.profile.core.dao.StateDao;
import org.broadleafcommerce.profile.core.domain.State;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @deprecated - use {@link org.broadleafcommerce.profile.core.service.CountrySubdivisionServiceImpl} instead.
 */
@Deprecated
@Service("blStateService")
public class StateServiceImpl implements StateService {

    @Resource(name="blStateDao")
    protected StateDao stateDao;

    public List<State> findStates() {
        return stateDao.findStates();
    }

    public List<State> findStates(String countryAbbreviation) {
        return stateDao.findStates(countryAbbreviation);
    }

    public State findStateByAbbreviation(String abbreviation) {
        return stateDao.findStateByAbbreviation(abbreviation);
    }

    @Transactional("blTransactionManager")
    public State save(State state) {
        return stateDao.save(state);
    }
}

