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

import org.w3c.dom.Element;

import ch.elca.el4j.services.xmlmergemod.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmergemod.Operation;
import ch.elca.el4j.services.xmlmergemod.OperationFactory;

/**
 * An operation factory delegating to other operation factories according to the
 * existence of the original and patch element.
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
public class DiffOperationFactory implements OperationFactory {

    /**
     * OperationFactory this factory delegates to if only the original element
     * exists.
     */
    OperationFactory m_onlyInOriginalOperationFactory;

    /**
     * OperationFactory this factory delegates to if only the patch element
     * exists.
     */
    OperationFactory m_onlyInPatchOperationFactory;

    /**
     * OperationFactory this factory delegates to if the original and patch
     * elements exist.
     */
    OperationFactory m_inBothOperationFactory;

    /**
     * Sets the operation factory this factory delegates to if the original and
     * patch elements exist.
     * 
     * @param inBothOperationFactory
     *            the operation factory this factory delegates to if the
     *            original and patch elements exist.
     */
    public void setInBothOperationFactory(
        OperationFactory inBothOperationFactory) {
        this.m_inBothOperationFactory = inBothOperationFactory;
    }

    /**
     * Sets the operation factory this factory delegates to if only the original
     * element exists.
     * 
     * @param onlyInOriginalOperationFactory
     *            factory this factory delegates to if only the original element
     *            exists
     */
    public void setOnlyInOriginalOperationFactory(
        OperationFactory onlyInOriginalOperationFactory) {
        this.m_onlyInOriginalOperationFactory = onlyInOriginalOperationFactory;
    }

    /**
     * Sets the operation factory this factory delegates to if only the patch
     * element exists.
     * 
     * @param onlyInPatchOperationFactory
     *            factory this factory delegates to if only the patch element
     *            exists
     */
    public void setOnlyInPatchOperationFactory(
        OperationFactory onlyInPatchOperationFactory) {
        this.m_onlyInPatchOperationFactory = onlyInPatchOperationFactory;
    }

    /**
     * {@inheritDoc}
     */
    public Operation getOperation(Element originalElement, Element patchElement)
        throws AbstractXmlMergeException {

        if (originalElement != null && patchElement == null) {
            return m_onlyInOriginalOperationFactory.getOperation(
                originalElement, patchElement);
        }

        if (originalElement == null && patchElement != null) {
            return m_onlyInPatchOperationFactory.getOperation(originalElement,
                patchElement);
        }

        if (originalElement != null && patchElement != null) {
            return m_inBothOperationFactory.getOperation(originalElement,
                patchElement);
        }

        throw new IllegalArgumentException();
    }

}
