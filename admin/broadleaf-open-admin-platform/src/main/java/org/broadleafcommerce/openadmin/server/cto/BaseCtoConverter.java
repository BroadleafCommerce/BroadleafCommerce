package org.broadleafcommerce.openadmin.server.cto;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.cto.server.CriteriaTransferObjectConverter;

/**
 * @author Jeff Fischer
 */
public interface BaseCtoConverter extends CriteriaTransferObjectConverter {

    void addStringLikeMapping(String mappingGroupName, String propertyId,
                              AssociationPath associationPath, String targetPropertyName);

    void addDecimalMapping(String mappingGroupName, String propertyId,
                           AssociationPath associationPath, String targetPropertyName);

    void addLongMapping(String mappingGroupName, String propertyId,
                        AssociationPath associationPath, String targetPropertyName);

    void addLongEQMapping(String mappingGroupName, String propertyId,
                          AssociationPath associationPath, String targetPropertyName);

    void addStringEQMapping(String mappingGroupName, String propertyId,
                            AssociationPath associationPath, String targetPropertyName);

    void addNullMapping(String mappingGroupName, String propertyId,
                        AssociationPath associationPath, String targetPropertyName);

    void addEmptyMapping(String mappingGroupName, String propertyId);

    void addBooleanMapping(String mappingGroupName, String propertyId,
                           AssociationPath associationPath, String targetPropertyName);

    void addCharacterMapping(String mappingGroupName, String propertyId,
                             AssociationPath associationPath, String targetPropertyName);

    void addDateMapping(String mappingGroupName, String propertyId,
                        AssociationPath associationPath, String targetPropertyName);

    void addCollectionSizeEqMapping(String mappingGroupName, String propertyId,
                                    AssociationPath associationPath, String targetPropertyName);

}
