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

package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.StateDao;
import org.broadleafcommerce.profile.core.domain.State;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

