package org.broadleafcommerce.common.dialect;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * @author Jeff Fischer
 */
public class Broadleaf2CompatibilityMySQL5InnoDBDialect extends MySQL5InnoDBDialect {

    public Broadleaf2CompatibilityMySQL5InnoDBDialect() {
        super();
        registerColumnType( java.sql.Types.BOOLEAN, "bit" );
    }

}
