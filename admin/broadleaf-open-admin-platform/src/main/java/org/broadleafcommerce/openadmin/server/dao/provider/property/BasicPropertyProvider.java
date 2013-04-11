package org.broadleafcommerce.openadmin.server.dao.provider.property;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.server.dao.provider.property.request.PropertyRequest;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Jeff Fischer
 */
@Component("blBasicPropertyProvider")
@Scope("prototype")
public class BasicPropertyProvider extends PropertyProviderAdapter {

    @Override
    public boolean canHandleField(Field field) {
        return true;
    }

    @Override
    public void buildProperty(PropertyRequest propertyRequest) {
        if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() != SupportedFieldType.UNKNOWN &&
                        propertyRequest.getExplicitType() != SupportedFieldType.BOOLEAN &&
                        propertyRequest.getExplicitType() != SupportedFieldType.INTEGER &&
                        propertyRequest.getExplicitType() != SupportedFieldType.DATE &&
                        propertyRequest.getExplicitType() != SupportedFieldType.STRING &&
                        propertyRequest.getExplicitType() != SupportedFieldType.MONEY &&
                        propertyRequest.getExplicitType() != SupportedFieldType.DECIMAL &&
                        propertyRequest.getExplicitType() != SupportedFieldType.FOREIGN_KEY &&
                        propertyRequest.getExplicitType() != SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                ) {
            propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getDynamicEntityDao()
                    .getMetadata().getFieldMetadata(propertyRequest.getPrefix(),
                            propertyRequest.getRequestedPropertyName(),
                            propertyRequest.getComponentProperties(),
                            propertyRequest.getExplicitType(), propertyRequest.getType(),
                            propertyRequest.getTargetClass(),
                            propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(), propertyRequest.getDynamicEntityDao()));
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.BOOLEAN
                        ||
                        propertyRequest.getReturnedClass().equals(Boolean.class) ||
                        propertyRequest.getReturnedClass().equals(Character.class)
                ) {
            propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getDynamicEntityDao()
                    .getMetadata().getFieldMetadata(propertyRequest.getPrefix(),
                            propertyRequest.getRequestedPropertyName(),
                            propertyRequest.getComponentProperties(),
                            SupportedFieldType.BOOLEAN, propertyRequest.getType(), propertyRequest.getTargetClass(),
                            propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                            propertyRequest.getDynamicEntityDao()));
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.INTEGER
                        ||
                        propertyRequest.getReturnedClass().equals(Byte.class) ||
                        propertyRequest.getReturnedClass().equals(Short.class) ||
                        propertyRequest.getReturnedClass().equals(Integer.class) ||
                        propertyRequest.getReturnedClass().equals(Long.class)
                ) {
            if (propertyRequest.getRequestedPropertyName().equals(propertyRequest.getIdProperty())) {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest
                                .getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(),
                                SupportedFieldType.ID, SupportedFieldType.INTEGER, propertyRequest.getType(),
                                propertyRequest.getTargetClass(), propertyRequest.getPresentationAttribute(),
                                propertyRequest.getMergedPropertyType(), propertyRequest.getDynamicEntityDao()));
            } else {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest
                                .getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(),
                                SupportedFieldType.INTEGER, propertyRequest.getType(), propertyRequest.getTargetClass(), propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                                propertyRequest.getDynamicEntityDao()));
            }
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.DATE
                        ||
                        propertyRequest.getReturnedClass().equals(Calendar.class) ||
                        propertyRequest.getReturnedClass().equals(Date.class) ||
                        propertyRequest.getReturnedClass().equals(Timestamp.class)
                ) {
            propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getDynamicEntityDao()
                    .getMetadata().getFieldMetadata(propertyRequest.getPrefix(),
                            propertyRequest.getRequestedPropertyName(),
                            propertyRequest.getComponentProperties(),
                            SupportedFieldType.DATE, propertyRequest.getType(), propertyRequest.getTargetClass(),
                            propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                            propertyRequest.getDynamicEntityDao()));
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.STRING
                        ||
                        propertyRequest.getReturnedClass().equals(String.class)
                ) {
            if (propertyRequest.getRequestedPropertyName().equals(propertyRequest.getIdProperty())) {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest
                                .getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(),
                                SupportedFieldType.ID, SupportedFieldType.STRING, propertyRequest.getType(),
                                propertyRequest.getTargetClass(), propertyRequest.getPresentationAttribute(),
                                propertyRequest.getMergedPropertyType(), propertyRequest.getDynamicEntityDao()));
            } else {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest
                                .getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(),
                                SupportedFieldType.STRING, propertyRequest.getType(), propertyRequest.getTargetClass(), propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                                propertyRequest.getDynamicEntityDao()));
            }
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.MONEY
                        ||
                        propertyRequest.getReturnedClass().equals(Money.class)
                ) {
            propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest.getPrefix(), propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getComponentProperties(),
                    SupportedFieldType.MONEY, propertyRequest.getType(), propertyRequest.getTargetClass(), propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                    propertyRequest.getDynamicEntityDao()));
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.DECIMAL
                        ||
                        propertyRequest.getReturnedClass().equals(Double.class) ||
                        propertyRequest.getReturnedClass().equals(BigDecimal.class)
                ) {
            propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest.getPrefix(), propertyRequest.getRequestedPropertyName(),
                    propertyRequest.getComponentProperties(),
                    SupportedFieldType.DECIMAL, propertyRequest.getType(), propertyRequest.getTargetClass(), propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                    propertyRequest.getDynamicEntityDao()));
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.FOREIGN_KEY
                        ||
                        propertyRequest.getForeignField() != null &&
                                propertyRequest.isPropertyForeignKey()
                ) {
            ClassMetadata foreignMetadata;
            String foreignKeyClass;
            String lookupDisplayProperty;
            if (propertyRequest.getForeignField() == null) {
                Class<?>[] entities = propertyRequest.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling(propertyRequest.getType().getReturnedClass());
                foreignMetadata = propertyRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(entities
                        [entities.length - 1]);
                foreignKeyClass = entities[entities.length - 1].getName();
                lookupDisplayProperty = ((BasicFieldMetadata) propertyRequest.getPresentationAttribute()).getLookupDisplayProperty();
            } else {
                try {
                    foreignMetadata = propertyRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(Class.forName(propertyRequest.getForeignField()
                            .getForeignKeyClass()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                foreignKeyClass = propertyRequest.getForeignField().getForeignKeyClass();
                lookupDisplayProperty = propertyRequest.getForeignField().getDisplayValueProperty();
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
            if (foreignResponseType.equals(String.class)) {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest
                                .getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(),
                                SupportedFieldType.FOREIGN_KEY, SupportedFieldType.STRING, propertyRequest.getType(),
                                propertyRequest.getTargetClass(),
                                propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                                propertyRequest.getDynamicEntityDao()));
            } else {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(), propertyRequest
                        .getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest.getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(),
                                SupportedFieldType.FOREIGN_KEY, SupportedFieldType.INTEGER, propertyRequest.getType(), propertyRequest.getTargetClass(),

                                propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                                propertyRequest.getDynamicEntityDao()));
            }
            ((BasicFieldMetadata) propertyRequest.getRequestedProperties().get(propertyRequest.getRequestedPropertyName())).setForeignKeyProperty(foreignMetadata
                    .getIdentifierPropertyName());
            ((BasicFieldMetadata) propertyRequest.getRequestedProperties().get(propertyRequest.getRequestedPropertyName()))
                    .setForeignKeyClass(foreignKeyClass);
            ((BasicFieldMetadata) propertyRequest.getRequestedProperties().get(propertyRequest.getRequestedPropertyName())).setForeignKeyDisplayValueProperty(lookupDisplayProperty);
        } else if (
                propertyRequest.getExplicitType() != null &&
                        propertyRequest.getExplicitType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                        ||
                        propertyRequest.getAdditionalForeignFields() != null &&
                                propertyRequest.getAdditionalForeignKeyIndexPosition() >= 0
                ) {
            if (!propertyRequest.getType().isEntityType()) {
                throw new IllegalArgumentException("Only ManyToOne and OneToOne fields can be marked as a " +
                        "SupportedFieldType of ADDITIONAL_FOREIGN_KEY");
            }
            ClassMetadata foreignMetadata;
            String foreignKeyClass;
            String lookupDisplayProperty;
            if (propertyRequest.getAdditionalForeignKeyIndexPosition() < 0) {
                Class<?>[] entities = propertyRequest.getDynamicEntityDao().getAllPolymorphicEntitiesFromCeiling
                        (propertyRequest.getType().getReturnedClass());
                foreignMetadata = propertyRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(entities[entities.length - 1]);
                foreignKeyClass = entities[entities.length - 1].getName();
                lookupDisplayProperty = ((BasicFieldMetadata) propertyRequest.getPresentationAttribute()).getLookupDisplayProperty();
            } else {
                try {
                    foreignMetadata = propertyRequest.getDynamicEntityDao().getSessionFactory().getClassMetadata(Class.forName
                            (propertyRequest.getAdditionalForeignFields()[propertyRequest.getAdditionalForeignKeyIndexPosition()].getForeignKeyClass()));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                foreignKeyClass = propertyRequest.getAdditionalForeignFields()[propertyRequest
                        .getAdditionalForeignKeyIndexPosition()].getForeignKeyClass();
                lookupDisplayProperty = propertyRequest.getAdditionalForeignFields()[propertyRequest
                        .getAdditionalForeignKeyIndexPosition()]
                        .getDisplayValueProperty();
            }
            Class<?> foreignResponseType = foreignMetadata.getIdentifierType().getReturnedClass();
            if (foreignResponseType.equals(String.class)) {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest
                                .getPrefix(), propertyRequest.getRequestedPropertyName(),
                                propertyRequest.getComponentProperties(), SupportedFieldType.ADDITIONAL_FOREIGN_KEY,
                                SupportedFieldType.STRING,
                                propertyRequest.getType(), propertyRequest.getTargetClass(),
                                propertyRequest.getPresentationAttribute(), propertyRequest.getMergedPropertyType(),
                                propertyRequest.getDynamicEntityDao()));
            } else {
                propertyRequest.getRequestedProperties().put(propertyRequest.getRequestedPropertyName(), propertyRequest.getDynamicEntityDao().getMetadata().getFieldMetadata(propertyRequest.getPrefix(), propertyRequest.getRequestedPropertyName(),
                        propertyRequest.getComponentProperties(), SupportedFieldType.ADDITIONAL_FOREIGN_KEY, SupportedFieldType.INTEGER,
                        propertyRequest.getType(), propertyRequest.getTargetClass(), propertyRequest
                        .getPresentationAttribute(), propertyRequest.getMergedPropertyType(), propertyRequest.getDynamicEntityDao()));
            }
            ((BasicFieldMetadata) propertyRequest.getRequestedProperties().get(propertyRequest.getRequestedPropertyName())).setForeignKeyProperty(foreignMetadata.getIdentifierPropertyName());
            ((BasicFieldMetadata) propertyRequest.getRequestedProperties().get(propertyRequest.getRequestedPropertyName())).setForeignKeyClass(foreignKeyClass);
            ((BasicFieldMetadata) propertyRequest.getRequestedProperties().get(propertyRequest.getRequestedPropertyName())).setForeignKeyDisplayValueProperty(lookupDisplayProperty);
        }
        //return type not supported - just skip this property
    }

}
