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
 * @author Jeff Fischer
 */
public class ImportProcessor {

    private static final Log LOG = LogFactory.getLog(ImportProcessor.class);

    private static DocumentBuilder builder;
    private static XPath xPath;
    private static final String importPath = "/beans/import";

    static {
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

    private ApplicationContext applicationContext;

    public ImportProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ResourceInputStream[] processImports(ResourceInputStream[] sources) throws MergeException {
        try {
            DynamicResourceIterator resourceList = new DynamicResourceIterator();
            resourceList.addAll(Arrays.asList(sources));
            while(resourceList.hasNext()) {
                ResourceInputStream myStream = resourceList.nextResource();
                Document doc = builder.parse(myStream);
                NodeList nodeList = (NodeList) xPath.evaluate(importPath, doc, XPathConstants.NODESET);
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
