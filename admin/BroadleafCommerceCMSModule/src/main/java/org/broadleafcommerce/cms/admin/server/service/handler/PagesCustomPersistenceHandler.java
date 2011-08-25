package org.broadleafcommerce.cms.admin.server.service.handler;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSourceFactory;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/23/11
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PagesCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    @Resource(name="blPageService")
	protected PageService pageService;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return PageFolder.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return PageFolder.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return PageFolder.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return PageFolder.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        return super.add(persistencePackage, dynamicEntityDao, helper);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String parentCategoryId = cto.get(PagesTreeDataSourceFactory.parentFolderForeignKey).getFilterValues()[0];
            PageFolder pageOrFolder = null;
            if (parentCategoryId != null) {
                pageOrFolder = pageService.findPageById(Long.valueOf(parentCategoryId));
            }
            List<PageFolder> folders = pageService.findPageFolderChildren(null, pageOrFolder, null);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(folders);

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> pageProperties = getMergedProperties(PageFolder.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields());
            Entity[] pageEntities = helper.getRecords(pageProperties, convertedList);

            DynamicResultSet response = new DynamicResultSet(pageEntities, pageEntities.length);

            return response;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        super.remove(persistencePackage, dynamicEntityDao, helper);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        return super.update(persistencePackage, dynamicEntityDao, helper);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			ceilingEntityFullyQualifiedClass.getName(),
			entities,
			null,
			new String[]{},
			new ForeignKey[]{},
			MergedPropertyType.PRIMARY,
			populateManyToOneFields,
			includeManyToOneFields,
			excludeManyToOneFields,
			null,
			""
		);

		return mergedProperties;
	}
}
