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
 * Thrown when something is wrong in the matching process.
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
public class MatchException extends AbstractXmlMergeException {

    /**
     * Element which caused the exception.
     */
    Element m_element;
    
    /**
     * Constructor with message.
     * 
     * @param element Element which caused the exception
     * @param message Exception message
     */
    public MatchException(Element element, String message) {
        super(message);
        this.m_element = element;
    }

    /**
     * Constructor with cause.
     * 
     * @param element Element which caused the exception
     * @param cause Exception cause
     */
    public MatchException(Element element, Throwable cause) {
        super(cause);
        this.m_element = element;
    }

    /**
     * @return Returns the element.
     */
    public Element getElement() {
        return m_element;
    }

    /**
     * @param element Is the element to set.
     */
    public void setElement(Element element) {
        m_element = element;
    }
 
}
