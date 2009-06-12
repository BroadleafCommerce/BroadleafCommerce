////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex.ant;

import flex.ant.config.ConfigVariable;
import flex.ant.config.NestedAttributeElement;
import flex.ant.config.OptionSpec;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.Java;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 *
 */
public abstract class FlexTask extends Java
{
    /**
     * The build tool's name.
     */
    protected final String toolName;

    /**
     * The build tool's class name.
     */
    private final String toolClassName;

    /**
     * The build tool's jar file name.
     */
    private final String jarFileName;

     /**
      * The commandline used in execute()
      */
    protected final Commandline cmdl;

    /**
     * An array of ConfigVariabes that are set by setDynamicAttribute().
     */
    protected final ConfigVariable[] variables;

    protected List nestedAttribs;

    /**
     * fork attribute
     */
    protected boolean fork;

    /**
     * max memory attribute
     */
    private String maxmemory;

    private ClassLoader originalContextClassLoader;

    protected static OptionSpec rslpSpec = new OptionSpec(null, "runtime-shared-library-path", "rslp");

    /**
     * @param toolName The build tool's name.
     * @param toolClassName The build tool's class name.
     * @param jarFileName The build tool's jar file.
     * @param vars An array of ConfigVariables that will be set by attributes of the task
     */
    protected FlexTask(String toolName, String toolClassName, String jarFileName, ConfigVariable[] vars)
    {
        this.toolName = toolName;
        this.toolClassName = toolClassName;
        this.jarFileName = jarFileName;
        this.variables = vars;

        cmdl = new Commandline();
    }

    public Object createDynamicElement(String elementName)
    {
        ConfigVariable var = null;

        for (int i = 0; i < variables.length && var == null; i++)
        {
            if (variables[i].matches(elementName))
            {
                var = variables[i];
            }
        }

        if (var != null)
        {
            return createElem(elementName, var.getSpec());
        }
        else
        {
            throw new BuildException("The <" + toolName + "> type doesn't support the \"" +
                                     elementName + "\" nested element.", getLocation());
        }
    }

    protected NestedAttributeElement createElem(String attrib, OptionSpec spec)
    {
        NestedAttributeElement e = new NestedAttributeElement(attrib, spec);
        nestedAttribs.add(e);
        return e;
    }

    protected NestedAttributeElement createElem(String[] attribs, OptionSpec spec)
    {
        NestedAttributeElement e = new NestedAttributeElement(attribs, spec);
        nestedAttribs.add(e);
        return e;
    }

    /*=======================================================================*
     * 	Static Attributes                                                    *
     *=======================================================================*/
    /**
     * Sets whether to run the task in a separate VM.
     *
     * @param f if true then run in a separate VM.
     */
    public void setFork(boolean f)
    {
        super.setFork(f);
        this.fork = f;
    }

    /**
     * Sets the max memory of each VM
     *
     * @param max max memory parameter
     */
    public void setMaxmemory(String max)
    {
        super.setMaxmemory(max);
        this.maxmemory = max;
    }

    /*=======================================================================*
     *  Dynamic Attributes                                                   *
     *=======================================================================*/

    /**
     * Set the named attribute to the given value.
     *
     * @param attributeName The name of the attribute to set
     * @param value The value to set the named attribute to
     */
    public void setDynamicAttribute(String attributeName, String value)
    {
        ConfigVariable var = null;

        for (int i = 0; i < variables.length && var == null; i++)
        {
            if (variables[i].matches(attributeName))
            {
                var = variables[i];
            }
        }

        if (var != null)
        {
            var.set(value);
        }
        else
        {
            throw new BuildException("The <" + toolName + "> type doesn't support the \"" +
                                     attributeName + "\" attribute.", getLocation());
        }
    }

    /*=======================================================================*
     *  Execute and Related Functions                                        *
     *=======================================================================*/

    /**
     * Called by execute after the set ConfigVariables in <code>vars</code> has
     * been added to the commandline. This function is responsible for adding
     * all tool-specific options to the commandline as well as setting the
     * default options of a build tool.
     */
    protected abstract void prepareCommandline() throws BuildException;

    /**
     * Execute the task
     *
     * @throws BuildException If running build tool failed
     */
    public final void execute() throws BuildException
    {
        String flexHomeProperty = getProject().getProperty("FLEX_HOME");

        if (flexHomeProperty == null)
        {
            throw new BuildException("FLEX_HOME must be set to use the Flex Ant Tasks");
        }

        System.setProperty("FLEX_HOME", flexHomeProperty);

        //FIXME: wrap paths in quotes in case they contains spaces (so cmdline parses correctly)
        //
        //     this is a poor solution -- the problem is that arguments are passed as Objects
        //     directly to the compiler, and the compiler will concat them with other Strings.
        //     So you get badly formatted strings like: "C:/sdk"/frameworks
        //
        //     If both entrypoints had different methods of config serialization, or shared
        //     the same method (either pass objects, or a commandline string), this would be
        //     okay. For now we solve it piecemeal.
        //
        //     Search for uses of 'fork' to track this hack.
        {
            final String tmpFlexHomeProperty
                = fork
                    // wrap in double-quotes in case path contains spaces
                    ? ('"' + flexHomeProperty + "/frameworks\"")
                    : (flexHomeProperty + "/frameworks");

            cmdl.createArgument().setValue("+flexlib=" + tmpFlexHomeProperty);
        }
        prepareCommandline();

        if (fork)
            executeOutProcess();
        else
            executeInProcess();

    }

    /**
     * Executes the task in a separate VM
     *
     */
    private void executeOutProcess() throws BuildException
    {
        try
        {
            Class toolClass = resolveClass(toolClassName);
            URL url = toolClass.getProtectionDomain().getCodeSource().getLocation();
            String fileName = url.getFile();

            String[] temp = cmdl.getArguments();
            String s = "";

            super.setClassname(toolClassName);
            super.setClasspath(new Path(getProject(), fileName));

            //converts arguments into a string for use by executeJava()
            for (int i = 0; i < temp.length; i++)
            {
                if(temp[i]!=null)
                {
                    s += temp[i] + " ";
                }
            }

            super.setArgs(s);

            int err = super.executeJava();
            //check error code
            if(err > 0)
            {
                throw new BuildException(toolName + " task failed.");
            }
        }
        finally
        {
            if (originalContextClassLoader != null)
            {
                Thread.currentThread().setContextClassLoader(originalContextClassLoader);
            }
        }
    }

    /**
     * Executes the task in the same VM
     *
     */
    private void executeInProcess() throws BuildException
    {
        try
        {
            Class toolClass = resolveClass(toolClassName);
            Class threadLocalToolkitClass = resolveClass("flex2.compiler.util.ThreadLocalToolkit");

            log("FlexTask.execute: " + cmdl, Project.MSG_DEBUG);

            int errorCount = 0;

            try
            {
                Method toolMethod = toolClass.getMethod(toolName, new Class[] {String[].class});
                toolMethod.invoke(null, new Object[] {cmdl.getArguments()});

                Method errorCountMethod = threadLocalToolkitClass.getMethod("errorCount", (Class[]) null);
                errorCount = ((Integer) errorCountMethod.invoke(null, (Object[]) null)).intValue();
            }
            catch (Exception e)
            {
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                e.printStackTrace(printWriter);
                log(stringWriter.toString(), Project.MSG_DEBUG);
                throw new BuildException("Unable to run " + toolName + ": " + e.getMessage(), e);
            }

            if (errorCount > 0)
            {
                throw new BuildException(toolName + " task failed");
            }
        }
        finally
        {
            if (originalContextClassLoader != null)
            {
                Thread.currentThread().setContextClassLoader(originalContextClassLoader);
            }
        }
    }

    private Class resolveClass(String className)
    {
        Class result = null;

        try
        {
            result = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException ignoredClassNotFoundException)
        {
            String flexHomeProperty = getProject().getProperty("FLEX_HOME");

            if (flexHomeProperty != null)
            {
                File flexHome = new File(flexHomeProperty);

                if ( flexHome.exists() )
                {
                    File jarFile = new File(flexHome + "/lib", jarFileName);

                    if (jarFile.exists())
                    {
                        try
                        {
                            URLClassLoader urlClassLoader = new URLClassLoader(new URL[] {jarFile.toURL()});
                            result = Class.forName(className, true, urlClassLoader);
                            originalContextClassLoader = Thread.currentThread().getContextClassLoader();
                            Thread.currentThread().setContextClassLoader(urlClassLoader);
                        }
                        catch (MalformedURLException malformedURLException)
                        {
                            // We shouldn't really get here, but just in case.
                            malformedURLException.printStackTrace();
                        }
                        catch (ClassNotFoundException classNotFoundException)
                        {
                            throw new BuildException("The class not found in jar file: " + jarFileName,
                                                     getLocation());
                        }
                    }
                    else
                    {
                        throw new BuildException("File does not exist: " + jarFileName, getLocation());
                    }
                }
                else
                {
                    throw new BuildException("FLEX_HOME does not exist.", getLocation());
                }
            }
            else
            {
                throw new BuildException("The class, " + className +
                                         ", must be in the classpath or the FLEX_HOME property must be set.",
                                         getLocation());
            }
        }

        return result;
    }
} //End of FlexTask
