package org.broadleafcommerce.common.dialect;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.OracleEnumJdbcType;
import org.hibernate.dialect.OracleJdbcHelper;
import org.hibernate.dialect.OracleJsonJdbcType;
import org.hibernate.dialect.OracleOrdinalEnumJdbcType;
import org.hibernate.dialect.OracleReflectionStructJdbcType;
import org.hibernate.dialect.OracleXmlJdbcType;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.JavaObjectType;
import org.hibernate.type.NullType;
import org.hibernate.type.descriptor.jdbc.ArrayJdbcType;
import org.hibernate.type.descriptor.jdbc.BlobJdbcType;
import org.hibernate.type.descriptor.jdbc.NullJdbcType;
import org.hibernate.type.descriptor.jdbc.ObjectJdbcType;
import org.hibernate.type.descriptor.jdbc.ObjectNullAsNullTypeJdbcType;
import org.hibernate.type.descriptor.jdbc.OracleJsonBlobJdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;

import java.sql.Types;

import static org.hibernate.dialect.OracleJdbcHelper.getArrayJdbcTypeConstructor;
import static org.hibernate.dialect.OracleJdbcHelper.getNestedTableJdbcTypeConstructor;
import static org.hibernate.type.SqlTypes.BOOLEAN;

public class BroadleafOracleDialect extends OracleDialect {

	@Override
	protected String columnType(int sqlTypeCode) {
		if (sqlTypeCode == BOOLEAN) {
			return "number(1,0)";
		}
		return super.columnType(sqlTypeCode);
	}

	@Override
	public int getPreferredSqlTypeCodeForBoolean() {
		return Types.BIT;
	}

	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.contributeTypes( typeContributions, serviceRegistry );
		typeContributions.contributeJdbcType( OracleXmlJdbcType.INSTANCE );
		if ( OracleJdbcHelper.isUsable( serviceRegistry ) ) {
			typeContributions.contributeJdbcType( OracleJdbcHelper.getStructJdbcType( serviceRegistry ) );
		}
		else {
			typeContributions.contributeJdbcType( OracleReflectionStructJdbcType.INSTANCE );
		}

		// account for Oracle's deprecated support for LONGVARBINARY
		// prefer BLOB, unless the user explicitly opts out
		final boolean preferLong = serviceRegistry.requireService( ConfigurationService.class )
				.getSetting( PREFER_LONG_RAW, StandardConverters.BOOLEAN, false );
		typeContributions.contributeJdbcType( preferLong ? BlobJdbcType.PRIMITIVE_ARRAY_BINDING : BlobJdbcType.DEFAULT );

		if ( getVersion().isSameOrAfter( 21 ) ) {
			typeContributions.contributeJdbcType( OracleJsonJdbcType.INSTANCE );
		}
		else {
			typeContributions.contributeJdbcType( OracleJsonBlobJdbcType.INSTANCE );
		}

		if ( OracleJdbcHelper.isUsable( serviceRegistry ) ) {
			// Register a JdbcType to allow reading from native queries
			typeContributions.contributeJdbcType( new ArrayJdbcType( ObjectJdbcType.INSTANCE ) );
			typeContributions.contributeJdbcTypeConstructor( getArrayJdbcTypeConstructor( serviceRegistry ) );
			typeContributions.contributeJdbcTypeConstructor( getNestedTableJdbcTypeConstructor( serviceRegistry ) );
		}
		else {
			typeContributions.contributeJdbcType( OracleReflectionStructJdbcType.INSTANCE );
		}
		// Oracle requires a custom binder for binding untyped nulls with the NULL type
		typeContributions.contributeJdbcType( NullJdbcType.INSTANCE );
		typeContributions.contributeJdbcType( ObjectNullAsNullTypeJdbcType.INSTANCE );

		// Until we remove StandardBasicTypes, we have to keep this
		typeContributions.contributeType(
				new NullType(
						NullJdbcType.INSTANCE,
						typeContributions.getTypeConfiguration()
								.getJavaTypeRegistry()
								.getDescriptor( Object.class )
				)
		);
		typeContributions.contributeType(
				new JavaObjectType(
						ObjectNullAsNullTypeJdbcType.INSTANCE,
						typeContributions.getTypeConfiguration()
								.getJavaTypeRegistry()
								.getDescriptor( Object.class )
				)
		);

		if(getVersion().isSameOrAfter(23)) {
			final JdbcTypeRegistry jdbcTypeRegistry = typeContributions.getTypeConfiguration().getJdbcTypeRegistry();
			jdbcTypeRegistry.addDescriptor(OracleEnumJdbcType.INSTANCE);
			jdbcTypeRegistry.addDescriptor(OracleOrdinalEnumJdbcType.INSTANCE);
		}
	}

}
