package org.broadleafcommerce.openadmin.server.service.persistence;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.JoinStructure;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItem;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.*;
import org.broadleafcommerce.openadmin.server.service.exception.SandBoxException;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;
import org.broadleafcommerce.openadmin.server.service.type.ChangeType;
import org.hibernate.SessionFactory;
import org.hibernate.type.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service("blSandBoxService")
public class SandBoxServiceImpl implements SandBoxService {

	//@Resource(name="blSandBoxEntityDao")
	protected SandBoxEntityDao sandBoxDao;

    //@Resource(name="blSandBoxIdGenerationService")
    protected SandBoxIdGenerationService sandBoxIdGenerationService;

    //@Override
    public SandBoxItem retrieveSandBoxItemByTemporaryId(Object temporaryId) {
        return sandBoxDao.retrieveSandBoxItemByTemporaryId(temporaryId);
    }

    //@Resource(name="blSessionFactory")
    protected SessionFactory sessionFactory;

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.remote.SandBoxService#saveSandBox(org.broadleafcommerce.openadmin.client.dto.Entity, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, org.broadleafcommerce.openadmin.client.dto.SandBoxInfo)
	 */
	@Override
	public PersistencePackage saveSandBox(PersistencePackage persistencePackage, ChangeType changeType, PersistenceManager persistenceManager, RecordHelper helper) throws SandBoxException {
        SandBox sandBox = sandBoxDao.readSandBoxByName(persistencePackage.getSandBoxInfo().getSandBox());
        if (sandBox == null) {
            sandBox = createSandBox(persistencePackage);
        }
        SandBoxItem item;
        switch (changeType) {
            default: {
                item = createSandBoxItemFromDto(sandBox, persistencePackage, changeType, null);
                sandBox.getSandBoxItems().add(item);
                sandBoxDao.persist(sandBox);
                break;
            }
            case UPDATE: {
                Object primaryKey = null;
                try {
                    Map idMetadata = getIdMetadata(Class.forName(persistencePackage.getEntity().getType()[0]));
                    primaryKey = getPrimaryKey(idMetadata, persistencePackage.getEntity().findProperty((String) idMetadata.get("name")).getValue());
                } catch (Exception e) {
                    throw new SandBoxException(e);
                }
                item = sandBoxDao.retrieveSandBoxItemByTemporaryId(primaryKey);
                if (item == null) {
                    item = createSandBoxItemFromDto(sandBox, persistencePackage, changeType, primaryKey);
                    sandBox.getSandBoxItems().add(item);
                } else {
                    List<org.broadleafcommerce.openadmin.server.domain.Property> savedProperties = item.getEntity().getProperties();
                    for (final Property property : persistencePackage.getEntity().getProperties()) {
                        if (property.getIsDirty()) {
                            org.broadleafcommerce.openadmin.server.domain.Property matchedProperty = (org.broadleafcommerce.openadmin.server.domain.Property) org.apache.commons.collections.CollectionUtils.find(savedProperties, new Predicate() {
                                @Override
                                public boolean evaluate(Object o) {
                                    return ((org.broadleafcommerce.openadmin.server.domain.Property) o).getName().equals(property.getName());
                                }
                            });
                            if (matchedProperty == null) {
                                throw new SandBoxException("Unable to find the updated dtoProperty ("+property.getName()+") in the persisted record in the database for " + persistencePackage.getEntity().getType()[0] + "("+primaryKey+")");
                            }
                            matchedProperty.setIsDirty(property.getIsDirty());
                            matchedProperty.setValue(property.getValue());
                        }
                    }
                }
                sandBoxDao.merge(sandBox);
                break;
            }
            case DELETE: {
                Object primaryKey = null;
                try {
                    Map idMetadata = getIdMetadata(Class.forName(persistencePackage.getEntity().getType()[0]));
                    primaryKey = getPrimaryKey(idMetadata, persistencePackage.getEntity().findProperty((String) idMetadata.get("name")).getValue());
                } catch (Exception e) {
                    throw new SandBoxException(e);
                }
                item = sandBoxDao.retrieveSandBoxItemByTemporaryId(primaryKey);
                if (item != null) {
                    sandBox.getSandBoxItems().remove(item);
                    sandBoxDao.deleteItem(item);
                }
                item = createSandBoxItemFromDto(sandBox, persistencePackage, changeType, primaryKey);
                sandBox.getSandBoxItems().add(item);
                sandBoxDao.merge(sandBox);
                break;
            }
        }
        try {
            return createPersistencePackage(sandBox, item, persistenceManager);
        } catch (Exception e) {
            throw new SandBoxException(e);
        }
    }

    protected Map<String, Class<?>> getIdMetadata(Class<?> entityClass) {
        Map response = new HashMap();
        org.hibernate.metadata.ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
        String idProperty = metadata.getIdentifierPropertyName();
        response.put("name", idProperty);
        Type idType = metadata.getIdentifierType();
        response.put("type", idType);

        return response;
    }

    protected Object getPrimaryKey(Map idMetadata, String value) {
        Type idType = (Type) idMetadata.get("type");
        Object response;
        if (Long.class.isAssignableFrom(idType.getReturnedClass())) {
            response = Long.valueOf(value);
        } else {
            response = value;
        }

        return response;
    }

    protected String[] getSplitArray(String item, String delim) {
        String[] response = item==null?null:item.split(delim);
        if (!ArrayUtils.isEmpty(response) && response[0].equals("")) {
            response = new String[]{};
        }
        return response;
    }

    protected PersistencePackage createPersistencePackage(SandBox sandBox, SandBoxItem sandBoxItem, PersistenceManager persistenceManager) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        PersistencePackage pkg = new PersistencePackage();
        pkg.setCeilingEntityFullyQualifiedClassname(sandBoxItem.getCeilingEntityFullyQualifiedClassname());
        pkg.setCustomCriteria(getSplitArray(sandBoxItem.getCustomCriteria(),","));
        org.broadleafcommerce.openadmin.server.domain.Entity persistentEntity = sandBoxItem.getEntity();
        Entity dtoEntity = new Entity();
        pkg.setEntity(dtoEntity);
        dtoEntity.setType(getSplitArray(persistentEntity.getType(),","));
        SandBoxInfo info = new SandBoxInfo();
        pkg.setSandBoxInfo(info);
        info.setSandBox(sandBox.getName());
        info.setCommitImmediately(false);
        PersistencePerspective dtoPersistencePerspective = new PersistencePerspective();
        pkg.setPersistencePerspective(dtoPersistencePerspective);
        org.broadleafcommerce.openadmin.server.domain.PersistencePerspective persistentPersistencePerspective = sandBoxItem.getPersistencePerspective();
        List<org.broadleafcommerce.openadmin.server.domain.ForeignKey> persistenceForeignKeyList = persistentPersistencePerspective.getAdditionalForeignKeys();
        ForeignKey[] dtoForeignKeyList = new ForeignKey[persistenceForeignKeyList.size()];
        for (int j=0;j<dtoForeignKeyList.length;j++) {
            ForeignKey dtoForeignKey = new ForeignKey();
            dtoForeignKey.setCurrentValue(persistenceForeignKeyList.get(j).getCurrentValue());
            dtoForeignKey.setDataSourceName(persistenceForeignKeyList.get(j).getDataSourceName());
            dtoForeignKey.setDisplayValueProperty(persistenceForeignKeyList.get(j).getDisplayValueProperty());
            dtoForeignKey.setForeignKeyClass(persistenceForeignKeyList.get(j).getForeignKeyClass());
            dtoForeignKey.setManyToField(persistenceForeignKeyList.get(j).getManyToField());
            dtoForeignKey.setRestrictionType(persistenceForeignKeyList.get(j).getRestrictionType());
            dtoForeignKeyList[j] = dtoForeignKey;
        }
        dtoPersistencePerspective.setAdditionalForeignKeys(dtoForeignKeyList);
        dtoPersistencePerspective.setAdditionalNonPersistentProperties(getSplitArray(persistentPersistencePerspective.getAdditionalNonPersistentProperties(),","));
        dtoPersistencePerspective.setExcludeFields(getSplitArray(persistentPersistencePerspective.getExcludeFields(),","));
        dtoPersistencePerspective.setIncludeFields(getSplitArray(persistentPersistencePerspective.getIncludeFields(),","));
        OperationTypes dtoOperationTypes = new OperationTypes();
        dtoPersistencePerspective.setOperationTypes(dtoOperationTypes);
        dtoOperationTypes.setAddType(persistentPersistencePerspective.getOperationTypes().getAddType());
        dtoOperationTypes.setFetchType(persistentPersistencePerspective.getOperationTypes().getFetchType());
        dtoOperationTypes.setInspectType(persistentPersistencePerspective.getOperationTypes().getInspectType());
        dtoOperationTypes.setRemoveType(persistentPersistencePerspective.getOperationTypes().getRemoveType());
        dtoOperationTypes.setUpdateType(persistentPersistencePerspective.getOperationTypes().getUpdateType());
        dtoPersistencePerspective.setPopulateToOneFields(persistentPersistencePerspective.getPopulateToOneFields());
        final Map<PersistencePerspectiveItemType, PersistencePerspectiveItem> dtoPersistencePerspectiveItemMap = new HashMap<PersistencePerspectiveItemType, PersistencePerspectiveItem>();
        dtoPersistencePerspective.setPersistencePerspectiveItems(dtoPersistencePerspectiveItemMap);
        Map<PersistencePerspectiveItemType, org.broadleafcommerce.openadmin.server.domain.PersistencePerspectiveItem> persistentPersistencePerspectiveItemMap = persistentPersistencePerspective.getPersistencePerspectiveItems();
        for (final PersistencePerspectiveItemType perspectiveItemType : persistentPersistencePerspectiveItemMap.keySet()) {
            org.broadleafcommerce.openadmin.server.domain.PersistencePerspectiveItem persistentPersistencePerspectiveItem = persistentPersistencePerspectiveItemMap.get(perspectiveItemType);
            org.broadleafcommerce.openadmin.server.domain.visitor.PersistencePerspectiveItemVisitor visitor = new org.broadleafcommerce.openadmin.server.domain.visitor.PersistencePerspectiveItemVisitorAdapter() {
                @Override
                public void visit(org.broadleafcommerce.openadmin.server.domain.ForeignKey persistentForeignKey) {
                    ForeignKey dtoForeignKey = new ForeignKey();
                    dtoForeignKey.setCurrentValue(persistentForeignKey.getCurrentValue());
                    dtoForeignKey.setDataSourceName(persistentForeignKey.getDataSourceName());
                    dtoForeignKey.setDisplayValueProperty(persistentForeignKey.getDisplayValueProperty());
                    dtoForeignKey.setForeignKeyClass(persistentForeignKey.getForeignKeyClass());
                    dtoForeignKey.setManyToField(persistentForeignKey.getManyToField());
                    dtoForeignKey.setRestrictionType(persistentForeignKey.getRestrictionType());
                    dtoPersistencePerspectiveItemMap.put(perspectiveItemType, dtoForeignKey);
                }

                @Override
                public void visit(org.broadleafcommerce.openadmin.server.domain.JoinStructure persistentJoinStructure) {
                    JoinStructure dtoJoinStructure = new JoinStructure();
                    dtoJoinStructure.setInverse(persistentJoinStructure.getInverse());
                    dtoJoinStructure.setJoinStructureEntityClassname(persistentJoinStructure.getJoinStructureEntityClassname());
                    dtoJoinStructure.setLinkedIdProperty(persistentJoinStructure.getLinkedIdProperty());
                    dtoJoinStructure.setLinkedObjectPath(persistentJoinStructure.getLinkedObjectPath());
                    dtoJoinStructure.setName(persistentJoinStructure.getName());
                    dtoJoinStructure.setSortAscending(persistentJoinStructure.getSortAscending());
                    dtoJoinStructure.setSortField(persistentJoinStructure.getSortField());
                    dtoJoinStructure.setTargetIdProperty(persistentJoinStructure.getTargetIdProperty());
                    dtoJoinStructure.setTargetObjectPath(persistentJoinStructure.getTargetObjectPath());
                    dtoPersistencePerspectiveItemMap.put(perspectiveItemType, dtoJoinStructure);
                }

                @Override
                public void visit(org.broadleafcommerce.openadmin.server.domain.MapStructure persistentMapStructure) {
                    MapStructure dtoMapStructure = new MapStructure();
                    dtoMapStructure.setDeleteValueEntity(persistentMapStructure.getDeleteValueEntity());
                    dtoMapStructure.setKeyClassName(persistentMapStructure.getKeyClassName());
                    dtoMapStructure.setKeyPropertyFriendlyName(persistentMapStructure.getKeyPropertyFriendlyName());
                    dtoMapStructure.setKeyPropertyName(persistentMapStructure.getKeyPropertyName());
                    dtoMapStructure.setMapProperty(persistentMapStructure.getMapProperty());
                    dtoMapStructure.setValueClassName(persistentMapStructure.getValueClassName());
                    dtoPersistencePerspectiveItemMap.put(perspectiveItemType, dtoMapStructure);
                }

                @Override
                public void visit(org.broadleafcommerce.openadmin.server.domain.SimpleValueMapStructure persistentSimpleValueMapStructure) {
                    SimpleValueMapStructure dtoSimpleValueMapStructure = new SimpleValueMapStructure();
                    dtoSimpleValueMapStructure.setValuePropertyFriendlyName(persistentSimpleValueMapStructure.getValuePropertyFriendlyName());
                    dtoSimpleValueMapStructure.setValuePropertyName(persistentSimpleValueMapStructure.getValuePropertyName());
                    dtoSimpleValueMapStructure.setDeleteValueEntity(persistentSimpleValueMapStructure.getDeleteValueEntity());
                    dtoSimpleValueMapStructure.setKeyClassName(persistentSimpleValueMapStructure.getKeyClassName());
                    dtoSimpleValueMapStructure.setKeyPropertyFriendlyName(persistentSimpleValueMapStructure.getKeyPropertyFriendlyName());
                    dtoSimpleValueMapStructure.setKeyPropertyName(persistentSimpleValueMapStructure.getKeyPropertyName());
                    dtoSimpleValueMapStructure.setMapProperty(persistentSimpleValueMapStructure.getMapProperty());
                    dtoSimpleValueMapStructure.setValueClassName(persistentSimpleValueMapStructure.getValueClassName());
                    dtoPersistencePerspectiveItemMap.put(perspectiveItemType, dtoSimpleValueMapStructure);
                }
            };
            persistentPersistencePerspectiveItem.accept(visitor);
        }
        Property[] dtoPropertyList = new Property[persistentEntity.getProperties().size()];
        List<org.broadleafcommerce.openadmin.server.domain.Property> persistentPropertyList = persistentEntity.getProperties();
        Map idMetadata = getIdMetadata(Class.forName(dtoEntity.getType()[0]));
        String primaryKeyProperty = (String) idMetadata.get("name");
        for (int j=0;j<dtoPropertyList.length;j++) {
            Property dtoProperty = createDtoProperty(persistentPropertyList.get(j), primaryKeyProperty, sandBoxItem.getTemporaryId());
            dtoPropertyList[j] = dtoProperty;
        }
        dtoEntity.setProperties(dtoPropertyList);

        return pkg;
    }

    protected Property createDtoProperty(org.broadleafcommerce.openadmin.server.domain.Property persistentProperty, String primaryKeyProperty, Object primaryKey) {
        Property property = new Property();
        property.setDisplayValue(persistentProperty.getDisplayValue());
        property.setIsDirty(persistentProperty.getIsDirty());
        property.setName(persistentProperty.getName());
        property.getMetadata().setSecondaryType(persistentProperty.getSecondaryType());
        if (persistentProperty.getName().equals(primaryKeyProperty)) {
            property.setValue(primaryKey.toString());
        } else {
            property.setValue(persistentProperty.getValue());
        }
        return property;
    }

    protected SandBox createSandBox(PersistencePackage dtoPersistencePackage) {
        SandBoxInfo sandBoxInfo = dtoPersistencePackage.getSandBoxInfo();
        SandBox sandBox = new SandBoxImpl();
		sandBox.setName(sandBoxInfo.getSandBox());

        sandBox = sandBoxDao.persist(sandBox);

        return sandBox;
    }
	
	protected SandBoxItem createSandBoxItemFromDto(SandBox sandBox, PersistencePackage persistencePackage, ChangeType changeType, Object primaryKey) {
		SandBoxInfo sandBoxInfo = persistencePackage.getSandBoxInfo();
		Entity dtoEntity = persistencePackage.getEntity();
		PersistencePerspective dtoPersistencePerspective = persistencePackage.getPersistencePerspective();
		SandBoxItem sandBoxItem = new SandBoxItemImpl();
		sandBox.getSandBoxItems().add(sandBoxItem);
		sandBoxItem.setSandBox(sandBox);
        sandBoxItem.setCeilingEntityFullyQualifiedClassname(persistencePackage.getCeilingEntityFullyQualifiedClassname());
        sandBoxItem.setCustomCriteria(StringUtils.join(persistencePackage.getCustomCriteria(), ','));
        sandBoxItem.setChangeType(changeType);
        Long temporaryId = (Long) primaryKey;
        if (temporaryId == null) {
            temporaryId = sandBoxIdGenerationService.findNextId("org.broadleafcommerce.openadmin.server.service.persistence.SandBoxService");
        }
        sandBoxItem.setTemporaryId(temporaryId);
		org.broadleafcommerce.openadmin.server.domain.Entity persistentEntity = new EntityImpl();
		sandBoxItem.setEntity(persistentEntity);
		persistentEntity.setType(StringUtils.join(dtoEntity.getType(), ','));
		for (Property dtoProperty : dtoEntity.getProperties()){
			org.broadleafcommerce.openadmin.server.domain.Property persistentProperty = new PropertyImpl();
			persistentEntity.getProperties().add(persistentProperty);
			persistentProperty.setDisplayValue(dtoProperty.getDisplayValue());
			persistentProperty.setEntity(persistentEntity);
			persistentProperty.setName(dtoProperty.getName());
			persistentProperty.setValue(dtoProperty.getValue());
			persistentProperty.setIsDirty(dtoProperty.getIsDirty());
            persistentProperty.setSecondaryType(dtoProperty.getMetadata().getSecondaryType());
		}
		final org.broadleafcommerce.openadmin.server.domain.PersistencePerspective persistentPersistencePerspective = new PersistencePerspectiveImpl();
		sandBoxItem.setPersistencePerspective(persistentPersistencePerspective);
		for (ForeignKey dtoForeignKey : dtoPersistencePerspective.getAdditionalForeignKeys()) {
			org.broadleafcommerce.openadmin.server.domain.ForeignKey persistentForeignKey = new AdditionalForeignKeyImpl();
			persistentPersistencePerspective.getAdditionalForeignKeys().add(persistentForeignKey);
			persistentForeignKey.setCurrentValue(dtoForeignKey.getCurrentValue());
			persistentForeignKey.setDataSourceName(dtoForeignKey.getDataSourceName());
			persistentForeignKey.setDisplayValueProperty(dtoForeignKey.getDisplayValueProperty());
			persistentForeignKey.setForeignKeyClass(dtoForeignKey.getForeignKeyClass());
			persistentForeignKey.setManyToField(dtoForeignKey.getManyToField());
			persistentForeignKey.setRestrictionType(dtoForeignKey.getRestrictionType());
			((AdditionalForeignKeyImpl) persistentForeignKey).setPersistencePerspective(persistentPersistencePerspective);
		}
		persistentPersistencePerspective.setAdditionalNonPersistentProperties(StringUtils.join(dtoPersistencePerspective.getAdditionalNonPersistentProperties(), ','));
		persistentPersistencePerspective.setPopulateToOneFields(dtoPersistencePerspective.getPopulateToOneFields());
		persistentPersistencePerspective.setExcludeFields(StringUtils.join(dtoPersistencePerspective.getExcludeFields(), ','));
		persistentPersistencePerspective.setIncludeFields(StringUtils.join(dtoPersistencePerspective.getIncludeFields(), ','));
		org.broadleafcommerce.openadmin.server.domain.OperationTypes persistentOperationTypes = new OperationTypesImpl();
		persistentPersistencePerspective.setOperationTypes(persistentOperationTypes);
		persistentOperationTypes.setAddType(dtoPersistencePerspective.getOperationTypes().getAddType());
		persistentOperationTypes.setFetchType(dtoPersistencePerspective.getOperationTypes().getFetchType());
		persistentOperationTypes.setInspectType(dtoPersistencePerspective.getOperationTypes().getInspectType());
		persistentOperationTypes.setRemoveType(dtoPersistencePerspective.getOperationTypes().getRemoveType());
		persistentOperationTypes.setUpdateType(dtoPersistencePerspective.getOperationTypes().getUpdateType());
		for (final PersistencePerspectiveItemType type : dtoPersistencePerspective.getPersistencePerspectiveItems().keySet()) {
			PersistencePerspectiveItem dtoPersistencePerspectiveItem = dtoPersistencePerspective.getPersistencePerspectiveItems().get(type);
			org.broadleafcommerce.openadmin.client.dto.visitor.PersistencePerspectiveItemVisitor visitor = new org.broadleafcommerce.openadmin.client.dto.visitor.PersistencePerspectiveItemVisitorAdapter() {

				@Override
				public void visit(JoinStructure dtoJoinStructure) {
					org.broadleafcommerce.openadmin.server.domain.JoinStructure persistentJoinStructure = new JoinStructureImpl();
					persistentJoinStructure.setInverse(dtoJoinStructure.getInverse());
					persistentJoinStructure.setJoinStructureEntityClassname(dtoJoinStructure.getJoinStructureEntityClassname());
					persistentJoinStructure.setLinkedIdProperty(dtoJoinStructure.getLinkedIdProperty());
					persistentJoinStructure.setLinkedObjectPath(dtoJoinStructure.getLinkedObjectPath());
					persistentJoinStructure.setName(dtoJoinStructure.getName());
					persistentJoinStructure.setSortAscending(dtoJoinStructure.getSortAscending());
					persistentJoinStructure.setSortField(dtoJoinStructure.getSortField());
					persistentJoinStructure.setTargetIdProperty(dtoJoinStructure.getTargetIdProperty());
					persistentJoinStructure.setTargetObjectPath(dtoJoinStructure.getTargetObjectPath());
					persistentPersistencePerspective.getPersistencePerspectiveItems().put(type, persistentJoinStructure);
				}

				@Override
				public void visit(MapStructure dtoMapStructure) {
					org.broadleafcommerce.openadmin.server.domain.MapStructure persistentMapStructure = new MapStructureImpl();
					persistentMapStructure.setDeleteValueEntity(dtoMapStructure.getDeleteValueEntity());
					persistentMapStructure.setKeyClassName(dtoMapStructure.getKeyClassName());
					persistentMapStructure.setKeyPropertyFriendlyName(dtoMapStructure.getKeyPropertyFriendlyName());
					persistentMapStructure.setKeyPropertyName(dtoMapStructure.getKeyPropertyName());
					persistentMapStructure.setMapProperty(dtoMapStructure.getMapProperty());
					persistentMapStructure.setValueClassName(dtoMapStructure.getValueClassName());
					persistentPersistencePerspective.getPersistencePerspectiveItems().put(type, persistentMapStructure);
				}

				@Override
				public void visit(SimpleValueMapStructure dtoSimpleValueMapStructure) {
					org.broadleafcommerce.openadmin.server.domain.SimpleValueMapStructure persistentSimpleValueMapStructure = new SimpleValueMapStructureImpl();
					persistentSimpleValueMapStructure.setDeleteValueEntity(dtoSimpleValueMapStructure.getDeleteValueEntity());
					persistentSimpleValueMapStructure.setKeyClassName(dtoSimpleValueMapStructure.getKeyClassName());
					persistentSimpleValueMapStructure.setKeyPropertyFriendlyName(dtoSimpleValueMapStructure.getKeyPropertyFriendlyName());
					persistentSimpleValueMapStructure.setKeyPropertyName(dtoSimpleValueMapStructure.getKeyPropertyName());
					persistentSimpleValueMapStructure.setMapProperty(dtoSimpleValueMapStructure.getMapProperty());
					persistentSimpleValueMapStructure.setValueClassName(dtoSimpleValueMapStructure.getValueClassName());
					persistentSimpleValueMapStructure.setValuePropertyFriendlyName(dtoSimpleValueMapStructure.getValuePropertyFriendlyName());
					persistentSimpleValueMapStructure.setValuePropertyName(dtoSimpleValueMapStructure.getValuePropertyName());
					persistentPersistencePerspective.getPersistencePerspectiveItems().put(type, persistentSimpleValueMapStructure);
				}
				
				@Override
				public void visit(ForeignKey dtoForeignKey) {
					org.broadleafcommerce.openadmin.server.domain.ForeignKey persistentForeignKey = new ForeignKeyImpl();
					persistentForeignKey.setCurrentValue(dtoForeignKey.getCurrentValue());
					persistentForeignKey.setDataSourceName(dtoForeignKey.getDataSourceName());
					persistentForeignKey.setDisplayValueProperty(dtoForeignKey.getDisplayValueProperty());
					persistentForeignKey.setForeignKeyClass(dtoForeignKey.getForeignKeyClass());
					persistentForeignKey.setManyToField(dtoForeignKey.getManyToField());
					persistentForeignKey.setRestrictionType(dtoForeignKey.getRestrictionType());
					persistentPersistencePerspective.getPersistencePerspectiveItems().put(type, persistentForeignKey);
				}
				
			};
			dtoPersistencePerspectiveItem.accept(visitor);
		}
		
		return sandBoxItem;
	}
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.remote.SandBoxService#getSandBoxDao()
	 */
	@Override
	public SandBoxEntityDao getSandBoxDao() {
		return sandBoxDao;
	}

	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.remote.SandBoxService#setSandBoxDao(org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao)
	 */
	@Override
	public void setSandBoxDao(SandBoxEntityDao sandBoxDao) {
		this.sandBoxDao = sandBoxDao;
	}

    public SandBoxIdGenerationService getSandBoxIdGenerationService() {
        return sandBoxIdGenerationService;
    }

    public void setSandBoxIdGenerationService(SandBoxIdGenerationService sandBoxIdGenerationService) {
        this.sandBoxIdGenerationService = sandBoxIdGenerationService;
    }
}
