/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.extensibility.context.merge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.context.ResourceInputStream;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

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

    protected ApplicationContext applicationContext;
    protected DocumentBuilder builder;
    protected XPath xPath;

    public ImportProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
                    Resource resource = applicationContext.getResource(element.getAttribute("resource"));
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
