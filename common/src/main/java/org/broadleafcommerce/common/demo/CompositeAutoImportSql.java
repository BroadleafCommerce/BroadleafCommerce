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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, List<AutoImportSql>> constructAutoImportSqlMapForPU(String persistenceUnit) {
        Map<String, List<AutoImportSql>> sqlMap = new LinkedHashMap<>();
        sqlMap.put("AutoImportStage.PRIMARY_EARLY", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_FRAMEWORK_SECURITY", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_PRE_MODULE_SECURITY", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_MODULE_SECURITY", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_POST_MODULE_SECURITY", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_PRE_BASIC_DATA", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_BASIC_DATA", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_POST_BASIC_DATA", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.ALL_TABLE_SEQUENCE", new ArrayList<AutoImportSql>());
        sqlMap.put("AutoImportStage.PRIMARY_LATE", new ArrayList<AutoImportSql>());

        for (AutoImportSql sql : importSqlList) {
            if (persistenceUnit.equals(sql.getPersistenceUnit()) || AutoImportPersistenceUnit.ALL.equals(sql.getPersistenceUnit())) {
                int order = sql.getOrder();
                if (order < AutoImportStage.PRIMARY_FRAMEWORK_SECURITY) {
                    sqlMap.get("AutoImportStage.PRIMARY_EARLY").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_FRAMEWORK_SECURITY
                        && order < AutoImportStage.PRIMARY_PRE_MODULE_SECURITY) {
                    sqlMap.get("AutoImportStage.PRIMARY_FRAMEWORK_SECURITY").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_PRE_MODULE_SECURITY
                        && order < AutoImportStage.PRIMARY_MODULE_SECURITY) {
                    sqlMap.get("AutoImportStage.PRIMARY_PRE_MODULE_SECURITY").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_MODULE_SECURITY
                        && order < AutoImportStage.PRIMARY_POST_MODULE_SECURITY) {
                    sqlMap.get("AutoImportStage.PRIMARY_MODULE_SECURITY").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_POST_MODULE_SECURITY
                        && order < AutoImportStage.PRIMARY_PRE_BASIC_DATA) {
                    sqlMap.get("AutoImportStage.PRIMARY_POST_MODULE_SECURITY").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_PRE_BASIC_DATA
                        && order < AutoImportStage.PRIMARY_BASIC_DATA) {
                    sqlMap.get("AutoImportStage.PRIMARY_PRE_BASIC_DATA").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_BASIC_DATA
                        && order < AutoImportStage.PRIMARY_POST_BASIC_DATA) {
                    sqlMap.get("AutoImportStage.PRIMARY_BASIC_DATA").add(sql);
                } else if (order >= AutoImportStage.PRIMARY_POST_BASIC_DATA
                        && order < AutoImportStage.ALL_TABLE_SEQUENCE) {
                    sqlMap.get("AutoImportStage.PRIMARY_POST_BASIC_DATA").add(sql);
                } else if (order >= AutoImportStage.ALL_TABLE_SEQUENCE
                        && order < AutoImportStage.PRIMARY_LATE) {
                    sqlMap.get("AutoImportStage.ALL_TABLE_SEQUENCE").add(sql);
                } else  {
                    sqlMap.get("AutoImportStage.PRIMARY_LATE").add(sql);
                }
            }
        }

        return sqlMap;
    }
}
