package org.broadleafcommerce.gwt.server.service.module;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.server.cto.BaseCtoConverter;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.w3c.dom.DOMException;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

public interface RecordHelper {

	public BaseCtoConverter getCtoConverter(PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties);
	
	public Entity[] getRecords(Map<String, FieldMetadata> primaryMergedProperties, List<Serializable> records, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException;
	
	public Entity[] getRecords(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, List<Serializable> records) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException;
	
	public Entity getRecord(Map<String, FieldMetadata> primaryMergedProperties, Serializable record, Map<String, FieldMetadata> alternateMergedProperties, String pathToTargetObject) throws ParserConfigurationException, DOMException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, TransformerFactoryConfigurationError, TransformerConfigurationException, IllegalArgumentException, TransformerException, SecurityException, ClassNotFoundException;
	
	public Entity getRecord(Class<?> ceilingEntityClass, PersistencePerspective persistencePerspective, Serializable record) throws SecurityException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, DOMException, TransformerConfigurationException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException;
	
	public int getTotalRecords(String ceilingEntityFullyQualifiedClassname, CriteriaTransferObject cto, BaseCtoConverter ctoConverter) throws ClassNotFoundException;
	
	public Serializable createPopulatedInstance(Serializable instance, Entity entity, Map<String, FieldMetadata> mergedProperties, Boolean setId) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, NumberFormatException, InstantiationException, ClassNotFoundException;
	
	public Object getPrimaryKey(Entity entity, Map<String, FieldMetadata> mergedProperties) throws RuntimeException, NumberFormatException;
	
	public Map<String, FieldMetadata> getSimpleMergedProperties(String entityName, PersistencePerspective persistencePerspective, DynamicEntityDao dynamicEntityDao, Class<?>[] entityClasses) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
	
	public FieldManager getFieldManager();
	
}
