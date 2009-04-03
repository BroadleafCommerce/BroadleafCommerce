package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.State;

public interface StateDao {

    public List<State> findStates();

    public State findStateByAbbreviation(String abbreviation);
}
