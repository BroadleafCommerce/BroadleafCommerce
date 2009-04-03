package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.State;

public interface StateService {

    public List<State> findStates();

    public State findStateByAbbreviation(String abbreviation);
}