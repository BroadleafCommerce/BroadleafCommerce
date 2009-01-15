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
package ch.elca.el4j.services.xmlmergemod;

import org.w3c.dom.Element;

/**
 * Creates operation (action, mapper and matcher) instances corresponding to a
 * pair of elements from the original and patch DOMs.
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
public interface OperationFactory {

    /**
     * Creates operation (action, mapper and matcher) instances corresponding to
     * a pair of elements from the original and patch DOMs.
     * 
     * @param originalElement
     *            Original element
     * @param modifiedElement
     *            Modified element
     * @return The operation (action, mapper or matcher) for the given element
     *         pair
     * @throws AbstractXmlMergeException
     *             If an error occurs during operation creation
     */
    public Operation getOperation(Element originalElement,
        Element modifiedElement) throws AbstractXmlMergeException;

}
