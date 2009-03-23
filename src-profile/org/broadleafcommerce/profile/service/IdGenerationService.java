package org.broadleafcommerce.profile.service;

public interface IdGenerationService {

    public Long findNextId(String idType);
}