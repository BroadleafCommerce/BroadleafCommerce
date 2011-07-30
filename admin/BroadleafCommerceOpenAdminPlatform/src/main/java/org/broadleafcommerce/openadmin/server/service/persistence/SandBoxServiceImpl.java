package org.broadleafcommerce.openadmin.server.service.persistence;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.JoinStructure;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItem;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;
import org.broadleafcommerce.openadmin.client.dto.visitor.PersistencePerspectiveItemVisitor;
import org.broadleafcommerce.openadmin.client.dto.visitor.PersistencePerspectiveItemVisitorAdapter;
import org.broadleafcommerce.openadmin.server.dao.SandBoxEntityDao;
import org.broadleafcommerce.openadmin.server.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("blSandBoxService")
public class SandBoxServiceImpl implements SandBoxService {

	@Resource(name="blSandBoxEntityDao")
	protected SandBoxEntityDao sandBoxDao;
	
	/* (non-Javadoc)
	 * @see org.broadleafcommerce.openadmin.server.service.remote.SandBoxService#saveSandBox(org.broadleafcommerce.openadmin.client.dto.Entity, org.broadleafcommerce.openadmin.client.dto.PersistencePerspective, org.broadleafcommerce.openadmin.client.dto.SandBoxInfo)
	 */
	@Override
	public SandBox saveSandBox(PersistencePackage persistencePackage) {
		SandBox response = createSandBoxEntityFromDto(persistencePackage);
		response = sandBoxDao.merge(response);
		return response;
	}
	
	protected SandBox createSandBoxEntityFromDto(PersistencePackage persistencePackage) {
		SandBoxInfo sandBoxInfo = persistencePackage.getSandBoxInfo();
		Entity entity = persistencePackage.getEntity();
		PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
		SandBox sandBox = new SandBoxImpl();
		sandBox.setName(sandBoxInfo.getSandBox());
		SandBoxItem sandBoxItem = new SandBoxItemImpl();
		sandBox.getSandBoxItems().add(sandBoxItem);
		sandBoxItem.setSandBox(sandBox);
		org.broadleafcommerce.openadmin.server.domain.Entity entityImpl = new EntityImpl();
		sandBoxItem.setEntity(entityImpl);
		entityImpl.setType(StringUtils.join(entity.getType(),','));
		for (Property property : entity.getProperties()){
			org.broadleafcommerce.openadmin.server.domain.Property propertyImpl = new PropertyImpl();
			entityImpl.getProperties().add(propertyImpl);
			propertyImpl.setDisplayValue(property.getDisplayValue());
			propertyImpl.setEntity(entityImpl);
			propertyImpl.setName(property.getName());
			propertyImpl.setValue(property.getValue());
			propertyImpl.setIsDirty(property.getIsDirty());
		}
		final org.broadleafcommerce.openadmin.server.domain.PersistencePerspective persistencePerspectiveImpl = new PersistencePerspectiveImpl();
		sandBoxItem.setPersistencePerspective(persistencePerspectiveImpl);
		for (ForeignKey foreignKey : persistencePerspective.getAdditionalForeignKeys()) {
			org.broadleafcommerce.openadmin.server.domain.ForeignKey foreignKeyImpl = new AdditionalForeignKeyImpl();
			persistencePerspectiveImpl.getAdditionalForeignKeys().add(foreignKeyImpl);
			foreignKeyImpl.setCurrentValue(foreignKey.getCurrentValue());
			foreignKeyImpl.setDataSourceName(foreignKey.getDataSourceName());
			foreignKeyImpl.setDisplayValueProperty(foreignKey.getDisplayValueProperty());
			foreignKeyImpl.setForeignKeyClass(foreignKey.getForeignKeyClass());
			foreignKeyImpl.setManyToField(foreignKey.getManyToField());
			foreignKeyImpl.setRestrictionType(foreignKey.getRestrictionType());
			((AdditionalForeignKeyImpl) foreignKeyImpl).setPersistencePerspective(persistencePerspectiveImpl);
		}
		persistencePerspectiveImpl.setAdditionalNonPersistentProperties(StringUtils.join(persistencePerspective.getAdditionalNonPersistentProperties(), ','));
		persistencePerspectiveImpl.setPopulateToOneFields(persistencePerspective.getPopulateToOneFields());
		persistencePerspectiveImpl.setExcludeFields(StringUtils.join(persistencePerspective.getExcludeFields(), ','));
		persistencePerspectiveImpl.setIncludeFields(StringUtils.join(persistencePerspective.getIncludeFields(), ','));
		org.broadleafcommerce.openadmin.server.domain.OperationTypes operationTypesImpl = new OperationTypesImpl();
		persistencePerspectiveImpl.setOperationTypes(operationTypesImpl);
		operationTypesImpl.setAddType(persistencePerspective.getOperationTypes().getAddType());
		operationTypesImpl.setFetchType(persistencePerspective.getOperationTypes().getFetchType());
		operationTypesImpl.setInspectType(persistencePerspective.getOperationTypes().getInspectType());
		operationTypesImpl.setRemoveType(persistencePerspective.getOperationTypes().getRemoveType());
		operationTypesImpl.setUpdateType(persistencePerspective.getOperationTypes().getUpdateType());
		for (final PersistencePerspectiveItemType type : persistencePerspective.getPersistencePerspectiveItems().keySet()) {
			PersistencePerspectiveItem persistencePerspectiveItem = persistencePerspective.getPersistencePerspectiveItems().get(type);
			PersistencePerspectiveItemVisitor visitor = new PersistencePerspectiveItemVisitorAdapter() {

				@Override
				public void visit(JoinStructure joinStructure) {
					org.broadleafcommerce.openadmin.server.domain.JoinStructure joinStructureImpl = new JoinStructureImpl();
					joinStructureImpl.setInverse(joinStructure.getInverse());
					joinStructureImpl.setJoinStructureEntityClassname(joinStructure.getJoinStructureEntityClassname());
					joinStructureImpl.setLinkedIdProperty(joinStructure.getLinkedIdProperty());
					joinStructureImpl.setLinkedObjectPath(joinStructure.getLinkedObjectPath());
					joinStructureImpl.setName(joinStructure.getName());
					joinStructureImpl.setSortAscending(joinStructure.getSortAscending());
					joinStructureImpl.setSortField(joinStructure.getSortField());
					joinStructureImpl.setTargetIdProperty(joinStructure.getTargetIdProperty());
					joinStructureImpl.setTargetObjectPath(joinStructure.getTargetObjectPath());
					persistencePerspectiveImpl.getPersistencePerspectiveItems().put(type, joinStructureImpl);
				}

				@Override
				public void visit(MapStructure mapStructure) {
					org.broadleafcommerce.openadmin.server.domain.MapStructure mapStructureImpl = new MapStructureImpl();
					mapStructureImpl.setDeleteValueEntity(mapStructure.getDeleteValueEntity());
					mapStructureImpl.setKeyClassName(mapStructure.getKeyClassName());
					mapStructureImpl.setKeyPropertyFriendlyName(mapStructure.getKeyPropertyFriendlyName());
					mapStructureImpl.setKeyPropertyName(mapStructure.getKeyPropertyName());
					mapStructureImpl.setMapProperty(mapStructure.getMapProperty());
					mapStructureImpl.setValueClassName(mapStructure.getValueClassName());
					persistencePerspectiveImpl.getPersistencePerspectiveItems().put(type, mapStructureImpl);
				}

				@Override
				public void visit(SimpleValueMapStructure simpleValueMapStructure) {
					org.broadleafcommerce.openadmin.server.domain.SimpleValueMapStructure simpleValueMapStructureImpl = new SimpleValueMapStructureImpl();
					simpleValueMapStructureImpl.setDeleteValueEntity(simpleValueMapStructure.getDeleteValueEntity());
					simpleValueMapStructureImpl.setKeyClassName(simpleValueMapStructure.getKeyClassName());
					simpleValueMapStructureImpl.setKeyPropertyFriendlyName(simpleValueMapStructure.getKeyPropertyFriendlyName());
					simpleValueMapStructureImpl.setKeyPropertyName(simpleValueMapStructure.getKeyPropertyName());
					simpleValueMapStructureImpl.setMapProperty(simpleValueMapStructure.getMapProperty());
					simpleValueMapStructureImpl.setValueClassName(simpleValueMapStructure.getValueClassName());
					simpleValueMapStructureImpl.setValuePropertyFriendlyName(simpleValueMapStructure.getValuePropertyFriendlyName());
					simpleValueMapStructureImpl.setValuePropertyName(simpleValueMapStructure.getValuePropertyName());
					persistencePerspectiveImpl.getPersistencePerspectiveItems().put(type, simpleValueMapStructureImpl);
				}
				
				@Override
				public void visit(ForeignKey foreignKey) {
					org.broadleafcommerce.openadmin.server.domain.ForeignKey foreignKeyImpl = new ForeignKeyImpl();
					foreignKeyImpl.setCurrentValue(foreignKey.getCurrentValue());
					foreignKeyImpl.setDataSourceName(foreignKey.getDataSourceName());
					foreignKeyImpl.setDisplayValueProperty(foreignKey.getDisplayValueProperty());
					foreignKeyImpl.setForeignKeyClass(foreignKey.getForeignKeyClass());
					foreignKeyImpl.setManyToField(foreignKey.getManyToField());
					foreignKeyImpl.setRestrictionType(foreignKey.getRestrictionType());
					persistencePerspectiveImpl.getPersistencePerspectiveItems().put(type, foreignKeyImpl);
				}
				
			};
			persistencePerspectiveItem.accept(visitor);
		}
		
		return sandBox;
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
}
