/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.xmlmergemod.factory;

import java.lang.reflect.Field;

import ch.elca.el4j.services.xmlmergemod.ConfigurationException;
import ch.elca.el4j.services.xmlmergemod.Operation;

/**
 * Creates an operation instance given a short name (alias) or a class name.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class OperationResolver {

    /**
     * Class representing an operation.
     */
    Class m_constantClass;
    
    /**
     * Creates an OperationResolver given the class representing an operation.
     * 
     * @param class1
     *            The class of an operation
     */
    public OperationResolver(Class class1) {
        m_constantClass = class1;
    }
    
    /**
     * Resolves an alias or an operation class name to an operation.
     * 
     * @param aliasOrClassName
     *            an alias or class name representing an operation
     * @return The resolved operation
     * @throws ConfigurationException
     *             If an error occurred during the resolving process
     */
    public Operation resolve(String aliasOrClassName)
        throws ConfigurationException {
        Field field = null;
        try {
            field = m_constantClass.getField(aliasOrClassName.toUpperCase());
        } catch (NoSuchFieldException e) {

            try {
                return (Operation) Class.forName(aliasOrClassName)
                    .newInstance();
            } catch (InstantiationException e1) {
                throw new ConfigurationException(
                    "Cannot instanciate object from class " + aliasOrClassName);
            } catch (IllegalAccessException e1) {
                throw new ConfigurationException(
                    "Cannot access constructor or class " + aliasOrClassName);
            } catch (ClassNotFoundException e1) {
                throw new ConfigurationException(
                    "Verb not found or class not found:" + aliasOrClassName);
            } catch (ClassCastException e1) {
                throw new ConfigurationException(
                    "Class does not implement Operation :" + aliasOrClassName);
            }

        }
        try {
            return (Operation) field.get(null);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new ConfigurationException(e);
        }
    }
    
}
