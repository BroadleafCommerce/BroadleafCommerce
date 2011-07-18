package org.broadleafcommerce.openadmin.server.dao;

import org.broadleafcommerce.openadmin.server.domain.SandBox;

public interface SandBoxEntityDao {

	public abstract SandBox persist(SandBox entity);

	public abstract SandBox merge(SandBox entity);

	public abstract SandBox retrieve(Class<SandBox> entityClass,
			Object primaryKey);

}