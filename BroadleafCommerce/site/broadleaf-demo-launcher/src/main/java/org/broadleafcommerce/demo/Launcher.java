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
package org.broadleafcommerce.demo;

import java.net.URL;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * 
 * @author jfischer
 *
 */
public class Launcher {
	
	public static void main(String[] items) {
		if (items.length > 1 || (items.length == 1 && !items[0].equals("start") && !items[0].equals("stop"))) {
			System.out.println("Usage pattern: java -jar broadleaf-demo-launcher-[version].jar start|stop");
			System.out.println("Must provide an operation parameter - either start or stop");
			System.exit(1);
		}
		Project project = new Project();
		project.init();
		DefaultLogger antLogger = new DefaultLogger();
		antLogger.setErrorPrintStream(System.err);
		antLogger.setOutputPrintStream(System.out);
		antLogger.setMessageOutputLevel(Project.MSG_INFO);
		project.addBuildListener(antLogger);
		URL url = Launcher.class.getClassLoader().getResource("launch.xml");
		ProjectHelper.getProjectHelper().parse(project, url);
		if (items.length == 0 || items[0].equals("start")) {
			project.executeTarget("jetty-start");
		} else {
			project.executeTarget("jetty-stop");
		}
	}

}
