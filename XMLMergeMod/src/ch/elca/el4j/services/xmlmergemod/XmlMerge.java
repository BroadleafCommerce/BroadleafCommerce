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

import java.io.InputStream;

import org.w3c.dom.Document;

/**
 * Entry point for merging XML documents.
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
public interface XmlMerge {

    /**
     * Merges the given InputStream sources.
     * 
     * @param sources
     *            Array of InputStream sources to merge
     * @return InputStream corresponding to the merged sources
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */  
    public InputStream merge(InputStream[] sources)
        throws AbstractXmlMergeException;

    /**
     * Merges the given Document sources.
     * 
     * @param sources
     *            Array of Document sources to merge
     * @return Document corresponding to the merged sources
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */
    public Document merge(Document[] sources) throws AbstractXmlMergeException;

    /**
     * Merges the given String sources.
     * 
     * @param sources
     *            Array of String sources to merge
     * @return String corresponding to the merged sources
     * @throws AbstractXmlMergeException
     *             If an error occurred during the merge
     */
    public String merge(String[] sources) throws AbstractXmlMergeException;

    /**
     * Sets the MergeAction which will be applied to the root element.
     * 
     * @param rootMergeAction
     *            The MergeAction which will be applied to the root element
     */
    public void setRootMergeAction(MergeAction rootMergeAction);

    /**
     * Sets the Mapper which will be applied to the root element.
     * 
     * @param rootMapper
     *            The Mapper which will be applied to the root element
     */
    public void setRootMapper(Mapper rootMapper);

}