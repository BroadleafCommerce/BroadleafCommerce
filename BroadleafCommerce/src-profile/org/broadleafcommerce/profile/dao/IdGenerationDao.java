package org.broadleafcommerce.profile.dao;

import org.broadleafcommerce.profile.domain.IdGeneration;

public interface IdGenerationDao {

    public IdGeneration findNextId(String idType);

    public IdGeneration updateNextId(IdGeneration idGeneration);
}
