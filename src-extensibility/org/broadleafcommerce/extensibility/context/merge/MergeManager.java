package org.broadleafcommerce.extensibility.context.merge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.broadleafcommerce.extensibility.context.merge.handlers.MergeHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MergeManager {

    public static void main(String[] items) {
        try {
            MergeManager manager = new MergeManager();
            InputStream stream1 = MergeManager.class.getResourceAsStream("applicationContext_1.xml");
            InputStream stream2 = MergeManager.class.getResourceAsStream("applicationContext_2.xml");

            InputStream result = manager.merge(stream1, stream2);

            BufferedReader reader = null;
            try{
                boolean eof = false;
                reader = new BufferedReader(new InputStreamReader(result));
                while (!eof) {
                    String temp = reader.readLine();
                    if (temp == null) {
                        eof = true;
                    } else {
                        System.out.println(temp);
                    }
                }
            } finally {
                if (reader != null) {
                    try{ reader.close(); } catch (Throwable e) {}
                }
            }
        } catch (MergeManagerSetupException e) {
            e.printStackTrace();
        } catch (MergeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @SuppressWarnings("unchecked")
    private static ThreadLocal DOCUMENT = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return builder.newDocument();
        }
    };

    public static void clearDocument() {
        DOCUMENT.remove();
    }

    public static Document getCurrentDocument() {
        return (Document) DOCUMENT.get();
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
            @Override
            public int compare(Object arg0, Object arg1) {
                return ((MergeHandler) arg0).getName().compareTo(((MergeHandler) arg1).getName());
            }
        };
        Arrays.sort(explodedView, nameCompare);
        ArrayList<MergeHandler> finalHandlers = new ArrayList<MergeHandler>();
        for (int j=0;j<explodedView.length;j++){
            MergeHandler temp = explodedView[j];
            if (temp.getName().indexOf(".") >= 0) {
                String parentName = temp.getName().substring(0, temp.getName().lastIndexOf("."));
                int pos = Arrays.binarySearch(explodedView, parentName, nameCompare);
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
        String overrideFileClassPath = System.getProperty("org.broadleafcommerce.extensibility.context.merge.handlers.merge.properties");
        if (overrideFileClassPath != null) {
            props = new Properties(defaultProperties);
            props.load(MergeManager.class.getClassLoader().getResourceAsStream(overrideFileClassPath));
        } else {
            props = defaultProperties;
        }

        return props;
    }

    public InputStream merge(InputStream stream1, InputStream stream2) throws MergeException {
        try {
            XPathFactory  factory=XPathFactory.newInstance();
            XPath xPath=factory.newXPath();
            ArrayList<Node> exhaustedNodes1 = new ArrayList<Node>();
            ArrayList<Node> exhaustedNodes2 = new ArrayList<Node>();
            Document doc1 = builder.parse(stream1);
            Document doc2 = builder.parse(stream2);

            //process any defined handlers
            for (int j=0;j<this.handlers.length;j++){
                MergeHandler temp = this.handlers[j];
                Node node1 = (Node) xPath.evaluate(temp.getXPath(), doc1, XPathConstants.NODE);
                Node node2 = (Node) xPath.evaluate(temp.getXPath(), doc2, XPathConstants.NODE);
                if (node1 != null && node2 != null && !exhaustedNodes1.contains(node1) && !exhaustedNodes2.contains(node2)) {
                    exhaustedNodes1.add(node1);
                    exhaustedNodes2.add(node2);
                    temp.merge(node1, node2);
                }
            }

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer xmlTransformer = tFactory.newTransformer();
            xmlTransformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            xmlTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            xmlTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
            xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(getCurrentDocument());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
            StreamResult result = new StreamResult(writer);
            xmlTransformer.transform(source, result);

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Throwable e) {
            throw new MergeException(e);
        } finally {
            clearDocument();
        }
    }
}
