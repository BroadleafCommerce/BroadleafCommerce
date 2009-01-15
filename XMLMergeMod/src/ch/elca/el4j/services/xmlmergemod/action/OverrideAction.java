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
package ch.elca.el4j.services.xmlmergemod.action;

import org.w3c.dom.Element;

import ch.elca.el4j.services.xmlmergemod.Action;

/**
 * Copies the patch element if it exist in the original, keep the original if no
 * corresponding patch element exists.
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
public class OverrideAction implements Action {

    /**
     * {@inheritDoc}
     */
    public void perform(Element originalElement, Element patchElement,
        Element outputParentElement) {
        if (originalElement != null && patchElement != null) {
            outputParentElement.appendChild((Element) patchElement.cloneNode(true));
        } else if (originalElement != null) {
            outputParentElement.appendChild((Element) originalElement.cloneNode(true));
        }
    }

}
