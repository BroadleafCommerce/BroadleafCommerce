package org.broadleafcommerce.openadmin.server.service.persistence.module.provider;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.AssociationPathElement;
import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.ForeignKeyRestrictionType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.cto.BaseCtoConverter;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.DataFormatProvider;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldNotAvailableException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceException;
import org.broadleafcommerce.openadmin.web.rulebuilder.MVELToDataWrapperTranslator;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Embedded;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Jeff Fischer
 */
@Component("blBasicPersistenceProvider")
@Scope("prototype")
public class BasicPersistenceProvider extends AbstractPersistenceProvider {

    public boolean canHandlePersistence(Object instance, BasicFieldMetadata metadata) {
        return metadata.getFieldType() == SupportedFieldType.BOOLEAN ||
                metadata.getFieldType() == SupportedFieldType.DATE ||
                metadata.getFieldType() == SupportedFieldType.DECIMAL ||
                metadata.getFieldType() == SupportedFieldType.MONEY ||
                metadata.getFieldType() == SupportedFieldType.INTEGER ||
                metadata.getFieldType() == SupportedFieldType.EMAIL ||
                metadata.getFieldType() == SupportedFieldType.FOREIGN_KEY ||
                metadata.getFieldType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY ||
                metadata.getFieldType() == SupportedFieldType.ID;
    }

    @Override
    public boolean canHandleFilterMapping(BasicFieldMetadata metadata) {
        return canHandlePersistence(null, metadata);
    }

    @Override
    public boolean canHandleFilterProperties(Entity entity, Map<String, FieldMetadata> unfilteredProperties) {
        return false;
    }

    public void populateValue(Serializable instance, Boolean setId, FieldManager fieldManager, Property property,
                              BasicFieldMetadata metadata, Class<?> returnType, String value, PersistenceManager persistenceManager,
                              DataFormatProvider dataFormatProvider) throws PersistenceException {
        try {
            switch (metadata.getFieldType()) {
                case BOOLEAN:
                    boolean v = Boolean.valueOf(value);
                    try {
                        fieldManager.setFieldValue(instance, property.getName(), v);
                    } catch (IllegalArgumentException e) {
                        char c = v ? 'Y' : 'N';
                        fieldManager.setFieldValue(instance, property.getName(), c);
                    }
                    break;
                case DATE:
                    fieldManager.setFieldValue(instance, property.getName(), dataFormatProvider.getSimpleDateFormatter().parse(value));
                    break;
                case DECIMAL:
                    if (BigDecimal.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
                    } else {
                        fieldManager.setFieldValue(instance, property.getName(), new Double(value));
                    }
                    break;
                case MONEY:
                    if (BigDecimal.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), new BigDecimal(new Double(value)));
                    } else if (Double.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), new Double(value));
                    } else {
                        fieldManager.setFieldValue(instance, property.getName(), new Money(new Double(value)));
                    }
                    break;
                case INTEGER:
                    if (int.class.isAssignableFrom(returnType) || Integer.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), Integer.valueOf(value));
                    } else if (byte.class.isAssignableFrom(returnType) || Byte.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), Byte.valueOf(value));
                    } else if (short.class.isAssignableFrom(returnType) || Short.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), Short.valueOf(value));
                    } else if (long.class.isAssignableFrom(returnType) || Long.class.isAssignableFrom(returnType)) {
                        fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
                    }
                    break;
                case EMAIL:
                    fieldManager.setFieldValue(instance, property.getName(), value);
                    break;
                case FOREIGN_KEY: {
                    Serializable foreignInstance;
                    if (StringUtils.isEmpty(value)) {
                        foreignInstance = null;
                    } else {
                        if (SupportedFieldType.INTEGER.toString().equals(metadata.getSecondaryType().toString())) {
                            foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), Long.valueOf(value));
                        } else {
                            foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), value);
                        }
                    }

                    if (Collection.class.isAssignableFrom(returnType)) {
                        Collection collection;
                        try {
                            collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                        if (!collection.contains(foreignInstance)) {
                            collection.add(foreignInstance);
                        }
                    } else if (Map.class.isAssignableFrom(returnType)) {
                        throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                    } else {
                        fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
                    }
                    break;
                }
                case ADDITIONAL_FOREIGN_KEY: {
                    Serializable foreignInstance;
                    if (StringUtils.isEmpty(value)) {
                        foreignInstance = null;
                    } else {
                        if (SupportedFieldType.INTEGER.toString().equals(metadata.getSecondaryType().toString())) {
                            foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), Long.valueOf(value));
                        } else {
                            foreignInstance = persistenceManager.getDynamicEntityDao().retrieve(Class.forName(metadata.getForeignKeyClass()), value);
                        }
                    }

                    if (Collection.class.isAssignableFrom(returnType)) {
                        Collection collection;
                        try {
                            collection = (Collection) fieldManager.getFieldValue(instance, property.getName());
                        } catch (FieldNotAvailableException e) {
                            throw new IllegalArgumentException(e);
                        }
                        if (!collection.contains(foreignInstance)) {
                            collection.add(foreignInstance);
                        }
                    } else if (Map.class.isAssignableFrom(returnType)) {
                        throw new IllegalArgumentException("Map structures are not supported for foreign key fields.");
                    } else {
                        fieldManager.setFieldValue(instance, property.getName(), foreignInstance);
                    }
                    break;
                }
                case ID:
                    if (setId) {
                        switch (metadata.getSecondaryType()) {
                            case INTEGER:
                                fieldManager.setFieldValue(instance, property.getName(), Long.valueOf(value));
                                break;
                            case STRING:
                                fieldManager.setFieldValue(instance, property.getName(), value);
                                break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void extractValue(List<Property> props, FieldManager fieldManager, MVELToDataWrapperTranslator translator, ObjectMapper mapper, BasicFieldMetadata metadata, Object value, String strVal, Property propertyItem, String displayVal, PersistenceManager persistenceManager, DataFormatProvider dataFormatProvider) throws PersistenceException {
        try {
            if (value != null) {
                if (metadata.getForeignKeyCollection()) {
                    ((BasicFieldMetadata) propertyItem.getMetadata()).setFieldType(metadata.getFieldType());
                    strVal = null;
                } else if (metadata.getFieldType().equals(SupportedFieldType.BOOLEAN) && value instanceof Character) {
                    strVal = (value.equals('Y')) ? "true" : "false";
                } else if (Date.class.isAssignableFrom(value.getClass())) {
                    strVal = dataFormatProvider.getSimpleDateFormatter().format((Date) value);
                } else if (Timestamp.class.isAssignableFrom(value.getClass())) {
                    strVal = dataFormatProvider.getSimpleDateFormatter().format(new Date(((Timestamp) value).getTime()));
                } else if (Calendar.class.isAssignableFrom(value.getClass())) {
                    strVal = dataFormatProvider.getSimpleDateFormatter().format(((Calendar) value).getTime());
                } else if (Double.class.isAssignableFrom(value.getClass())) {
                    strVal = dataFormatProvider.getDecimalFormatter().format(value);
                } else if (BigDecimal.class.isAssignableFrom(value.getClass())) {
                    strVal = dataFormatProvider.getDecimalFormatter().format(((BigDecimal) value).doubleValue());
                } else if (metadata.getForeignKeyClass() != null) {
                    try {
                        strVal = fieldManager.getFieldValue(value, metadata.getForeignKeyProperty()).toString();
                        //see if there's a name property and use it for the display value
                        Object temp = null;
                        try {
                            temp = fieldManager.getFieldValue(value, metadata.getForeignKeyDisplayValueProperty());
                        } catch (FieldNotAvailableException e) {
                            //do nothing
                        }
                        if (temp != null) {
                            displayVal = temp.toString();
                        }
                    } catch (FieldNotAvailableException e) {
                        throw new IllegalArgumentException(e);
                    }
                } else {
                    strVal = value.toString();
                }
                propertyItem.setValue(strVal);
                propertyItem.setDisplayValue(displayVal);
            }
        } catch (IllegalAccessException e) {
            throw new PersistenceException(e);
        }
    }

    public void addFilterMapping(PersistencePerspective persistencePerspective, CriteriaTransferObject cto,
                                 String ceilingEntityFullyQualifiedClassname, Map<String, FieldMetadata> mergedProperties,
                                 BaseCtoConverter ctoConverter, String propertyName, FieldManager fieldManager) {
        AssociationPath associationPath;
        int dotIndex = propertyName.lastIndexOf('.');
        StringBuilder property;
        Class clazz;
        try {
            clazz = Class.forName(mergedProperties.get(propertyName).getInheritedFromType());
        } catch (ClassNotFoundException e) {
            throw new PersistenceException(e);
        }
        Field field = fieldManager.getField(clazz, propertyName);
        Class<?> targetType = null;
        if (field != null) {
            targetType = field.getType();
        }
        if (dotIndex >= 0) {
            property = new StringBuilder(propertyName.substring(dotIndex + 1, propertyName.length()));
            String prefix = propertyName.substring(0, dotIndex);
            StringTokenizer tokens = new StringTokenizer(prefix, ".");
            List<AssociationPathElement> elementList = new ArrayList<AssociationPathElement>(20);
            StringBuilder sb = new StringBuilder(150);
            StringBuilder pathBuilder = new StringBuilder(150);
            while (tokens.hasMoreElements()) {
                String token = tokens.nextToken();
                sb.append(token);
                pathBuilder.append(token);
                field = fieldManager.getField(clazz, pathBuilder.toString());
                Embedded embedded = field.getAnnotation(Embedded.class);
                if (embedded != null) {
                    sb.append('.');
                } else {
                    elementList.add(new AssociationPathElement(sb.toString()));
                    sb = new StringBuilder(150);
                }
                pathBuilder.append('.');
            }
            if (!elementList.isEmpty()) {
                AssociationPathElement[] elements = elementList.toArray(new AssociationPathElement[elementList.size()]);
                associationPath = new AssociationPath(elements);
            } else {
                property = property.insert(0, sb.toString());
                associationPath = AssociationPath.ROOT;
            }
        } else {
            property = new StringBuilder(propertyName);
            associationPath = AssociationPath.ROOT;
        }
        String convertedProperty = property.toString();
        BasicFieldMetadata metadata = (BasicFieldMetadata) mergedProperties.get(propertyName);
        switch (metadata.getFieldType()) {
            case BOOLEAN:
                if (targetType == null || targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
                    ctoConverter.addBooleanMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                } else {
                    ctoConverter.addCharacterMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                }
                break;
            case DATE:
                ctoConverter.addDateMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case DECIMAL:
                ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case MONEY:
                ctoConverter.addDecimalMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case INTEGER:
                ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            default:
                ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case EMAIL:
                ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                break;
            case FOREIGN_KEY:
                if (cto.get(propertyName).getFilterValues().length > 0) {
                    ForeignKey foreignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get
                            (PersistencePerspectiveItemType.FOREIGNKEY);
                    if (metadata.getForeignKeyCollection()) {
                        if (ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                            ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (cto.get(propertyName).getFilterValues()[0] == null || "null".equals(cto.get(propertyName).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addStringEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyName);
                }
                break;
            case ADDITIONAL_FOREIGN_KEY:
                if (cto.get(propertyName).getFilterValues().length > 0) {
                    int additionalForeignKeyIndexPosition = Arrays.binarySearch(persistencePerspective
                            .getAdditionalForeignKeys(), new ForeignKey(propertyName, null, null),
                            new Comparator<ForeignKey>() {

                        @Override
                        public int compare(ForeignKey o1, ForeignKey o2) {
                            return o1.getManyToField().compareTo(o2.getManyToField());
                        }
                    });
                    ForeignKey foreignKey = null;
                    if (additionalForeignKeyIndexPosition >= 0) {
                        foreignKey = persistencePerspective.getAdditionalForeignKeys()[additionalForeignKeyIndexPosition];
                    }
                    //in the case of a to-one lookup, an explicit ForeignKey is not passed in. The system should then default
                    //to just using a ForeignKeyRestrictionType.ID_EQ
                    if (metadata.getForeignKeyCollection()) {
                        if (foreignKey != null &&
                                ForeignKeyRestrictionType.COLLECTION_SIZE_EQ.toString().equals(foreignKey.getRestrictionType().toString())) {
                            ctoConverter.addCollectionSizeEqMapping(ceilingEntityFullyQualifiedClassname, propertyName, AssociationPath.ROOT, propertyName);
                        } else {
                            AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                            ctoConverter.addLongMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                        }
                    } else if (cto.get(propertyName).getFilterValues()[0] == null || "null".equals(cto.get(propertyName).getFilterValues()[0])) {
                        ctoConverter.addNullMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, propertyName);
                    } else if (metadata.getSecondaryType() == SupportedFieldType.STRING) {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addStringEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    } else {
                        AssociationPath foreignCategory = new AssociationPath(new AssociationPathElement(propertyName));
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, foreignCategory, metadata.getForeignKeyProperty());
                    }
                } else {
                    ctoConverter.addEmptyMapping(ceilingEntityFullyQualifiedClassname, propertyName);
                }
                break;
            case ID:
                switch (metadata.getSecondaryType()) {
                    case INTEGER:
                        ctoConverter.addLongEQMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                        break;
                    case STRING:
                        ctoConverter.addStringLikeMapping(ceilingEntityFullyQualifiedClassname, propertyName, associationPath, convertedProperty);
                        break;
                }
                break;
        }
    }

    @Override
    public void filterProperties(Entity entity, Map<String, FieldMetadata> mergedProperties) {
        //do nothing
    }
}
