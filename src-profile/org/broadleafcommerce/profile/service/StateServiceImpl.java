package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.StateDao;
import org.broadleafcommerce.profile.domain.State;
import org.springframework.stereotype.Service;

@Service("stateService")
public class StateServiceImpl implements StateService {

    @Resource
    private StateDao stateDao;

    public List<State> findStates() {
        return stateDao.findStates();
    }

    public State findStateByAbbreviation(String abbreviation) {
        return stateDao.findStateByAbbreviation(abbreviation);
    }
}