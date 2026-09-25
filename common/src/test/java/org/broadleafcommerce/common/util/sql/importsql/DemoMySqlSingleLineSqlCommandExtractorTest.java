/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.common.util.sql.importsql;

import org.hibernate.dialect.MySQLDialect;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DemoMySqlSingleLineSqlCommandExtractorTest {

    protected DemoMySqlSingleLineSqlCommandExtractor extractor = new DemoMySqlSingleLineSqlCommandExtractor();

    protected String convert(String command) {
        return extractor.uppercaseTableNames(command);
    }

    @Test
    public void testUppercasesTableNames() {
        assertEquals("INSERT INTO BLC_SITE (SITE_ID, NAME) VALUES (1, 'Blair''s');",
                convert("INSERT INTO blc_site (SITE_ID, NAME) VALUES (1, 'Blair''s');"));
        assertEquals("UPDATE BLC_SITE SET default_catalog=100 where site_id=100;",
                convert("UPDATE blc_site SET default_catalog=100 where site_id=100;"));
        assertEquals("delete from BLC_SITE_CATALOG where SITE_ID = 1;",
                convert("delete from blc_site_catalog where SITE_ID = 1;"));
        assertEquals("INSERT INTO BLC_SITE_CATALOG (SITE_ID) SELECT s.SITE_ID FROM BLC_SITE s JOIN BLC_CATALOG c ON c.OWNING_SITE = s.SITE_ID;",
                convert("INSERT INTO blc_site_catalog (SITE_ID) SELECT s.SITE_ID FROM blc_site s JOIN blc_catalog c ON c.OWNING_SITE = s.SITE_ID;"));
    }

    @Test
    public void testLeavesUppercaseCommandsUnchanged() {
        String command = "INSERT INTO BLC_SITE (SITE_ID, NAME) VALUES (-1, 'Master Site');";
        assertEquals(command, convert(command));
    }

    @Test
    public void testLeavesStringLiteralsUnchanged() {
        assertEquals("INSERT INTO BLC_PAGE (TITLE, BODY) VALUES ('update from us', 'Join into \\'the\\' club from here');",
                convert("INSERT INTO blc_page (TITLE, BODY) VALUES ('update from us', 'Join into \\'the\\' club from here');"));
        assertEquals("INSERT INTO BLC_PAGE (BODY) VALUES (\"select * from users\");",
                convert("INSERT INTO blc_page (BODY) VALUES (\"select * from users\");"));
    }

    @Test
    public void testExtractCommands() {
        String script = "-- Sites\n"
                + "INSERT INTO blc_site (SITE_ID, NAME) VALUES (1, 'from blc_site');\n"
                + "UPDATE blc_site SET VENDOR_ID = 1 WHERE SITE_ID = 1;\n";
        List<String> commands = extractor.extractCommands(new StringReader(script), new MySQLDialect());
        assertEquals(2, commands.size());
        assertEquals("INSERT INTO BLC_SITE (SITE_ID, NAME) VALUES (1, 'from blc_site')", commands.get(0));
        assertEquals("UPDATE BLC_SITE SET VENDOR_ID = 1 WHERE SITE_ID = 1", commands.get(1));
    }

}
