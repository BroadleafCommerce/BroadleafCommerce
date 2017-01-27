/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.search.service.solr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ExceptionHelper;
import org.broadleafcommerce.core.search.service.SearchService;
import org.broadleafcommerce.core.search.service.solr.index.IndexStatusInfo;
import org.broadleafcommerce.core.search.service.solr.index.SolrIndexStatusProvider;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * XML based Index Status provider.  Tracks current index status (last successful event id), events that have error (along with retry count), events that
 * have exceeded the retry count and are considered dead.  Also supports a period purge of the dead events to keep the XML file from growing too large.
 * 
 * @author Jeff Fischer
 */
public class FileSystemSolrIndexStatusProviderImpl implements SolrIndexStatusProvider {

    private static final Log LOG = LogFactory.getLog(FileSystemSolrIndexStatusProviderImpl.class);
    
    @Resource(name="blSearchService")
    protected SearchService searchService;

    protected DocumentBuilder builder;

    protected XPath xPath;

    protected SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    @Value("${solr.index.status.dead.event.ttl.seconds:165600}") //165600 - 2 days
    protected Integer deadEventTTLSeconds;

    @Value("${solr.index.status.dead.event.purge.seconds:3600}") //3600 - every hour
    protected Integer deadEventPurgeCycleSeconds;

    
    public FileSystemSolrIndexStatusProviderImpl() {
        XPathFactory factory=XPathFactory.newInstance();
        xPath=factory.newXPath();
    }

    /**
     * Updates the XML file with the index status, error status, and dead event status
     */
    @Override
    public synchronized void handleUpdateIndexStatus(IndexStatusInfo status) {
        handleUpdateIndexStatus(status, false);
    }

    /**
     * Updates the XML file with the index status, error status, and dead event status
     */
    protected synchronized void handleUpdateIndexStatus(IndexStatusInfo status, boolean clearDeadEvents) {
        try {
            if (searchService instanceof SolrSearchServiceImpl) {
                File statusFile = getStatusFile((SolrSearchServiceImpl) searchService);
                boolean exists = statusFile.exists();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setIgnoringElementContentWhitespace(true);
                dbf.setNamespaceAware(true);
                if (builder == null) {
                    builder = dbf.newDocumentBuilder();
                }
                Document document;
                Element rootElement;
                if (exists) {
                    document = builder.parse(statusFile);
                    rootElement = document.getDocumentElement();
                } else {
                    document = builder.newDocument();
                    rootElement = document.createElement("status");
                    document.appendChild(rootElement);
                }
                updateIndexSegment(document, rootElement, status);
                updateErrorSegment(document, rootElement, status);
                updateDeadEventSegment(document, rootElement, status, clearDeadEvents);

                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer xmlTransformer = tFactory.newTransformer();
                xmlTransformer.setOutputProperty(OutputKeys.VERSION, "1.0");
                xmlTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                xmlTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

                DOMSource source = new DOMSource(document);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(statusFile, false), "UTF-8"));
                StreamResult result = new StreamResult(writer);
                xmlTransformer.transform(source, result);
            }
        } catch (ParserConfigurationException e) {
            throw ExceptionHelper.refineException(e);
        } catch (SAXException e) {
            throw ExceptionHelper.refineException(e);
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        } catch (XPathExpressionException e) {
            throw ExceptionHelper.refineException(e);
        } catch (TransformerException e) {
            throw ExceptionHelper.refineException(e);
        } catch (ParseException e) {
            throw ExceptionHelper.refineException(e);
        }
    }

    /**
     * Performs the XML building for the Index segment
     * @param document
     * @param rootElement
     * @param status
     * @throws XPathExpressionException
     */
    protected void updateIndexSegment(Document document, Element rootElement, IndexStatusInfo status) throws XPathExpressionException, ParseException {
        Element indexElement;
        NodeList indexNodeList = (NodeList) xPath.evaluate("/status/index", document, XPathConstants.NODESET);
        if (indexNodeList.getLength() > 0) {
            indexElement = (Element) indexNodeList.item(0);
        } else {
            indexElement = document.createElement("index");
            indexElement.setAttribute("dateProcessed", "");
            rootElement.appendChild(indexElement);
        }
        String lastProcessed = indexElement.getAttribute("dateProcessed");
        //if we have a last processed from file and the new status index date is newer, update it.
        if (lastProcessed != null && ! lastProcessed.equals("")) {
            Date lastProcessedDate = format.parse(lastProcessed);
            if (status.getLastIndexDate().compareTo(lastProcessedDate) > 0) {
                indexElement.setAttribute("dateProcessed", format.format(status.getLastIndexDate()));
                clearNode(indexElement, "info");
            }
        } else {
            indexElement.setAttribute("dateProcessed", format.format(status.getLastIndexDate()));
        }
        for (Map.Entry<String, String> entry : status.getAdditionalInfo().entrySet()) {
            NodeList infos = (NodeList) xPath.evaluate("info[@key='" + entry.getKey() + "']", indexElement, XPathConstants.NODESET);
            if (infos.getLength() == 0) {
                Element addlInfo = document.createElement("info");
                addlInfo.setAttribute("key", entry.getKey());
                addlInfo.setAttribute("val", entry.getValue());
                indexElement.appendChild(addlInfo);
                LOG.debug(String.format("Adding new solr index entry %s", entry.getValue()));
            }
        }
    }
    
    /**
     * Performs the XML building for the error segment
     * @param document
     * @param rootElement
     * @param status
     * @throws XPathExpressionException
     */
    protected void updateErrorSegment(Document document, Element rootElement, IndexStatusInfo status) throws XPathExpressionException {
        Element errorsElement = null;
        clearNode(rootElement, "errors");
        NodeList indexNodeList = (NodeList) xPath.evaluate("/status/errors", document, XPathConstants.NODESET);
        if (indexNodeList.getLength() > 0) {
            errorsElement = (Element) indexNodeList.item(0);
        } else {
            if (status.getIndexErrors().size() > 0) {
                errorsElement = document.createElement("errors");
                rootElement.appendChild(errorsElement);
            }
        }
        for (Map.Entry<Long, Integer> entry : status.getIndexErrors().entrySet()) {
            NodeList errors = (NodeList) xPath.evaluate("error[@key='" + entry.getKey() + "']", errorsElement, XPathConstants.NODESET);
            if (errors.getLength() == 0) { //add the error
                Element anError = document.createElement("error");
                anError.setAttribute("key", entry.getKey().toString());
                anError.setAttribute("retry", entry.getValue().toString());
                errorsElement.appendChild(anError);
                LOG.debug(String.format("Adding/Updating solr index ERROR entry %d with retry count = %d", entry.getKey(), entry.getValue()));
            }
        }
    }
    
    /**
     * Performs the XML building for the dead event segment.  Note that this only clears the dead event node if specified.
     * @param document
     * @param rootElement
     * @param status
     * @throws XPathExpressionException
     */
    protected void updateDeadEventSegment(Document document, Element rootElement, IndexStatusInfo status, boolean clearDeadEvents) throws XPathExpressionException{
        Element deadEventElement = null;
        if (clearDeadEvents) {
            clearNode(rootElement, "dead-events");
        }
        NodeList indexNodeList = (NodeList) xPath.evaluate("/status/dead-events", document, XPathConstants.NODESET);
        if (indexNodeList.getLength() > 0) {
            deadEventElement = (Element) indexNodeList.item(0);
        } else {
            if (status.getDeadIndexEvents().size() > 0) {
                deadEventElement = document.createElement("dead-events");
                //if we are creating this attribute, either a purge occurred or we are creating this element for the first time
                deadEventElement.setAttribute("lastPurgeDate", format.format(new Date()));
                rootElement.appendChild(deadEventElement);
            }
        }
        for (Map.Entry<Long, Date> entry : status.getDeadIndexEvents().entrySet()) {
            NodeList errors = (NodeList) xPath.evaluate("event[@key='" + entry.getKey() + "']", deadEventElement, XPathConstants.NODESET);
            if (errors.getLength() == 0) {
                Element deadEvent = document.createElement("event");
                deadEvent.setAttribute("key", entry.getKey().toString());
                deadEvent.setAttribute("val", format.format(entry.getValue()));
                deadEventElement.appendChild(deadEvent);
                LOG.debug(String.format("Adding new solr index Dead Event entry %d", entry.getKey()));
            }
        }
    }

    /**
     * Common routine to remove all children nodes from the passed element container
     * @param parentElement
     * @param nodeName
     * @throws XPathExpressionException
     */
    protected void clearNode(Element parentElement, String nodeName) throws XPathExpressionException {
        if (parentElement.hasChildNodes()) {
            NodeList children = (NodeList) xPath.evaluate(nodeName, parentElement, XPathConstants.NODESET);
            for (int j = 0; j < children.getLength(); j++) {
                parentElement.removeChild(children.item(j));
            }
            children = parentElement.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                if (children.item(j).getNodeName().equalsIgnoreCase("#text")) {
                    parentElement.removeChild(children.item(j));
                }
            }
        }
    }
    
    /**
     * Builds the Index Status object.  Note for efficiency reasons, this typically does not return the Dead Event instances.  The
     * exception is if a purge cycle is executed.
     */
    @Override
    public synchronized IndexStatusInfo readIndexStatus(IndexStatusInfo status) {
        try {
            if (searchService instanceof SolrSearchServiceImpl) {
                File statusFile = getStatusFile((SolrSearchServiceImpl) searchService);
                boolean exists = statusFile.exists();
                if (exists) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setNamespaceAware(true);
                    if (builder == null) {
                        builder = dbf.newDocumentBuilder();
                    }
                    Document document = builder.parse(statusFile);
                    NodeList indexNodeList = (NodeList) xPath.evaluate("/status/index", document, XPathConstants.NODESET);
                    Element indexElement = (Element) indexNodeList.item(0);
                    status.setLastIndexDate(format.parse(indexElement.getAttribute("dateProcessed")));
                    NodeList infos = (NodeList) xPath.evaluate("info", indexElement, XPathConstants.NODESET);
                    for (int j = 0; j < infos.getLength(); j++) {
                        Element info = (Element) infos.item(j);
                        status.getAdditionalInfo().put(info.getAttribute("key"), info.getAttribute("val"));
                    }
                    NodeList errorsNodeList = (NodeList) xPath.evaluate("/status/errors", document, XPathConstants.NODESET);
                    if (errorsNodeList.getLength() > 0) {
                        Element errorsElement = (Element) errorsNodeList.item(0);
                        NodeList errors = (NodeList) xPath.evaluate("error", errorsElement, XPathConstants.NODESET);
                        for (int j = 0; j < errors.getLength(); j++) {
                            Element anError = (Element) errors.item(j);
                            Long eventId = Long.valueOf(anError.getAttribute("key"));
                            Integer retries = Integer.valueOf(anError.getAttribute("retry"));
                            status.getIndexErrors().put(eventId, retries);
                        }
                    }
                    purgeDeadEvents(document, status);
                }
            }
        } catch (ParserConfigurationException e) {
            throw ExceptionHelper.refineException(e);
        } catch (SAXException e) {
            throw ExceptionHelper.refineException(e);
        } catch (IOException e) {
            throw ExceptionHelper.refineException(e);
        } catch (XPathExpressionException e) {
            throw ExceptionHelper.refineException(e);
        } catch (ParseException e) {
            throw ExceptionHelper.refineException(e);
        }
        return status;
    }

    /**
     * Periodically purges the dead events based on solr.index.status.dead.event.purge.seconds - populating the deadEvents map in the IndexStatusInfo when it does.
     * @param document
     * @param status
     * @throws XPathExpressionException
     * @throws ParseException
     */
    protected void purgeDeadEvents(Document document, IndexStatusInfo status) throws XPathExpressionException, ParseException {
        boolean eventsPurged = false;
        NodeList deadEventNodeList = (NodeList) xPath.evaluate("/status/dead-events", document, XPathConstants.NODESET);
        if (deadEventNodeList.getLength() > 0) {
            Element deadEventsElement = (Element) deadEventNodeList.item(0);
            String lastPurge = deadEventsElement.getAttribute("lastPurgeDate");
            if (lastPurge != null && ! lastPurge.equals("")) {
                Date lastPurgeDate = format.parse(lastPurge);
                if (Long.valueOf(lastPurgeDate.getTime()) <= new Date().getTime() - deadEventPurgeCycleSeconds * 1000) {
                    NodeList deadEvents = (NodeList) xPath.evaluate("event", deadEventsElement, XPathConstants.NODESET);
                    Long deadEventExpiration = new Date().getTime() - deadEventTTLSeconds * 1000;
                    for (int j = 0; j < deadEvents.getLength(); j++) {
                        Element anEvent = (Element) deadEvents.item(j);
                        Date dateAttempted = format.parse(anEvent.getAttribute("val"));
                        Long eventTimeInMs = dateAttempted.getTime();
                        if (eventTimeInMs > deadEventExpiration) { //keep these events
                            Long eventId = Long.valueOf(anEvent.getAttribute("key"));
                            status.getDeadIndexEvents().put(eventId, dateAttempted);
                        } else {
                            eventsPurged = true;
                        }
                    }
                    LOG.debug(String.format("Purging solr index dead error entries - kept %d of %d", status.getDeadIndexEvents().size(), deadEvents.getLength()));
                }
            }
        }
        if (eventsPurged) {
            handleUpdateIndexStatus(status, true);
        }
    }

    protected File getStatusFile(SolrSearchServiceImpl searchService) {
        String statusDirectory = getStatusDirectory(searchService);
        File statusFile = new File(new File(statusDirectory), "solr_status.xml");
        return statusFile;
    }

    protected String getStatusDirectory(SolrSearchServiceImpl searchService) {
        String solrHome = searchService.getSolrHomePath();
        if (solrHome == null) {
            return System.getProperty("java.io.tmpdir");
        }
        return solrHome;
    }
    
}
