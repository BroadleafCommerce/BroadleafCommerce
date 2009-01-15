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
package ch.elca.el4j.services.xmlmergemod.merge;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import ch.elca.el4j.services.xmlmergemod.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmergemod.Mapper;
import ch.elca.el4j.services.xmlmergemod.Matcher;
import ch.elca.el4j.services.xmlmergemod.MergeAction;
import ch.elca.el4j.services.xmlmergemod.ParseException;
import ch.elca.el4j.services.xmlmergemod.Utility;
import ch.elca.el4j.services.xmlmergemod.XmlMerge;
import ch.elca.el4j.services.xmlmergemod.action.OrderedMergeAction;
import ch.elca.el4j.services.xmlmergemod.factory.StaticOperationFactory;
import ch.elca.el4j.services.xmlmergemod.mapper.IdentityMapper;
import ch.elca.el4j.services.xmlmergemod.matcher.TagMatcher;


// Checkstyle: MagicNumber off 

/**
 * Default implementation of XmlMerge. Create all JDOM documents, then perform
 * the merge into a new JDOM document.
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
public class DefaultXmlMerge implements XmlMerge {

    /**
     * Root mapper.
     */
    private Mapper m_rootMapper = new IdentityMapper();

    /**
     * Root merge action.
     */
    private MergeAction m_rootMergeAction = new OrderedMergeAction();

    /**
     * Root matcher.
     */
    private Matcher m_rootMatcher = new TagMatcher();

    /**
     * Creates a new DefaultXmlMerge instance.
     */
    public DefaultXmlMerge() {
        m_rootMergeAction.setActionFactory(new StaticOperationFactory(
            new OrderedMergeAction()));
        m_rootMergeAction.setMapperFactory(new StaticOperationFactory(
            new IdentityMapper()));
        m_rootMergeAction.setMatcherFactory(new StaticOperationFactory(
            new TagMatcher()));
    }

    /**
     * {@inheritDoc}
     */
    public void setRootMapper(Mapper rootMapper) {
        this.m_rootMapper = rootMapper;
    }

    /**
     * {@inheritDoc}
     */
    public void setRootMergeAction(MergeAction rootMergeAction) {
        this.m_rootMergeAction = rootMergeAction;
    }

    /**
     * {@inheritDoc}
     */
    public String merge(String[] sources) throws AbstractXmlMergeException {

        InputStream[] inputStreams = new InputStream[sources.length];

        for (int i = 0; i < sources.length; i++) {
            inputStreams[i] = new ByteArrayInputStream(sources[i].getBytes());
        }

        InputStream merged = merge(inputStreams);

        ByteArrayOutputStream result = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = merged.read(buffer)) != -1) {
                result.write(buffer, 0, len);
            }
        } catch (IOException ioe) {
            // should never happen
            throw new RuntimeException(ioe);
        }

        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    public org.w3c.dom.Document merge(org.w3c.dom.Document[] sources)
        throws AbstractXmlMergeException {
        
        Document result = doMerge(sources);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream merge(InputStream[] sources)
        throws AbstractXmlMergeException {
    	
    	String response;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			DocumentBuilder parser = dbf.newDocumentBuilder();

			Document[] docs = new Document[sources.length];

			for (int i = 0; i < sources.length; i++) {
			    try {
			        docs[i] = parser.parse(sources[i]);
			    } catch (IOException ioe) {
			        throw new ParseException(ioe);
			    }
			}

			Document result = doMerge(docs);
			
			response = Utility.formatItem(result);
		} catch (TransformerConfigurationException e) {
			throw new ParseException(e);
		} catch (IllegalArgumentException e) {
			throw new ParseException(e);
		} catch (ParserConfigurationException e) {
			throw new ParseException(e);
		} catch (SAXException e) {
			throw new ParseException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new ParseException(e);
		} catch (TransformerException e) {
			throw new ParseException(e);
		}
		
		return new ByteArrayInputStream(response.getBytes());
    }

    /**
     * Performs the actual merge.
     * 
     * @param docs
     *            The documents to merge
     * @return The merged result document
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */
    private Document doMerge(Document[] docs) throws AbstractXmlMergeException {
        Document temporary = docs[0];

        for (int i = 1; i < docs.length; i++) {

            if (!m_rootMatcher.matches((Element) temporary.getFirstChild(), (Element) docs[i]
                .getFirstChild())) {
                throw new IllegalArgumentException(
                    "Root elements do not match.");
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(false);
			DocumentBuilder parser;
			try {
				parser = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new ParseException(e);
			}
			Document output = parser.newDocument();
            output.appendChild(output.createElement("root"));
            m_rootMergeAction.perform((Element) temporary.getFirstChild(), (Element) docs[i]
                .getFirstChild(), (Element) output.getFirstChild());
            Element root = (Element) output.getFirstChild().getChildNodes().item(0);
            temporary.removeChild(temporary.getFirstChild());
            temporary.appendChild(temporary.importNode(root, true));
        }

        return temporary;
    }
}

// Checkstyle: MagicNumber on