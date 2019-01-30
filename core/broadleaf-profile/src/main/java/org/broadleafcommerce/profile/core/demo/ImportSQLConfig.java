/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.demo;

import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.AutoImportStage;
import org.broadleafcommerce.common.demo.DemoCondition;
import org.broadleafcommerce.common.demo.ImportCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jeff Fischer
 */
@Configuration("blProfileData")
@Conditional(ImportCondition.class)
public class ImportSQLConfig {

    @Bean
    @Conditional(DemoCondition.class)
    public AutoImportSql blProfileBasicData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/load_code_tables.sql", AutoImportStage.PRIMARY_PRE_BASIC_DATA);
    }

    @Bean
    @Conditional(DemoCondition.class)
    public AutoImportSql blProfileSequenceData() {
        return new AutoImportSql(AutoImportPersistenceUnit.ALL,"config/bc/sql/demo/load_profile_table_sequences.sql", AutoImportStage.ALL_TABLE_SEQUENCE);
    }

}
