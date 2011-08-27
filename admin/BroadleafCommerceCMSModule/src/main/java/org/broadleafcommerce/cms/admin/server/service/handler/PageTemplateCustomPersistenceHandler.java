package org.broadleafcommerce.cms.admin.server.service.handler;

import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jfischer
 */
public class PageTemplateCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    @Resource(name="blPageService")
	protected PageService pageService;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return
                PageTemplate.class.getName().equals(ceilingEntityFullyQualifiedClassname) &&
                persistencePackage.getCustomCriteria() != null &&
                persistencePackage.getCustomCriteria().length > 0 &&
                persistencePackage.getCustomCriteria()[0].equals("constructForm");
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleFetch(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String pageTemplateId = persistencePackage.getCustomCriteria()[1];
            PageTemplate template = pageService.findPageTemplateById(Long.valueOf(pageTemplateId));
            ClassMetadata metadata = new ClassMetadata();
            metadata.setCeilingType(PageTemplate.class.getName());
            PolymorphicEntity entity = new PolymorphicEntity();
            entity.setName("PageTemplateImpl");
            entity.setType(PageTemplateImpl.class.getName());
            PolymorphicEntity[] entities = new PolymorphicEntity[]{entity};
            metadata.setPolymorphicEntities(entities);

            List<Property> properties = new ArrayList<Property>();
            for (FieldGroup group : template.getFieldGroups()) {
                for (FieldDefinition definition : group.getFieldDefinitions()) {

                }
            }

            //TODO finish inspection implementation that will yield the datasource information necessary to render the page template form in the ui
            return null;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }
}
