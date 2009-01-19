package ch.elca.el4j.services.xmlmergemod;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.elca.el4j.services.xmlmergemod.config.ConfigurableXmlMerge;
import ch.elca.el4j.services.xmlmergemod.config.PropertyXPathConfigurer;

public class Demo {

	public static void main(String[] items) {
		try {
			Configurer configurer = new PropertyXPathConfigurer(
					"matcher.default=TAG\n" +
					"action.default=MERGE\n" +
					"xpath.2=//bean[@id='persistenceUnitManager']/property[@name='persistenceXmlLocations']/list\n" +
					//"action.1=PRESERVE\n" +
					//"matcher.2=com.springcommerce.demo.framework.PersistenceUnitMatcher\n" +
					//"xpath.2=//spring:bean[@id='persistenceUnitManager']/property\n" +
					//"xpath.3=//spring:bean[@id='persistenceUnitManager']/property/list\n" +
					//"xpath.4=//spring:bean[@id='persistenceUnitManager']/property/list/value\n" +
					"action.2=REPLACE\n" + 
					"matcher.2=SKIP"
			);
			InputStream in = null;
			try {
				in = new ConfigurableXmlMerge(configurer).merge(
						new InputStream[] {
								new BufferedInputStream(new FileInputStream(new File("/Java_Projects/dev/credera/DemoExtensibilityDataLayer/src-framework-perspective/applicationContext-framework.xml"))),
								new BufferedInputStream(new FileInputStream(new File("/Java_Projects/dev/credera/DemoExtensibilityDataLayer/src-framework-perspective/applicationContext-framework2.xml")))
						}
				);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (AbstractXmlMergeException e1) {
				e1.printStackTrace();
			}
			
			InputStreamReader reader = null;
			int temp;
			StringBuffer item = new StringBuffer();
			boolean eof = false;
			try {
				reader = new InputStreamReader(in);
				while (!eof) {
					temp = reader.read();
					if (temp == -1) {
						eof = true;
					} else {
						item.append((char) temp);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try{ reader.close(); } catch (Throwable e) {}
				}
			}
			
			System.out.println(item.toString());
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
}
