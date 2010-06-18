/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.extensibility.context.merge;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler;
import org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandlerAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This class manages all xml merge interactions with callers. It is responsible for
 * not only loading the handler configurations, but also for cycling through the handlers
 * in a prioritized fashion and exporting the final merged document.
 *
 * @author jfischer
 *
 */
public class MergeManager {

    /**
     * Additional merge points may be added by the caller. Also default merge points
     * may be overriden to change their current behavior. This is accomplished by
     * specifying the system property denoted by the key MergeManager.MERGE_DEFINITION_SYSTEM_PROPERTY
     * with a value stating the fully qualified path of user-created property file. Please refer
     * to the default properties file located at org/broadleafcommerce/extensibility/context/merge/default.properties
     * for more details.
     *
     */
    public static final String MERGE_DEFINITION_SYSTEM_PROPERTY = "org.broadleafcommerce.extensibility.context.merge.handlers.merge.properties";

    private static final Log LOG = LogFactory.getLog(MergeManager.class);

    private static DocumentBuilder builder;

    static {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LOG.error("Unable to create document builder", e);
            throw new RuntimeException(e);
        }
    }

    private MergeHandler[] handlers;

    public MergeManager() throws MergeManagerSetupException {
        try {
            Properties props = loadProperties();
            setHandlers(props);
        } catch (IOException e) {
            throw new MergeManagerSetupException(e);
        } catch (ClassNotFoundException e) {
            throw new MergeManagerSetupException(e);
        } catch (IllegalAccessException e) {
            throw new MergeManagerSetupException(e);
        } catch (InstantiationException e) {
            throw new MergeManagerSetupException(e);
        }
    }

    /**
     * Merge 2 xml document streams together into a final resulting stream. During
     * the merge, various merge business rules are followed based on configuration
     * defined for various merge points.
     *
     * @param stream1
     * @param stream2
     * @return the stream representing the merged document
     * @throws MergeException
     */
    public InputStream merge(InputStream stream1, InputStream stream2) throws MergeException {
        try {
            Document doc1 = builder.parse(stream1);
            Document doc2 = builder.parse(stream2);

            List<Node> exhaustedNodes = new ArrayList<Node>();

            //process any defined handlers
            for (int j=0;j<this.handlers.length;j++){
            	if (LOG.isDebugEnabled()) {
            		LOG.debug("Processing handler: " + this.handlers[j].getXPath());
            	}
                MergePoint point = new MergePoint(this.handlers[j], doc1, doc2);
                Node[] list = point.merge(exhaustedNodes);
                if (list != null) {
                    for (int x=0;x<list.length;x++){
                        exhaustedNodes.add(list[x]);
                    }
                }
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer xmlTransformer = tFactory.newTransformer();
            xmlTransformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            xmlTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            xmlTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
            xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
            StreamResult result = new StreamResult(writer);
            xmlTransformer.transform(source, result);

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Throwable e) {
            throw new MergeException(e);
        }
    }

    private void setHandlers(Properties props) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ArrayList<MergeHandler> handlers = new ArrayList<MergeHandler>();
        String[] keys = {};
        keys = props.keySet().toArray(keys);
        for (int j=0;j<keys.length;j++){
            if (keys[j].startsWith("handler.")) {
                MergeHandler temp = (MergeHandler) Class.forName(props.getProperty(keys[j])).newInstance();
                String name = keys[j].substring(8, keys[j].length());
                temp.setName(name);
                String priority = props.getProperty("priority."+name);
                if (priority != null) {
                    temp.setPriority(Integer.parseInt(priority));
                }
                String xpath = props.getProperty("xpath."+name);
                if (priority != null) {
                    temp.setXPath(xpath);
                }
                handlers.add(temp);
            }
        }
        MergeHandler[] explodedView = {};
        explodedView = handlers.toArray(explodedView);
        Comparator<Object> nameCompare = new Comparator<Object>() {
            public int compare(Object arg0, Object arg1) {
                return ((MergeHandler) arg0).getName().compareTo(((MergeHandler) arg1).getName());
            }
        };
        Arrays.sort(explodedView, nameCompare);
        ArrayList<MergeHandler> finalHandlers = new ArrayList<MergeHandler>();
        for (int j=0;j<explodedView.length;j++){
            MergeHandler temp = explodedView[j];
            if (temp.getName().indexOf(".") >= 0) {
                final String parentName = temp.getName().substring(0, temp.getName().lastIndexOf("."));
                int pos = Arrays.binarySearch(explodedView, new MergeHandlerAdapter() {
                    @Override
                    public String getName() {
                        return parentName;
                    }
                }, nameCompare);
                if (pos >= 0) {
                    MergeHandler[] parentHandlers = explodedView[pos].getChildren();
                    MergeHandler[] newHandlers = new MergeHandler[parentHandlers.length + 1];
                    System.arraycopy(parentHandlers, 0, newHandlers, 0, parentHandlers.length);
                    newHandlers[newHandlers.length - 1] = temp;
                    Arrays.sort(newHandlers);
                    explodedView[pos].setChildren(newHandlers);
                }
            } else {
                finalHandlers.add(temp);
            }
        }

        this.handlers = new MergeHandler[0];
        this.handlers = finalHandlers.toArray(this.handlers);
        Arrays.sort(this.handlers);
    }

    private Properties loadProperties() throws IOException {
        Properties defaultProperties = new Properties();
        defaultProperties.load(MergeManager.class.getResourceAsStream("default.properties"));
        Properties props;
        String overrideFileClassPath = System.getProperty(MERGE_DEFINITION_SYSTEM_PROPERTY);
        if (overrideFileClassPath != null) {
            props = new Properties(defaultProperties);
            props.load(MergeManager.class.getClassLoader().getResourceAsStream(overrideFileClassPath));
        } else {
            props = defaultProperties;
        }

        return props;
    }

}
