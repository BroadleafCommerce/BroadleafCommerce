/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.extensibility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This class is based on the OpenJPA's org.apache.openjpa.enhance.InstrumentationFactory.  It essentially does
 * its best to install an instrumentation agent.  The preferred or prescribed way to install an instrumentation agent
 * is to add the agent as an argument on the command line when starting the JVM.  This class attempts to do the
 * same thing after the JVM has already started.  Unfortunately, this is the only way we know of to attach an agent to
 * the JVM except by adding a "javaagent:..." flag on the command line.
 *
 * @author Kelly Tisdell
 * @deprecated Because of classloader differences, this approach is not reliable for some containers. Use the javaagent jvm argument instead to set instrumentation.
 */
@Deprecated
public class InstrumentationRuntimeFactory {
    private static final Log LOG = LogFactory.getLog(InstrumentationRuntimeFactory.class);
    private static final String IBM_VM_CLASS = "com.ibm.tools.attach.VirtualMachine";
    private static final String SUN_VM_CLASS = "com.sun.tools.attach.VirtualMachine";
    private static boolean isIBM = false;
    private static Instrumentation inst;

    /**
     * This method is called by the JVM to set the instrumentation.  We can't synchronize this because it will cause
     * a deadlock with the thread calling the getInstrumentation() method when the instrumentation is installed.
     *
     * @param agentArgs
     * @param instrumentation
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        inst = instrumentation;
    }

    /**
     * This method returns the Instrumentation object provided by the JVM. If the Instrumentation object is null,
     * it does its best to add an instrumentation agent to the JVM and then the instrumentation object.
     * @return Instrumentation
     */
    public static synchronized Instrumentation getInstrumentation() {
        if (inst != null) {
            return inst;
        }
        
        if (System.getProperty("java.vendor").toUpperCase().contains("IBM")) {
            isIBM = true;
        }

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    if (!InstrumentationRuntimeFactory.class.getClassLoader().equals(
                            ClassLoader.getSystemClassLoader())) {
                        return null;
                    }
                } catch (Throwable t) {
                    return null;
                }
                File toolsJar = null;
                // When running on IBM, the attach api classes are packaged in vm.jar which is a part
                // of the default vm classpath.
                if (! isIBM) {
                    // If we can't find the tools.jar and we're not on IBM we can't load the agent.
                    toolsJar = findToolsJar();
                    if (toolsJar == null) {
                        return null;
                    }
                }

                Class<?> vmClass = loadVMClass(toolsJar);
                if (vmClass == null) {
                    return null;
                }
                String agentPath = getAgentJar();
                if (agentPath == null) {
                    return null;
                }
                loadAgent(agentPath, vmClass);
                return null;
            }
        });

        return inst;
    }

    private static File findToolsJar() {
        String javaHome = System.getProperty("java.home");
        File javaHomeFile = new File(javaHome);

        File toolsJarFile = new File(javaHomeFile, "lib" + File.separator + "tools.jar");
        if (!toolsJarFile.exists()) {
            // If we're on an IBM SDK, then remove /jre off of java.home and try again.
            if (javaHomeFile.getAbsolutePath().endsWith(File.separator + "jre")) {
                javaHomeFile = javaHomeFile.getParentFile();
                toolsJarFile = new File(javaHomeFile, "lib" + File.separator + "tools.jar");
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // If we're on a Mac, then change the search path to use ../Classes/classes.jar.
                if (javaHomeFile.getAbsolutePath().endsWith(File.separator + "Home")) {
                    javaHomeFile = javaHomeFile.getParentFile();
                    toolsJarFile = new File(javaHomeFile, "Classes" + File.separator + "classes.jar");

                }
            }
        }

        if (! toolsJarFile.exists()) {
            return null;
        } else {
            return toolsJarFile;
        }
    }

    private static String createAgentJar() throws IOException {
        File file =
                File.createTempFile(InstrumentationRuntimeFactory.class.getName(), ".jar");
        file.deleteOnExit();

        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
        zout.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(zout));

        writer.println("Agent-Class: " + InstrumentationRuntimeFactory.class.getName());
        writer.println("Can-Redefine-Classes: true");
        // IBM doesn't support retransform
        writer.println("Can-Retransform-Classes: " + Boolean.toString(!isIBM));

        writer.close();

        return file.getAbsolutePath();
    }

    private static String getAgentJar() {
        File agentJarFile = null;
        // Find the name of the File that this class was loaded from. That
        // jar *should* be the same location as our agent.
        CodeSource cs =
                InstrumentationRuntimeFactory.class.getProtectionDomain().getCodeSource();
        if (cs != null) {
            URL loc = cs.getLocation();
            if (loc != null) {
                agentJarFile = new File(loc.getFile());
            }
        }

        // Determine whether the File that this class was loaded from has this
        // class defined as the Agent-Class.
        boolean createJar = false;
        if (cs == null || agentJarFile == null
                || agentJarFile.isDirectory()) {
            createJar = true;
        } else if (!validateAgentJarManifest(agentJarFile, InstrumentationRuntimeFactory.class.getName())) {
            // We have an agentJarFile, but this class isn't the Agent-Class.
            createJar = true;
        }

        String agentJar;
        if (createJar) {
            try {
                agentJar = createAgentJar();
            } catch (IOException ioe) {
                agentJar = null;
            }
        } else {
            agentJar = agentJarFile.getAbsolutePath();
        }

        return agentJar;
    }

    private static void loadAgent(String agentJar, Class<?> vmClass) {
        try {
            // first obtain the PID of the currently-running process
            // ### this relies on the undocumented convention of the
            // RuntimeMXBean's
            // ### name starting with the PID, but there appears to be no other
            // ### way to obtain the current process' id, which we need for
            // ### the attach process
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            String pid = runtime.getName();
            if (pid.contains("@"))
                pid = pid.substring(0, pid.indexOf("@"));

            // JDK1.6: now attach to the current VM so we can deploy a new agent
            // ### this is a Sun JVM specific feature; other JVMs may offer
            // ### this feature, but in an implementation-dependent way
            Object vm = vmClass.getMethod("attach", new Class<?>[]{String.class}).invoke(null, pid);
            vmClass.getMethod("loadAgent", new Class[]{String.class}).invoke(vm, agentJar);
            vmClass.getMethod("detach", new Class[]{}).invoke(vm);
        } catch (Throwable t) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Problem loading the agent", t);
            }
        }
    }

    private static Class<?> loadVMClass(File toolsJar) {
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String cls = SUN_VM_CLASS;
            if (isIBM) {
                cls = IBM_VM_CLASS;
            } else {
                loader = new URLClassLoader(new URL[]{toolsJar.toURI().toURL()}, loader);
            }
            return loader.loadClass(cls);
        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Failed to load the virtual machine class", e);
            }
        }
        return null;
    }

    private static boolean validateAgentJarManifest(File agentJarFile,
                                                    String agentClassName) {
        try {
            JarFile jar = new JarFile(agentJarFile);
            Manifest manifest = jar.getManifest();
            if (manifest == null) {
                return false;
            }
            Attributes attributes = manifest.getMainAttributes();
            String ac = attributes.getValue("Agent-Class");
            if (ac != null && ac.equals(agentClassName)) {
                return true;
            }
        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Unexpected exception occured.", e);
            }
        }
        return false;
    }
}
