package org.broadleafcommerce.cms.admin.client.datasource.pages;

import com.smartgwt.client.data.Criteria;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * Created by jfischer
 */
public class PageTemplateSearchListDataSource extends ListGridDataSource {

    public PageTemplateSearchListDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
    }

    protected Criteria permanentCriteria = new Criteria("locale.defaultFlag", "true");

    public Criteria getPermanentCriteria() {
        return permanentCriteria;
    }

    public void setPermanentCriteria(Criteria permanentCriteria) {
        this.permanentCriteria = permanentCriteria;
    }
}
