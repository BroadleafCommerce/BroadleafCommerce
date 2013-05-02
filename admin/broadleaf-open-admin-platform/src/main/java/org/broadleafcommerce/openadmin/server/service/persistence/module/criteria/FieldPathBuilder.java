package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.apache.commons.lang.StringUtils;
import org.hibernate.ejb.criteria.path.PluralAttributePath;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.List;

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

    public Path getPath(From root, String fullPropertyName) {
        return getPath(root, getFieldPath(root, fullPropertyName));
    }

    public Path getPath(From root, FieldPath fieldPath) {
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
            path = path.get(piece);
        }

        return path;
    }
}
