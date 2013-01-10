package org.broadleafcommerce.demo;

import java.net.URL;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

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
