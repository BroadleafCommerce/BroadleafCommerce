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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ch.elca.el4j.services.xmlmergemod.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmergemod.Operation;
import ch.elca.el4j.services.xmlmergemod.OperationFactory;
import ch.elca.el4j.services.xmlmergemod.ParseException;
import ch.elca.el4j.services.xmlmergemod.Utility;

/**
 * An operation factory that resolves operations given a map { xpath (as
 * String), Operation }. The order in the map is relevant if several XPath
 * matches.
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
public class XPathOperationFactory implements OperationFactory {

    /**
     * A map containing configuration properties.
     */
    Map m_map = new HashMap();

    /**
     * The default operation returned by this factory.
     */
    Operation m_defaultOperation;

    /**
     * Sets the factory's map containing configuration properties.
     * 
     * @param map
     *            A map containing configuration properties.
     */
    public void setOperationMap(Map map) {
        this.m_map = map;
    }

    /**
     * Sets the default operation returned by this factory.
     * @param operation The default operation returned by this factory.
     */
    public void setDefaultOperation(Operation operation) {
        this.m_defaultOperation = operation;
    }

    /**
     * {@inheritDoc}
     */
    public Operation getOperation(Element originalElement, Element patchElement)
        throws AbstractXmlMergeException {
        Iterator it = m_map.keySet().iterator();
        while (it.hasNext()) {
            String xPath = (String) it.next();
            if (matches(originalElement, xPath) || matches(patchElement,
                xPath)) {
                return (Operation) m_map.get(xPath);
            }
        }
        return m_defaultOperation;
    }

    /**
     * Detects whether the given element matches the given XPath string.
     * 
     * @param element
     *            The element which will be checked
     * @param xPathString
     *            The XPath expression the element will be checked against
     * @return True if the given element matches the given XPath string
     * @throws AbstractXmlMergeException
     *             If an error occurred during the matching process
     */
    private boolean matches(Element element, String xPathString)
        throws AbstractXmlMergeException {

        if (element == null) {
            return false;
        }

    	boolean contains;
		try {
			Document doc = element.getOwnerDocument();
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(xPathString);
			NodeList result = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			List list = Utility.buildListFromNodeList(result);
			contains = list.contains(
			    element);
		} catch (XPathExpressionException e) {
			throw new ParseException(e);
		}

        return contains;
    }

}
