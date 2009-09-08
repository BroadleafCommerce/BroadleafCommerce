package org.broadleafcommerce.dialect;

import org.hibernate.dialect.MySQLInnoDBDialect;
import org.hibernate.util.StringHelper;

public class MySQLInnoDB_BLC_DDialect extends MySQLInnoDBDialect{

	@Override
	public String getAddForeignKeyConstraintString(
			String constraintName, 
			String[] foreignKey, 
			String referencedTable, 
			String[] primaryKey, boolean referencesPrimaryKey
	) {
		String cols = StringHelper.join(", ", foreignKey);
		return new StringBuffer(30)
			.append(" add constraint ")
			.append(constraintName)
			.append(" foreign key (")
			.append(cols)
			.append(") references ")
			.append(referencedTable)
			.append(" (")
			.append( StringHelper.join(", ", primaryKey) )
			.append(')')
			.toString();
	}
}
