/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.demo;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Jeff Fischer
 */
@Component("blCompositeAutoImportSql")
public class CompositeAutoImportSql {

    @Autowired(required = false)
    protected List<AutoImportSql> importSqlList = new ArrayList<AutoImportSql>();

    public String compileSqlFilePathList(String persistenceUnit) {
        StringBuilder sb = new StringBuilder();
        for (AutoImportSql sql : importSqlList) {
            if (persistenceUnit.equals(sql.getPersistenceUnit()) || AutoImportPersistenceUnit.ALL.equals(sql.getPersistenceUnit())) {
                sb.append(sql.getSqlFilePath());
                sb.append(",");
            }
        }
        String response = sb.toString();
        if (response.endsWith(",")) {
            response = response.substring(0,response.length()-1);
        }
        return response;
    }
}
