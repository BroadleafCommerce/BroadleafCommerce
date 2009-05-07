package org.broadleafcommerce.email.dao;

import java.util.List;

import org.broadleafcommerce.email.domain.EmailStore;
import org.broadleafcommerce.email.domain.SurveyTargetUser;

public interface EmailWebTaskDao {

    public List<SurveyTargetUser> retrieveClickAndPickSurveyTargets();
    public EmailStore retrieveStoreById(long id);

}
