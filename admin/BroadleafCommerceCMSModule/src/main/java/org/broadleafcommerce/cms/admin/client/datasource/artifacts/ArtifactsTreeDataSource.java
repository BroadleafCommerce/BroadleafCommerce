package org.broadleafcommerce.cms.admin.client.datasource.artifacts;

import com.smartgwt.client.data.Criteria;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.TreeGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * Created by jfischer
 */
public class ArtifactsTreeDataSource extends TreeGridDataSource {

    public ArtifactsTreeDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName) {
        super(name, persistencePerspective, service, modules, rootId, rootName);
    }

    protected Criteria permanentCriteria = new Criteria("pageTemplate.locale.localeName", "default");

    public Criteria getPermanentCriteria() {
        return permanentCriteria;
    }

    public void setPermanentCriteria(Criteria permanentCriteria) {
        this.permanentCriteria = permanentCriteria;
    }
}
