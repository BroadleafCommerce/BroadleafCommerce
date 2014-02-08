/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 * #L%
 */

package com.broadleafcommerce.customfield.service;

import com.broadleafcommerce.customfield.dao.CustomFieldDao;
import com.broadleafcommerce.customfield.domain.CustomField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jeff Fischer
 */
@Service("blCustomFieldService")
public class CustomFieldServiceImpl implements CustomFieldService {

    @Resource(name="blCustomFieldDao")
    protected CustomFieldDao customFieldDao;

    @Override
    public CustomField findById(Long id) {
        return customFieldDao.retrieveById(id);
    }

    @Override
    public List<CustomField> findByTargetEntityName(String targetEntityName) {
        return customFieldDao.retrieveByTargetEntityName(targetEntityName);
    }
}
