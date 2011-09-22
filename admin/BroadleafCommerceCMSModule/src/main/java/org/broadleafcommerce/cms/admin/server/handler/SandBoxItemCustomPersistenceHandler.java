package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.criteria.PersistentEntityCriteria;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.domain.SandBox;
import org.broadleafcommerce.openadmin.server.domain.SandBoxItem;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import javax.annotation.Resource;
import java.io.Serializable;
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
public class SandBoxItemCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    private Log LOG = LogFactory.getLog(SandBoxItemCustomPersistenceHandler.class);

    @Resource(name="blSandBoxService")
    protected SandBoxService sandBoxService;

    @Resource(name="blAdminSecurityService")
    protected AdminSecurityService adminSecurityService;

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        return SandBoxItem.class.getName().equals(ceilingEntityFullyQualifiedClassname);
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

    protected SandBox getSandBox(PersistencePackage persistencePackage) {
        return sandBoxService.retrieveSandboxById(persistencePackage.getSandBoxInfo().getSandBox());
    }

    protected List<SandBoxItem> retrieveSandBoxItems(List<Long> ids, DynamicEntityDao dynamicEntityDao) {
        Criteria criteria = dynamicEntityDao.createCriteria(SandBoxItem.class);
        criteria.add(Restrictions.in("id", ids));
        return criteria.list();
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        String[] customCriteria = persistencePackage.getCustomCriteria();
        if (ArrayUtils.isEmpty(customCriteria) || customCriteria.length != 3) {
            ServiceException e = new ServiceException("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname);
            LOG.error("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname, e);
            throw e;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            ServiceException e = new ServiceException("Unable to determine current user logged in status");
            LOG.error("Unable to determine current user logged in status", e);
            throw e;
        }
        try {
            String operation = customCriteria[0];
            List<Long> targets = new ArrayList<Long>();
            if (!StringUtils.isEmpty(customCriteria[1])) {
                String[] parts = customCriteria[1].split(",");
                for (String part : parts) {
                    try {
                        targets.add(Long.valueOf(part));
                    } catch (NumberFormatException e) {
                        //do nothing
                    }
                }
            }
            String comment = customCriteria[2];
            User principal = (User) authentication.getPrincipal();
            AdminUser adminUser = adminSecurityService.readAdminUserByUserName(principal.getUsername());

            if (operation.equals("promoteAll")) {
                sandBoxService.promoteAllSandBoxItems(adminUser, getSandBox(persistencePackage), comment);
            } else if (operation.equals("promoteSelected")) {
                List<SandBoxItem> items = retrieveSandBoxItems(targets, dynamicEntityDao);
                sandBoxService.promoteSelectedItems(adminUser, getSandBox(persistencePackage), comment, items);
            } else if (operation.equals("revertAll")) {
                sandBoxService.revertAllSandBoxItems(adminUser, getSandBox(persistencePackage));
            } else if (operation.equals("revertSelected")) {
                List<SandBoxItem> items = retrieveSandBoxItems(targets, dynamicEntityDao);
                sandBoxService.revertSelectedSandBoxItems(adminUser, getSandBox(persistencePackage), items);
            }

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(SandBoxItem.class);
            Map<String, FieldMetadata> originalProps = helper.getSimpleMergedProperties(SandBoxItem.class.getName(), persistencePerspective, entities);
            cto.get("sandBox").setFilterValue(persistencePackage.getSandBoxInfo().getSandBox().toString());
            BaseCtoConverter ctoConverter = helper.getCtoConverter(persistencePerspective, cto, SandBoxItem.class.getName(), originalProps);
            PersistentEntityCriteria queryCriteria = ctoConverter.convert(cto, SandBoxItem.class.getName());
            List<Serializable> records = dynamicEntityDao.query(queryCriteria, SandBoxItem.class);
            Entity[] results = helper.getRecords(originalProps, records);
            int totalRecords = helper.getTotalRecords(SandBoxItem.class.getName(), cto, ctoConverter);

            DynamicResultSet response = new DynamicResultSet(results, totalRecords);

            return response;
        } catch (Exception e) {
            LOG.error("Unable to execute persistence activity", e);
            throw new ServiceException("Unable to execute persistence activity for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        ServiceException e = new ServiceException("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname);
        LOG.error("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname, e);
        throw e;
    }

    @Override
    public Entity update(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        ServiceException e = new ServiceException("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname);
        LOG.error("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname, e);
        throw e;
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        ServiceException e = new ServiceException("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname);
        LOG.error("Invalid request for entity: " + ceilingEntityFullyQualifiedClassname, e);
        throw e;
    }
}
