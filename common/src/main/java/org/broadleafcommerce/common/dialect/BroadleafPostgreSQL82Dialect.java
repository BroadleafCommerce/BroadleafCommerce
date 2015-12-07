/**
 * 
 */

package org.broadleafcommerce.common.dialect;

import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import java.sql.Types;

/**
 * <p>
 * By default, Postgres maps BLOB types (properties of type byte[] that are annotated with @Lob) as an 'oid' type. The
 * problem with this is that an oid type in a Postgres table is an identifier to some other storage location that can only
 * be read within a transaction. Attempting to add transactional boundaries to these particular reads (which would also
 * have to occur in eagerly-loaded relationships) is too error-prone.
 * 
 * <p>
 * Since Broadleaf's use case for Blob types is relatively small (the oid type is geared for large data, like >1GB) this
 * dialect overrides the given blob type with the 'bytea' type instead. 'bytea' stores the binary data in the table itself,
 * there is no reference to another location.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public class BroadleafPostgreSQL82Dialect extends PostgreSQL82Dialect {

    @Override
    protected void registerColumnType(int code, String name) {
        if (code == Types.BLOB) {
            super.registerColumnType(Types.BLOB, "bytea");
        } else {
            super.registerColumnType(code, name);
        }
    }

    @Override
    public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
        if (Types.BLOB == sqlTypeDescriptor.getSqlType()) {
            return BinaryTypeDescriptor.INSTANCE;
        }
        return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
    }

}
