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

import org.w3c.dom.Document;

/**
 * Thrown when something is wrong with a source or output document.
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
public class DocumentException extends AbstractXmlMergeException {

    /**
     * A document instance.
     */
    Document m_document;
    
    /**
     * Constructor with message.
     * 
     * @param document Document which caused the exception
     * @param message Exception message
     */
    public DocumentException(Document document, String message) {
        super(message);
        m_document = document;
    }

    /**
     * Constructor with cause.
     * 
     * @param document Document which caused the exception
     * @param cause Exception cause
     */
    public DocumentException(Document document, Throwable cause) {
        super(makeMessage(document), cause);
        m_document = document;
    }

    /**
     * Announces that there is a problem with the given document.
     * @param document A given document
     * @return String announcing that there is a problem with the given document
     */
    private static String makeMessage(Document document) {
        return "Problem with document " + document;
    }

    /**
     * @return Returns the document.
     */
    public Document getDocument() {
        return m_document;
    }

    /**
     * @param document Is the document to set.
     */
    public void setDocument(Document document) {
        m_document = document;
    }
    
}
