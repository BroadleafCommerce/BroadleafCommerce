/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * This class serves to parse any passed in source application context files and
 * look for the Spring "import" element. If found, the resource of the import element
 * is retrieved and set as another source element after the current one. Also, once the
 * resource is retrieved and included, the import element is deleted from the source
 * document.
 *
 * @author Jeff Fischer
 */
public class ImportProcessor {

    private static final Log LOG = LogFactory.getLog(ImportProcessor.class);
    private static final String IMPORT_PATH = "/beans/import";

    protected ResourceLoader loader;
    protected DocumentBuilder builder;
    protected XPath xPath;

    public ImportProcessor(ResourceLoader loader) {
        this.loader = loader;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            builder = dbf.newDocumentBuilder();
            XPathFactory factory=XPathFactory.newInstance();
            xPath=factory.newXPath();
        } catch (ParserConfigurationException e) {
            LOG.error("Unable to create document builder", e);
            throw new RuntimeException(e);
        }
    }

    public ResourceInputStream[] extract(ResourceInputStream[] sources) throws MergeException {
        if (sources == null) {
            return null;
        }
        try {
            DynamicResourceIterator resourceList = new DynamicResourceIterator();
            resourceList.addAll(Arrays.asList(sources));
            while(resourceList.hasNext()) {
                ResourceInputStream myStream = resourceList.nextResource();
                Document doc = builder.parse(myStream);
                NodeList nodeList = (NodeList) xPath.evaluate(IMPORT_PATH, doc, XPathConstants.NODESET);
                int length = nodeList.getLength();
                for (int j=0;j<length;j++) {
                    Element element = (Element) nodeList.item(j);
                    Resource resource = loader.getResource(element.getAttribute("resource"));
                    ResourceInputStream ris = new ResourceInputStream(resource.getInputStream(), resource.getURL().toString());
                    resourceList.addEmbeddedResource(ris);
                    element.getParentNode().removeChild(element);
                }
                if (length > 0) {
                    TransformerFactory tFactory = TransformerFactory.newInstance();
                    Transformer xmlTransformer = tFactory.newTransformer();
                    xmlTransformer.setOutputProperty(OutputKeys.VERSION, "1.0");
                    xmlTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    xmlTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

                    DOMSource source = new DOMSource(doc);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
                    StreamResult result = new StreamResult(writer);
                    xmlTransformer.transform(source, result);

                    byte[] itemArray = baos.toByteArray();

                    resourceList.set(resourceList.getPosition() - 1, new ResourceInputStream(new ByteArrayInputStream(itemArray), null, myStream.getNames()));
                } else {
                    myStream.reset();
                }
            }

            return resourceList.toArray(new ResourceInputStream[resourceList.size()]);
        } catch (Exception e) {
            throw new MergeException(e);
        }
    }
}
