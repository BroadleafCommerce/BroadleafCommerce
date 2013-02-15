/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.factory.PersistencePackageFactory;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

/**
 * @author Andre Azzolini (apazzolini)
 */
@Service("blAdminEntityService")
public class AdminEntityServiceImpl implements AdminEntityService {

    @Resource(name = "blDynamicEntityRemoteService")
    protected DynamicEntityService service;

    @Resource(name = "blPersistencePackageFactory")
    protected PersistencePackageFactory persistencePackageFactory;

    @Override
    public ClassMetadata getClassMetadata(String className) throws ServiceException, ApplicationSecurityException {
        PersistencePackageRequest request = PersistencePackageRequest.standard()
                .withClassName(className);
        return getClassMetadata(request);
    }

    @Override
    public ClassMetadata getClassMetadata(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        ClassMetadata cmd = inspect(request).getClassMetaData();
        cmd.setCeilingType(request.getClassName());
        return cmd;
    }

    @Override
    public Entity[] getRecords(PersistencePackageRequest request) throws ServiceException, ApplicationSecurityException {
        return fetch(request).getRecords();
    }

    @Override
    public Entity getRecord(String className, String id) throws ServiceException, ApplicationSecurityException {
        FilterAndSortCriteria fasc = new FilterAndSortCriteria("id");
        fasc.setFilterValue(id);

        PersistencePackageRequest request = PersistencePackageRequest.standard()
                .withClassName(className)
                .addFilterAndSortCriteria(fasc);

        Entity[] entities = fetch(request).getRecords();

        if (entities == null || entities.length > 1) {
            throw new RuntimeException("More than one entity found with the same id");
        }

        Entity entity = entities[0];
        return entity;
    }

    @Override
    public Entity[] getRecordsForCollection(final ClassMetadata containingClassMetadata, final String containingEntityId,
            final Property collectionProperty)
            throws ServiceException, ApplicationSecurityException {
        final PersistencePackageRequest request = new PersistencePackageRequest();

        collectionProperty.getMetadata().accept(new MetadataVisitor() {

            @Override
            public void visit(MapMetadata fmd) {
                ForeignKey foreignField = (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems()
                        .get(PersistencePerspectiveItemType.FOREIGNKEY);

                MapStructure mapStructure = (MapStructure) fmd.getPersistencePerspective()
                        .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);

                FilterAndSortCriteria fasc = new FilterAndSortCriteria(foreignField.getManyToField());
                fasc.setFilterValue(containingEntityId);

                request.withType(PersistencePackageRequest.Type.MAP)
                        .withClassName(fmd.getTargetClass())
                        .withMapStructure(mapStructure)
                        .addForeignKey(foreignField)
                        .addFilterAndSortCriteria(fasc);
            }

            @Override
            public void visit(AdornedTargetCollectionMetadata fmd) {
                AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                        .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);

                FilterAndSortCriteria fasc = new FilterAndSortCriteria(adornedList.getCollectionFieldName());
                fasc.setFilterValue(containingEntityId);

                request.withType(PersistencePackageRequest.Type.ADORNED)
                        .withClassName(fmd.getCollectionCeilingEntity())
                        .withAdornedList(adornedList)
                        .addFilterAndSortCriteria(fasc);
            }

            @Override
            public void visit(BasicCollectionMetadata fmd) {
                ForeignKey foreignField = (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems()
                        .get(PersistencePerspectiveItemType.FOREIGNKEY);

                FilterAndSortCriteria fasc = new FilterAndSortCriteria(foreignField.getManyToField());
                fasc.setFilterValue(containingEntityId);

                request.withType(PersistencePackageRequest.Type.STANDARD)
                        .withClassName(fmd.getCollectionCeilingEntity())
                        .addForeignKey(foreignField)
                        .addFilterAndSortCriteria(fasc);
            }

            @Override
            public void visit(BasicFieldMetadata fmd) {
                throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was not a " +
                        "collection field.", collectionProperty.getName(), containingClassMetadata.getCeilingType()));
            }
        });

        try {
            return fetch(request).getRecords();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Map<String, Entity[]> getRecordsForAllSubCollections(final String containingClassName,
            final String containingEntityId)
            throws ServiceException, ApplicationSecurityException {
        final Map<String, Entity[]> map = new HashMap<String, Entity[]>();

        final ClassMetadata cmd = getClassMetadata(containingClassName);
        for (final Property p : cmd.getProperties()) {
            if (p.getMetadata() instanceof CollectionMetadata) {
                try {
                    Entity[] rows = getRecordsForCollection(cmd, containingEntityId, p);
                    map.put(p.getName(), rows);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return map;
    }

    @Override
    public Entity addSubCollectionEntity(EntityForm entityForm, String className, final String fieldName,
            final String parentId)
            throws ServiceException, ApplicationSecurityException, ClassNotFoundException {
        // Find the FieldMetadata for this collection field
        final ClassMetadata cmd = getClassMetadata(className);

        final List<Property> properties = new ArrayList<Property>();
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties.add(p);
        }

        final PersistencePackageRequest request = new PersistencePackageRequest()
                .withEntity(new Entity());

        cmd.getPMap().get(fieldName).getMetadata().accept(new MetadataVisitor() {
            @Override
            public void visit(MapMetadata fmd) {
                request.setType(PersistencePackageRequest.Type.MAP);
                //TODO apa
            }

            @Override
            public void visit(AdornedTargetCollectionMetadata fmd) {
                AdornedTargetList adornedList = (AdornedTargetList) fmd.getPersistencePerspective()
                        .getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);

                request.setType(PersistencePackageRequest.Type.ADORNED);
                request.getEntity().setType(new String[] { adornedList.getAdornedTargetEntityClassname() });
                request.setAdornedList(adornedList);
            }

            @Override
            public void visit(BasicCollectionMetadata fmd) {
                ForeignKey foreignField = null;
                if (fmd != null) {
                    foreignField = (ForeignKey) fmd.getPersistencePerspective().getPersistencePerspectiveItems()
                            .get(PersistencePerspectiveItemType.FOREIGNKEY);
                    Property fp = new Property();
                    fp.setName(foreignField.getManyToField());
                    fp.setValue(parentId);
                    properties.add(fp);
                }

                request.setType(PersistencePackageRequest.Type.STANDARD);
                request.getEntity().setType(new String[] { fmd.getCollectionCeilingEntity() });
                request.addForeignKey(foreignField);
            }

            @Override
            public void visit(BasicFieldMetadata fmd) {
                throw new IllegalArgumentException(String.format("The specified field [%s] for class [%s] was" +
                        " not a collection field.", fieldName, cmd.getCeilingType()));
            }
        });

        Property[] propArr = new Property[properties.size()];
        properties.toArray(propArr);
        request.getEntity().setProperties(propArr);

        request.withClassName(request.getEntity().getType()[0]);

        return add(request);
    }

    @Override
    public Entity updateEntity(EntityForm entityForm, String className)
            throws ServiceException, ApplicationSecurityException {
        // Build the property array from the field map
        Property[] properties = new Property[entityForm.getFields().size()];
        int i = 0;
        for (Entry<String, Field> entry : entityForm.getFields().entrySet()) {
            Property p = new Property();
            p.setName(entry.getKey());
            p.setValue(entry.getValue().getValue());
            properties[i++] = p;
        }

        Entity entity = new Entity();
        entity.setProperties(properties);
        entity.setType(new String[] { entityForm.getEntityType() });

        PersistencePackageRequest request = PersistencePackageRequest.standard()
                .withEntity(entity)
                .withClassName(className);
        return update(request);
    }

    protected Entity add(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.add(pkg);
    }

    protected Entity update(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.update(pkg);
    }

    protected DynamicResultSet inspect(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);
        return service.inspect(pkg);
    }

    protected DynamicResultSet fetch(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException {
        PersistencePackage pkg = persistencePackageFactory.create(request);

        CriteriaTransferObject cto = getDefaultCto();
        if (request.getFilterAndSortCriteria() != null) {
            cto.addAll(request.getFilterAndSortCriteria());
        }

        return service.fetch(pkg, cto);
    }

    protected CriteriaTransferObject getDefaultCto() {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        cto.setFirstResult(0);
        cto.setMaxResults(75);
        return cto;
    }

}