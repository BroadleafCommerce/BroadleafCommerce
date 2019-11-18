/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.demo;

import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.AutoImportStage;
import org.broadleafcommerce.common.demo.DemoCondition;
import org.broadleafcommerce.common.demo.ImportCondition;
import org.broadleafcommerce.common.demo.MTCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jeff Fischer
 */
@Configuration("blCMSData")
@Conditional(ImportCondition.class)
public class ImportSQLConfig {

    public static final int BASIC_DATA_SPECIAL = AutoImportStage.PRIMARY_PRE_BASIC_DATA + 200;

    @Bean
    @Conditional(DemoCondition.class)
    public AutoImportSql blCMSBasicData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/load_content_structure.sql," +
                "config/bc/sql/demo/load_content_data.sql,config/bc/sql/demo/load_content_structure_i18n.sql," +
                "config/bc/sql/demo/load_content_data_i18n.sql", BASIC_DATA_SPECIAL);
    }

    @Bean
    @Conditional(DemoCondition.class)
    public AutoImportSql blCMSSequenceData() {
        return new AutoImportSql(AutoImportPersistenceUnit.ALL,"config/bc/sql/demo/load_cms_table_sequences.sql", AutoImportStage.ALL_TABLE_SEQUENCE);
    }

    @Bean
    @Conditional({MTCondition.class, DemoCondition.class})
    public AutoImportSql blCMSLateData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/fix_static_asset_data.sql", AutoImportStage.PRIMARY_LATE);
    }
}
