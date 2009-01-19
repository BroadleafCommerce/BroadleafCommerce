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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ch.elca.el4j.services.xmlmergemod.Action;
import ch.elca.el4j.services.xmlmergemod.Utility;

/**
 * Copies the patch element into the output by inserting it after already
 * existing elements of the same name. Usually applied with the
 * {@link ch.elca.el4j.services.xmlmergemod.matcher.xmlmerge.matcher.SkipMatcher}.
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
public class InsertAction implements Action {

    /**
     * {@inheritDoc}
     */
    public void perform(Element originalElement, Element patchElement,
        Element outputParentElement) {

        if (patchElement == null && originalElement != null) {
        	Document parentDoc = outputParentElement.getOwnerDocument();
        	Node temp = parentDoc.importNode(originalElement.cloneNode(true), true);
            outputParentElement.appendChild(temp);

        } else {
            List outputContent = Utility.buildListFromNodeList(outputParentElement.getChildNodes());

            Iterator it = outputContent.iterator();

            int lastIndex = outputContent.size();

            while (it.hasNext()) {
                Content content = (Content) it.next();

                if (content instanceof Element) {
                    Element element = (Element) content;

                    if (element.getNodeName().equals(
                        patchElement.getNodeName())) {
                        lastIndex = outputContent.lastIndexOf(element);
                    }
                }
            }

            List toAdd = new ArrayList();
            toAdd.add(patchElement);
            outputContent.addAll(Math.min(lastIndex + 1, outputContent.size()),
                toAdd);
        }
    }

}
