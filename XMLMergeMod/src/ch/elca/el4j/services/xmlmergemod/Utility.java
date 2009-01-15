package ch.elca.el4j.services.xmlmergemod;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utility {

	public static List buildListFromNodeList(NodeList nodelist) {
		List list = new ArrayList();
		int length = nodelist.getLength();
		for (int j=0;j<length;j++){
			list.add(nodelist.item(j));
		}
		
		return list;
	}
	
	public static String formatItem(Node node) throws TransformerConfigurationException, TransformerException {
		StringWriter writer = new StringWriter();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer xmlTransformer = tFactory.newTransformer();
		//xmlTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
		//xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(node);
		StreamResult after = new StreamResult(writer);
		xmlTransformer.transform(source, after);
		
		return writer.toString();
	}
}
