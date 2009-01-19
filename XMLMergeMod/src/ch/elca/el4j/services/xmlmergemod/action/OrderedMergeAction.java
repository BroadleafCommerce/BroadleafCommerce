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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import ch.elca.el4j.services.xmlmergemod.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmergemod.Action;
import ch.elca.el4j.services.xmlmergemod.DocumentException;
import ch.elca.el4j.services.xmlmergemod.Mapper;
import ch.elca.el4j.services.xmlmergemod.Matcher;
import ch.elca.el4j.services.xmlmergemod.MergeAction;
import ch.elca.el4j.services.xmlmergemod.Utility;


/**
 * Merge implementation traversing parallelly both element contents. Works when
 * contents are in the same order in both elements.
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
public class OrderedMergeAction extends AbstractMergeAction {

    /**
     * Private logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(OrderedMergeAction.class);
    
    /**
     * {@inheritDoc}
     */
    public void perform(Element originalElement, Element patchElement,
        Element outputParentElement) throws AbstractXmlMergeException {

        s_logger.debug("Merging: " + originalElement + "(List 1) and "
            + patchElement + "(List 2)");

        Mapper mapper = (Mapper) m_mapperFactory.getOperation(originalElement,
            patchElement);
        Document parentDoc = outputParentElement.getOwnerDocument();
        if (originalElement == null) {
        	Element temp = (Element) parentDoc.importNode(mapper.map(patchElement), true);
            outputParentElement.appendChild(temp);
        } else if (patchElement == null) {
        	Element temp = (Element) parentDoc.importNode(originalElement.cloneNode(true), true);
            outputParentElement.appendChild(temp);
        } else {
        	Element workingElement = (Element) originalElement.cloneNode(false);
            addAttributes(workingElement, originalElement);

            s_logger.debug("Adding " + workingElement);
            workingElement = (Element) parentDoc.importNode(workingElement, false);
            outputParentElement.appendChild(workingElement);

            doIt(workingElement, originalElement, patchElement);
        }

    }

    /**
     * Performs the actual merge between two source elements.
     * 
     * @param parentOut
     *            The merged element
     * @param parentIn1
     *            The first source element
     * @param parentIn2
     *            The second source element
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */
    private void doIt(Element parentOut, Element parentIn1, Element parentIn2)
        throws AbstractXmlMergeException {

        addAttributes(parentOut, parentIn2);
        
        List preList1 = Utility.buildListFromNodeList(parentIn1.getChildNodes());
        List preList2 = Utility.buildListFromNodeList(parentIn2.getChildNodes());
        Node[] list1 = (Node[]) preList1.toArray(new Node[preList1.size()]);
        Node[] list2 = (Node[]) preList2.toArray(new Node[preList2.size()]);

        int offsetTreated1 = 0;
        int offsetTreated2 = 0;

        Document parentDoc = parentOut.getOwnerDocument();
        for (int i = 0; i < list1.length; i++) {

            s_logger.debug("List 1: " + list1[i]);

            if (list1[i] instanceof Comment || list1[i] instanceof Text) {
            	Node temp = parentDoc.importNode(list1[i], true);
                parentOut.appendChild(temp);
                offsetTreated1++;
            } else if (!(list1[i] instanceof Element)) {
                throw new DocumentException(list1[i].getOwnerDocument(),
                    "Contents of type " + list1[i].getClass().getName()
                        + " not supported");
            } else {
                Element e1 = (Element) list1[i];

                // does e1 exist on list2 and has not yet been treated
                int posInList2 = -1;
                for (int j = offsetTreated2; j < list2.length; j++) {

                    s_logger.debug("List 2: " + list2[j]);

                    if (list2[j] instanceof Element) {

                        if (((Matcher) m_matcherFactory.getOperation(e1,
                                (Element) list2[j]))
                            .matches(e1, (Element) list2[j])) {
                            s_logger.debug("Match found: " + e1 + " and "
                                + list2[j]);
                            posInList2 = j;
                            break;
                        }
                    } else if (list2[j] instanceof Comment
                        || list2[j] instanceof Text) {
                        // skip
                    } else {
                        throw new DocumentException(list2[j].getOwnerDocument(),
                            "Contents of type " + list2[j].getClass().getName()
                                + " not supported");
                    }
                }

                // element found in second list, but there is some elements to
                // be
                // treated before in second list
                while (posInList2 != -1 && offsetTreated2 < posInList2) {
                    Node contentToAdd;
                    if (list2[offsetTreated2] instanceof Element) {
                        applyAction(parentOut, null,
                            (Element) list2[offsetTreated2]);
                    } else {
                        contentToAdd = parentDoc.importNode(list2[offsetTreated2], true);
                        parentOut.appendChild(contentToAdd);
                    }

                    offsetTreated2++;
                }

                // element found in all lists
                if (posInList2 != -1) {

                    applyAction(parentOut, (Element) list1[offsetTreated1],
                        (Element) list2[offsetTreated2]);

                    offsetTreated1++;
                    offsetTreated2++;
                } else {
                // element not found in second list
                    applyAction(parentOut, (Element) list1[offsetTreated1],
                        null);
                    offsetTreated1++;
                }
            }
        }

        // at end of list1, is there some elements on list2 which must be still
        // treated?
        while (offsetTreated2 < list2.length) {
            Node contentToAdd;
            if (list2[offsetTreated2] instanceof Element) {
                applyAction(parentOut, null, (Element) list2[offsetTreated2]);
            } else {
                contentToAdd = parentDoc.importNode(list2[offsetTreated2], true);
                parentOut.appendChild(contentToAdd);
            }

            offsetTreated2++;
        }

    }

    /**
     * Applies the action which performs the merge between two source elements.
     * 
     * @param workingParent
     *            Output parent element
     * @param originalElement
     *            Original element
     * @param patchElement
     *            Patch element
     * @throws AbstractXmlMergeException
     *             if an error occurred during the merge
     */
    private void applyAction(Element workingParent, Element originalElement,
        Element patchElement) throws AbstractXmlMergeException {
        Action action = (Action) m_actionFactory.getOperation(originalElement,
            patchElement);
        Mapper mapper = (Mapper) m_mapperFactory.getOperation(originalElement,
            patchElement);

        // Propagate the factories to deeper merge actions
        // TODO: find a way to make it cleaner
        if (action instanceof MergeAction) {
            MergeAction mergeAction = (MergeAction) action;
            mergeAction.setActionFactory(m_actionFactory);
            mergeAction.setMapperFactory(m_mapperFactory);
            mergeAction.setMatcherFactory(m_matcherFactory);
        }

        action
            .perform(originalElement, mapper.map(patchElement), workingParent);
    }

    /**
     * Adds attributes from in element to out element.
     * @param out out element
     * @param in in element
     */
    private void addAttributes(Element out, Element in) {
    	/*
    	 * TODO need to review how this works. This piece
    	 * is not complete enough to handle the proper merging
    	 * of applicationContext files when you involve various
    	 * schema locations with all the merge files.
    	 */
        LinkedHashMap allAttributes = new LinkedHashMap();

        NamedNodeMap outAttributes = out.getAttributes();
        NamedNodeMap inAttributes = in.getAttributes();

        for (int i = 0; i < outAttributes.getLength(); i++) {
        	Node attr = outAttributes.item(i);
            allAttributes.put(attr.getNodeName(), attr);
            s_logger.debug("adding attr from out:" + attr);
        }

        for (int i = 0; i < inAttributes.getLength(); i++) {
        	Node attr = inAttributes.item(i);
            allAttributes.put(attr.getNodeName(), attr);
            s_logger.debug("adding attr from in:" + attr);
        }

        Iterator keys = allAttributes.keySet().iterator();
        while (keys.hasNext()) {
        	String key = (String) keys.next();
        	Node attr = (Node) allAttributes.get(key);
        	out.setAttribute(key, attr.getNodeValue());
        }
    }

}
