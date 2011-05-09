package org.broadleafcommerce.gwt.client.datasource.dynamic.module;

import java.util.ArrayList;
import java.util.List;

import org.broadleafcommerce.gwt.client.datasource.relations.JoinStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.Record;

public class JoinStructureModule extends BasicEntityModule {
	
	/**
	 * @param ceilingEntityFullyQualifiedClassname
	 * @param persistencePerspective
	 * @param dataSource
	 * @param service
	 */
	public JoinStructureModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
	}

	@Override
	public CriteriaTransferObject getCto(DSRequest request) {
		JoinStructure joinTable = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		CriteriaTransferObject cto = super.getCto(request);
		if (joinTable.getSortField() != null) {
			FilterAndSortCriteria sortCriteria = cto.get(joinTable.getSortField());
            sortCriteria.setSortAscending(joinTable.getSortAscending()!=null?joinTable.getSortAscending():true);
		}
		return cto;
	}
	
	@Override
	public boolean isCompatible(OperationType operationType) {
    	return OperationType.JOINSTRUCTURE.equals(operationType);
    }
	
	@Override
	public Entity buildEntity(Record record) {
		JoinStructure joinTable = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		Entity entity = super.buildEntity(record);
		//JoinStructure joinTable = (JoinStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
		entity.setType(new String[]{joinTable.getJoinStructureEntityClassname()});
		List<Property> properties = new ArrayList<Property>();
		{
			Property property = new Property();
			property.setName(joinTable.getLinkedObjectPath() + "." + joinTable.getLinkedIdProperty());
			property.setValue(dataSource.stripDuplicateAllowSpecialCharacters(getLinkedValue()));
			properties.add(property);
		}
		{
			Property property = new Property();
			property.setName(joinTable.getTargetObjectPath() + "." + joinTable.getTargetIdProperty());
			String id = dataSource.stripDuplicateAllowSpecialCharacters(dataSource.getPrimaryKeyValue(record));
			if (id == null || id.equals("")) {
				id = dataSource.stripDuplicateAllowSpecialCharacters(record.getAttribute("backup_id"));
			}
			property.setValue(id);
			properties.add(property);
		}
		if (joinTable.getSortField() != null) {
			Property property = new Property();
			property.setName(joinTable.getSortField());
			property.setValue(record.getAttribute(joinTable.getSortField()));
			properties.add(property);
		}
		
		Property[] props = new Property[properties.size() + entity.getProperties().length];
		for (int j=0;j<properties.size();j++){
			props[j] = properties.get(j);
		}
		int count = properties.size();
		for (int j = 0; j<entity.getProperties().length; j++){
			props[count] = entity.getProperties()[j];
			count++;
		}
		entity.setProperties(props);
		
		return entity;
	}
}
