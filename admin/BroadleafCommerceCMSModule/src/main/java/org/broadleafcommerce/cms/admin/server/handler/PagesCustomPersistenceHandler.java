package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.admin.client.datasource.pages.PagesTreeDataSourceFactory;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageFolder;
import org.broadleafcommerce.cms.page.domain.PageFolderImpl;
import org.broadleafcommerce.cms.page.service.PageService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.service.SandBoxContext;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
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

    private static final Log LOG = LogFactory.getLog(PagesCustomPersistenceHandler.class);

    @Resource(name="blPageService")
	protected PageService pageService;

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return PageFolder.class.getName().equals(ceilingEntityFullyQualifiedClassname);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
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

    protected SandBox getSandBox() {
        return sandBoxService.retrieveSandboxById(SandBoxContext.getSandBoxContext().getSandBoxId());
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			PageFolder adminInstance = (PageFolder) Class.forName(entity.getType()[0]).newInstance();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(PageFolder.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(PageFolder.class.getName(), persistencePerspective, entityClasses);
			adminInstance = (PageFolder) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            if (PageFolderImpl.class.getName().equals(entity.getType()[0])) {
                pageService.addPageFolder(adminInstance, adminInstance.getParentFolder());
            } else {
                pageService.addPage((Page) adminInstance, adminInstance.getParentFolder(), getSandBox());
            }

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

			return adminEntity;
		} catch (Exception e) {
            LOG.error("Unable to add entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
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
            String[] filterValues = cto.get("pageTemplate.locale.defaultFlag").getFilterValues();
            String localeCode;
            if (!ArrayUtils.isEmpty(filterValues)) {
                localeCode = null;
            } else {
                localeCode = cto.get("pageTemplate.locale.localeCode").getFilterValues()[0];
            }
            List<PageFolder> folders = pageService.findPageFolderChildren(getSandBox(), pageOrFolder, localeCode);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(folders);

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> pageProperties = getMergedProperties(PageFolder.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getConfigurationKey(), persistencePerspective.getAdditionalForeignKeys());
            Entity[] pageEntities = helper.getRecords(pageProperties, convertedList);

            DynamicResultSet response = new DynamicResultSet(pageEntities, pageEntities.length);

            return response;
        } catch (Exception e) {
            LOG.error("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, String configurationKey, ForeignKey[] additionalForeignKeys) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			ceilingEntityFullyQualifiedClass.getName(),
			entities,
			null,
			new String[]{},
			additionalForeignKeys,
			MergedPropertyType.PRIMARY,
			populateManyToOneFields,
			includeManyToOneFields,
			excludeManyToOneFields,
            configurationKey,
			""
		);

		return mergedProperties;
	}

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Page.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Serializable persistenceObject = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            if (!Page.class.isAssignableFrom(persistenceObject.getClass())) {
                throw new ServiceException("Unable to update PageFolder instances.");
            }
			Page adminInstance = (Page) persistenceObject;
            adminInstance.getPageFields().size();
            //detach page from the session so that our changes are not persisted here (we want to let the service take care of this)
            adminInstance = (Page) SerializationUtils.clone(adminInstance);
			adminInstance = (Page) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance = pageService.updatePage(adminInstance, getSandBox());

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

			return adminEntity;
		} catch (Exception e) {
            LOG.error("Unable to update entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to update entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
        try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(Page.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(Page.class.getName(), persistencePerspective, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
            Serializable persistenceObject = dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);
            if (!Page.class.isAssignableFrom(persistenceObject.getClass())) {
                PageFolder adminInstance = (PageFolder) persistenceObject;
                pageService.deletePageFolder(adminInstance);
            } else {
			    Page adminInstance = (Page) persistenceObject;
                pageService.deletePage(adminInstance, getSandBox());
            }
		} catch (Exception e) {
            LOG.error("Unable to remove entity for " + entity.getType()[0], e);
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
    }
}
