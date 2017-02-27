/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.demo;

import org.broadleafcommerce.common.demo.AutoImportPersistenceUnit;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.broadleafcommerce.common.demo.AutoImportStage;
import org.broadleafcommerce.common.demo.DemoCondition;
import org.broadleafcommerce.common.demo.ImportCondition;
import org.broadleafcommerce.common.demo.MTCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author Jeff Fischer
 */
@Configuration("blCoreData")
@Conditional(ImportCondition.class)
public class ImportSQLConfig {

    @Bean
    public AutoImportSql blFrameworkSecurityData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/load_admin_permissions.sql,config/bc/sql/load_admin_roles.sql,config/bc/sql/load_admin_menu.sql", AutoImportStage.PRIMARY_FRAMEWORK_SECURITY);
    }

    @Bean
    @Conditional(DemoCondition.class)
    public AutoImportSql blFrameworkPreBasicData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/load_catalog_data.sql,config/bc/sql/demo/load_catalog_i18n_data_ES.sql," +
                "config/bc/sql/demo/load_catalog_i18n_data_FR.sql", AutoImportStage.PRIMARY_PRE_BASIC_DATA);
    }

    @Bean
    @Conditional(DemoCondition.class)
    public AutoImportSql blFrameworkSequenceData() {
        return new AutoImportSql(AutoImportPersistenceUnit.ALL,"config/bc/sql/demo/load_framework_table_sequences.sql", AutoImportStage.ALL_TABLE_SEQUENCE);
    }

    @Bean
    @Conditional({MTCondition.class, DemoCondition.class})
    public AutoImportSql blFrameworkLateData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/fix_catalog_data.sql", AutoImportStage.PRIMARY_LATE);
    }
    
    @Bean
    @Conditional({AssetFoldersExistCondition.class, DemoCondition.class})
    public AutoImportSql blAssetFolderData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/populate_asset_folders.sql", AutoImportStage.PRIMARY_POST_BASIC_DATA);
    }

    @Bean
    @Conditional({AssetFoldersExistCondition.class, GiftCardAndCustomerCreditExistCondition.class, DemoCondition.class})
    public AutoImportSql blAssetFolderGiftCardData() {
        return new AutoImportSql(AutoImportPersistenceUnit.BL_PU,"config/bc/sql/demo/populate_asset_folders_gift_cards.sql", AutoImportStage.PRIMARY_POST_BASIC_DATA);
    }
    
    public static class AssetFoldersExistCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return ClassUtils.isPresent("com.broadleafcommerce.enterprise.foldering.admin.domain.AssetFolder", context.getClassLoader());
        }
        
    }

    public static class GiftCardAndCustomerCreditExistCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return ClassUtils.isPresent("com.broadleafcommerce.accountcredit.profile.core.domain.GiftCardAccount", context.getClassLoader());
        }

    }
}
