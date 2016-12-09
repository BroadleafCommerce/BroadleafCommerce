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
package org.broadleafcommerce.common.extensibility.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.context.merge.MergeXmlConfigResource;
import org.broadleafcommerce.common.extensibility.context.merge.ResourceInputStream;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.common.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.broadleafcommerce.common.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author jfischer
 *
 */
public class MergeJPAPersistenceResource extends MergeXmlConfigResource {

    private static final Log LOG = LogFactory.getLog(MergeJPAPersistenceResource.class);
    private ErrorHandler handler = new SimpleSaxErrorHandler(LOG);

    @Override
    public Resource getMergedConfigResource(ResourceInputStream[] sources) throws BeansException {
        Resource configResource = null;
        ResourceInputStream merged = null;
        try {
            List<String> mappingFiles = new ArrayList<>(20);
            ResourceInputStream[] inMemoryStreams = new ResourceInputStream[sources.length];
            for (int j=0;j<sources.length;j++){
                byte[] sourceArray = buildArrayFromStream(sources[j]);
                compileMappingFiles(mappingFiles, sourceArray);
                inMemoryStreams[j] = new ResourceInputStream(new ByteArrayInputStream(sourceArray), sources[j].getName());
            }

            merged = merge(inMemoryStreams);

            //read the final stream into a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean eof = false;
            while (!eof) {
                int temp = merged.read();
                if (temp == -1) {
                    eof = true;
                } else {
                    baos.write(temp);
                }
            }
            configResource = new ByteArrayResource(baos.toByteArray());

            if (LOG.isDebugEnabled()) {
                LOG.debug("Merged config: \n" + StringUtil.sanitize(serialize(configResource)));
            }
        } catch (MergeException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } catch (MergeManagerSetupException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } catch (IOException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } catch (SAXException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } catch (ParserConfigurationException e) {
            throw new FatalBeanException("Unable to merge source and patch locations", e);
        } finally {
            if (merged != null) {
                try{ merged.close(); } catch (Throwable e) {
                    LOG.error("Unable to merge source and patch locations", e);
                }
            }
        }

        return configResource;
    }

    private void compileMappingFiles(List<String> mappingFiles, byte[] sourceArray) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        // Disable DTDs to prevent XXE attack
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        DocumentBuilder parser = dbf.newDocumentBuilder();
        parser.setErrorHandler(handler);

        Document dom = parser.parse(new ByteArrayInputStream(sourceArray));

        NodeList nodes = dom.getElementsByTagName("/persistence/persistence-unit/mapping-file");
        if (nodes != null && nodes.getLength() > 0) {
            int length = nodes.getLength();
            for (int j=0;j<length;j++){
                Node node = nodes.item(j);
                mappingFiles.add(node.getNodeValue());
            }
        }
    }
}
