/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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

import static org.junit.Assert.assertEquals;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.common.demo.AutoImportSql;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(classes = {ImportSQLConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ImportSQLConfigDiffblueTest {
  @Autowired
  private ImportSQLConfig importSQLConfig;

  /**
   * Test {@link ImportSQLConfig#blProfileBasicData()}.
   * <ul>
   *   <li>Given {@link ImportSQLConfig}.</li>
   * </ul>
   * <p>
   * Method under test: {@link ImportSQLConfig#blProfileBasicData()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"AutoImportSql ImportSQLConfig.blProfileBasicData()"})
  public void testBlProfileBasicData_givenImportSQLConfig() {
    // Arrange and Act
    AutoImportSql actualBlProfileBasicDataResult = importSQLConfig.blProfileBasicData();

    // Assert
    assertEquals("blPU", actualBlProfileBasicDataResult.getPersistenceUnit());
    assertEquals("config/bc/sql/demo/load_code_tables.sql", actualBlProfileBasicDataResult.getSqlFilePath());
    assertEquals(5000, actualBlProfileBasicDataResult.getOrder());
  }

  /**
   * Test {@link ImportSQLConfig#blProfileBasicData()}.
   * <ul>
   *   <li>Given {@link ImportSQLConfig} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link ImportSQLConfig#blProfileBasicData()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"AutoImportSql ImportSQLConfig.blProfileBasicData()"})
  public void testBlProfileBasicData_givenImportSQLConfig2() {
    // Arrange and Act
    AutoImportSql actualBlProfileBasicDataResult = (new ImportSQLConfig()).blProfileBasicData();

    // Assert
    assertEquals("blPU", actualBlProfileBasicDataResult.getPersistenceUnit());
    assertEquals("config/bc/sql/demo/load_code_tables.sql", actualBlProfileBasicDataResult.getSqlFilePath());
    assertEquals(5000, actualBlProfileBasicDataResult.getOrder());
  }
}
