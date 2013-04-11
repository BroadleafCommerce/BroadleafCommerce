package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.hibernate.mapping.Property;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Component("blBasicPropertyProvider")
@Scope("prototype")
public class BasicPropertyProvider extends AbstractPropertyProvider {

    @Override
    public boolean canHandleField(Field field) {
        return true;
    }

    @Override
    public void buildProperty(
            Field field,
            Class<?> targetClass,
            ForeignKey foreignField,
            ForeignKey[] additionalForeignFields,
            MergedPropertyType mergedPropertyType,
            List<Property> componentProperties,
            Map<String, FieldMetadata> fields,
            String idProperty,
            String prefix,
            String propertyName,
            Type type,
            boolean isPropertyForeignKey,
            int additionalForeignKeyIndexPosition,
            Map<String, FieldMetadata> presentationAttributes,
            FieldMetadata presentationAttribute,
            SupportedFieldType explicitType,
            Class<?> returnedClass,
            DynamicEntityDao dynamicEntityDao
    ) {
        if (
                explicitType != null &&
                        explicitType != SupportedFieldType.UNKNOWN &&
                        explicitType != SupportedFieldType.BOOLEAN &&
                        explicitType != SupportedFieldType.INTEGER &&
                        explicitType != SupportedFieldType.DATE &&
                        explicitType != SupportedFieldType.STRING &&
                        explicitType != SupportedFieldType.MONEY &&
                        explicitType != SupportedFieldType.DECIMAL &&
                        explicitType != SupportedFieldType.FOREIGN_KEY &&
                        explicitType != SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                ) {
            fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                    componentProperties,
                    explicitType, type, targetClass, presentationAttribute, mergedPropertyType, dynamicEntityDao));
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.BOOLEAN
                        ||
                        returnedClass.equals(Boolean.class) ||
                        returnedClass.equals(Character.class)
                ) {
            fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                    componentProperties,
                    SupportedFieldType.BOOLEAN, type, targetClass, presentationAttribute, mergedPropertyType,
                    dynamicEntityDao));
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.INTEGER
                        ||
                        returnedClass.equals(Byte.class) ||
                        returnedClass.equals(Short.class) ||
                        returnedClass.equals(Integer.class) ||
                        returnedClass.equals(Long.class)
                ) {
            if (propertyName.equals(idProperty)) {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties,
                        SupportedFieldType.ID, SupportedFieldType.INTEGER, type, targetClass, presentationAttribute,
                        mergedPropertyType, dynamicEntityDao));
            } else {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties,
                        SupportedFieldType.INTEGER, type, targetClass, presentationAttribute, mergedPropertyType,
                        dynamicEntityDao));
            }
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.DATE
                        ||
                        returnedClass.equals(Calendar.class) ||
                        returnedClass.equals(Date.class) ||
                        returnedClass.equals(Timestamp.class)
                ) {
            fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                    componentProperties,
                    SupportedFieldType.DATE, type, targetClass, presentationAttribute, mergedPropertyType,
                    dynamicEntityDao));
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.STRING
                        ||
                        returnedClass.equals(String.class)
                ) {
            if (propertyName.equals(idProperty)) {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties,
                        SupportedFieldType.ID, SupportedFieldType.STRING, type, targetClass, presentationAttribute,
                        mergedPropertyType, dynamicEntityDao));
            } else {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties,
                        SupportedFieldType.STRING, type, targetClass, presentationAttribute, mergedPropertyType,
                        dynamicEntityDao));
            }
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.MONEY
                        ||
                        returnedClass.equals(Money.class)
                ) {
            fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                    componentProperties,
                    SupportedFieldType.MONEY, type, targetClass, presentationAttribute, mergedPropertyType,
                    dynamicEntityDao));
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.DECIMAL
                        ||
                        returnedClass.equals(Double.class) ||
                        returnedClass.equals(BigDecimal.class)
                ) {
            fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                    componentProperties,
                    SupportedFieldType.DECIMAL, type, targetClass, presentationAttribute, mergedPropertyType,
                    dynamicEntityDao));
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.FOREIGN_KEY
                        ||
                        foreignField != null &&
                                isPropertyForeignKey
                ) {
            ClassMetadata foreignMetadata;
            String foreignKeyClass;
            String lookupDisplayProperty;
            if (foreignField == null) {
                Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(type.getReturnedClass());
                foreignMetadata = dynamicEntityDao.getSessionFactory().getClassMetadata(entities[entities.length - 1]);
                foreignKeyClass = entities[entities.length - 1].getName();
                lookupDisplayProperty = ((BasicFieldMetadata) presentationAttribute).getLookupDisplayProperty();
            } else {
                try {
                    foreignMetadata = dynamicEntityDao.getSessionFactory().getClassMetadata(Class.forName(foreignField
                            .getForeignKeyClass()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                foreignKeyClass = foreignField.getForeignKeyClass();
                lookupDisplayProperty = foreignField.getDisplayValueProperty();
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
            if (foreignResponseType.equals(String.class)) {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties,
                        SupportedFieldType.FOREIGN_KEY, SupportedFieldType.STRING, type, targetClass,
                        presentationAttribute, mergedPropertyType, dynamicEntityDao));
            } else {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties,
                        SupportedFieldType.FOREIGN_KEY, SupportedFieldType.INTEGER, type, targetClass,
                        presentationAttribute, mergedPropertyType, dynamicEntityDao));
            }
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyProperty(foreignMetadata
                    .getIdentifierPropertyName());
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyClass(foreignKeyClass);
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyDisplayValueProperty(lookupDisplayProperty);
        } else if (
                explicitType != null &&
                        explicitType == SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                        ||
                        additionalForeignFields != null &&
                                additionalForeignKeyIndexPosition >= 0
                ) {
            if (!type.isEntityType()) {
                throw new IllegalArgumentException("Only ManyToOne and OneToOne fields can be marked as a " +
                        "SupportedFieldType of ADDITIONAL_FOREIGN_KEY");
            }
            ClassMetadata foreignMetadata;
            String foreignKeyClass;
            String lookupDisplayProperty;
            if (additionalForeignKeyIndexPosition < 0) {
                Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(type.getReturnedClass());
                foreignMetadata = dynamicEntityDao.getSessionFactory().getClassMetadata(entities[entities.length - 1]);
                foreignKeyClass = entities[entities.length - 1].getName();
                lookupDisplayProperty = ((BasicFieldMetadata) presentationAttribute).getLookupDisplayProperty();
            } else {
                try {
                    foreignMetadata = dynamicEntityDao.getSessionFactory().getClassMetadata(Class.forName
                            (additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                foreignKeyClass = additionalForeignFields[additionalForeignKeyIndexPosition].getForeignKeyClass();
                lookupDisplayProperty = additionalForeignFields[additionalForeignKeyIndexPosition]
                        .getDisplayValueProperty();
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
            if (foreignResponseType.equals(String.class)) {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties, SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.STRING,
                        type, targetClass, presentationAttribute, mergedPropertyType, dynamicEntityDao));
            } else {
                fields.put(propertyName, dynamicEntityDao.getMetadata().getFieldMetadata(prefix, propertyName,
                        componentProperties, SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.INTEGER,
                        type, targetClass, presentationAttribute, mergedPropertyType, dynamicEntityDao));
            }
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyClass(foreignKeyClass);
            ((BasicFieldMetadata) fields.get(propertyName)).setForeignKeyDisplayValueProperty(lookupDisplayProperty);
        }
        //return type not supported - just skip this property
    }

}
