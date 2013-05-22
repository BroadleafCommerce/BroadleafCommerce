package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
import org.hibernate.ejb.criteria.path.PluralAttributePath;
import org.hibernate.ejb.criteria.path.SingularAttributePath;
import org.hibernate.internal.SessionFactoryImpl;

import javax.persistence.Embeddable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class FieldPathBuilder {

    public FieldPath getFieldPath(From root, String fullPropertyName) {
        String[] pieces = fullPropertyName.split("\\.");
        List<String> associationPath = new ArrayList<String>();
        List<String> basicProperties = new ArrayList<String>();
        int j = 0;
        for (String piece : pieces) {
            checkPiece: {
                if (j == 0) {
                    Path path = root.get(piece);
                    if (path instanceof PluralAttributePath) {
                        associationPath.add(piece);
                        break checkPiece;
                    }
                }
                basicProperties.add(piece);
            }
            j++;
        }
        FieldPath fieldPath = new FieldPath()
            .withAssociationPath(associationPath)
            .withTargetPropertyPieces(basicProperties);

        return fieldPath;
    }

    public Path getPath(From root, String fullPropertyName, CriteriaBuilder builder) {
        return getPath(root, getFieldPath(root, fullPropertyName), builder);
    }

    public Path getPath(From root, FieldPath fieldPath, CriteriaBuilder builder) {
        FieldPath myFieldPath = fieldPath;
        if (!StringUtils.isEmpty(fieldPath.getTargetProperty())) {
            myFieldPath = getFieldPath(root, fieldPath.getTargetProperty());
        }
        From myRoot = root;
        for (String pathElement : myFieldPath.getAssociationPath()) {
            myRoot = myRoot.join(pathElement);
        }
        Path path = myRoot;
        for (String piece : myFieldPath.getTargetPropertyPieces()) {
            if (path.getJavaType().isAnnotationPresent(Embeddable.class)) {
                String original = ((SingularAttributePath) path).getAttribute().getDeclaringType().getJavaType().getName() + "." + ((SingularAttributePath) path).getAttribute().getName() + "." + piece;
                String copy = path.getJavaType().getName() + "." + piece;
                copyCollectionPersister(original, copy, ((CriteriaBuilderImpl) builder).getEntityManagerFactory().getSessionFactory());
            }
            path = path.get(piece);
            if (path.getParentPath() != null && path.getParentPath().getJavaType().isAnnotationPresent(Embeddable.class) && path instanceof PluralAttributePath) {
                //TODO this code should work, but there still appear to be bugs in Hibernate's JPA criteria handling for lists
                //inside Embeddables
                Class<?> myClass = ((PluralAttributePath) path).getAttribute().getClass().getInterfaces()[0];
                //we don't know which version of "join" to call, so we'll let reflection figure it out
                try {
                    From embeddedJoin = myRoot.join(((SingularAttributePath) path.getParentPath()).getAttribute());
                    Method join = embeddedJoin.getClass().getMethod("join", myClass);
                    path = (Path) join.invoke(embeddedJoin, ((PluralAttributePath) path).getAttribute());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return path;
    }

    /**
     * This is a workaround for HHH-6562 (https://hibernate.atlassian.net/browse/HHH-6562)
     */
    @SuppressWarnings("unchecked")
    private void copyCollectionPersister(String originalKey, String copyKey,
            SessionFactoryImpl sessionFactory) {
        try {
            Field collectionPersistersField = SessionFactoryImpl.class
                    .getDeclaredField("collectionPersisters");
            collectionPersistersField.setAccessible(true);
            Map collectionPersisters = (Map) collectionPersistersField.get(sessionFactory);
            if (collectionPersisters.containsKey(originalKey)) {
                Object collectionPersister = collectionPersisters.get(originalKey);
                collectionPersisters.put(copyKey, collectionPersister);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
