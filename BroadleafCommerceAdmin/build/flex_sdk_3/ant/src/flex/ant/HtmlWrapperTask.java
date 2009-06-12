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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.Task;

public final class HtmlWrapperTask extends Task implements DynamicAttribute
{
    private static final int CLIENT_SIDE_DETECTION = 0;
    private static final int EXPRESS_INSTALLATION = 1;
    private static final int NO_PLAYER_DETECTION = 2;

    private static final String[] templates = new String[] {"client-side-detection",
                                                            "express-installation",
                                                            "no-player-detection"};

    private static final String TEMPLATE_DIR = "/templates/html-templates/";
    private static final String WITH_HISTORY = "-with-history/";
    private static final String INDEX_TEMPLATE_HTML = "index.template.html";
    private static final String AC_OETAGS_JS = "AC_OETags.js";
    private static final String HISTORY_CSS = "history/history.css";
    private static final String HISTORY_JS = "history/history.js";
    private static final String HISTORY_FRAME_HTML = "history/historyFrame.html";
    private static final String PLAYERPRODUCTINSTALL_SWF = "playerProductInstall.swf";

    private String application;
    private String bgcolor = "white";
    private String fileName = "index.html";
    private String height = "400";
    private String output;
    private String swf;
    private String title = "Flex Application";
    private String versionMajor = "9";
    private String versionMinor = "0";
    private String versionRevision = "0";
    private String width = "400";
    private boolean history = false;
    private int template;

    public HtmlWrapperTask()
    {
        setTaskName("html-wrapper");
    }

    public void execute() throws BuildException
    {
        // Check for requirements.
        if (swf == null)
        {
            throw new BuildException("The <html-wrapper> task requires the 'swf' attribute.", getLocation());
        }

        InputStream inputStream = getInputStream();

        if (inputStream != null)
        {
            BufferedReader bufferedReader = null;
            PrintWriter printWriter = null;
            String path = null;

            try
            {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                if (output != null)
                {
                    File outputDir = new File(output);
                    if (outputDir.exists() && outputDir.isDirectory())
                    {
                        path = output + File.separatorChar + fileName;
                    }
                    else
                    {
                        String base = getProject().getBaseDir().getAbsolutePath();
                        outputDir = new File(base + File.separatorChar + output);
                        if (outputDir.exists() && outputDir.isDirectory())
                        {
                            path = base + File.separatorChar + output + File.separatorChar + fileName;
                        }
                        else
                        {
                            throw new BuildException("output directory does not exist: " + output);
                        }
                    }
                }
                else
                {
                    path = fileName;
                }

                printWriter = new PrintWriter(new FileWriter(path));

                String line;

                while ((line = bufferedReader.readLine()) != null)
                {
                    printWriter.println(substitute(line));
                }
            }
            catch (IOException ioException)
            {
                System.err.println("Error outputting resource: " + path);
                ioException.printStackTrace();
            }
            finally
            {
                try
                {
                    bufferedReader.close();
                    printWriter.close();
                }
                catch (Exception exception)
                {
                }
            }
        }
        else
        {
            throw new BuildException("Missing resources", getLocation());
        }
    }

    private InputStream getInputStream()
    {
        InputStream inputStream = null;

        switch (template)
        {
            case CLIENT_SIDE_DETECTION:
            {
                if (history)
                {
                    inputStream = getClass().getResourceAsStream(TEMPLATE_DIR + templates[0] +
                                                                 WITH_HISTORY + INDEX_TEMPLATE_HTML);
                    outputResources(TEMPLATE_DIR + templates[0] + WITH_HISTORY, 
                                    new String[] {AC_OETAGS_JS, HISTORY_FRAME_HTML, HISTORY_JS, HISTORY_CSS});
                }
                else
                {
                    inputStream = getClass().getResourceAsStream(TEMPLATE_DIR + templates[0] + "/" +
                                                                 INDEX_TEMPLATE_HTML);
                    outputResources(TEMPLATE_DIR + templates[0] + "/", new String[] {AC_OETAGS_JS});
                }
                break;
            }
            case EXPRESS_INSTALLATION:
            default:
            {
                if (history)
                {
                    inputStream = getClass().getResourceAsStream(TEMPLATE_DIR + templates[1] +
                                                                 WITH_HISTORY + INDEX_TEMPLATE_HTML);
                    outputResources(TEMPLATE_DIR + templates[1] + WITH_HISTORY, 
                                    new String[] {AC_OETAGS_JS, HISTORY_FRAME_HTML, HISTORY_JS,
                                                  HISTORY_CSS, PLAYERPRODUCTINSTALL_SWF});
                }
                else
                {
                    inputStream = getClass().getResourceAsStream(TEMPLATE_DIR + templates[1] + "/" +
                                                                 INDEX_TEMPLATE_HTML);
                    outputResources(TEMPLATE_DIR + templates[1] + "/",
                                    new String[] {AC_OETAGS_JS, PLAYERPRODUCTINSTALL_SWF});
                }
                break;
            }
            case NO_PLAYER_DETECTION:
            {
                if (history)
                {
                    inputStream = getClass().getResourceAsStream(TEMPLATE_DIR + templates[2] +
                                                                 WITH_HISTORY + INDEX_TEMPLATE_HTML);
                    outputResources(TEMPLATE_DIR + templates[2] + WITH_HISTORY, 
                                    new String[] {AC_OETAGS_JS, HISTORY_FRAME_HTML, HISTORY_JS, HISTORY_CSS});
                }
                else
                {
                    inputStream = getClass().getResourceAsStream(TEMPLATE_DIR + templates[2] + "/" +
                                                                 INDEX_TEMPLATE_HTML);
                    outputResources(TEMPLATE_DIR + templates[2] + "/", new String[] {AC_OETAGS_JS});
                }
                break;
            }
        }

        return inputStream;
    }

    private void outputResources(String resourceDir, String[] resources)
    {
        BufferedReader bufferedReader = null;
        PrintWriter printWriter = null;

        for (int i = 0; i < resources.length; i++)
        {
            try
            {
                InputStream inputStream = getClass().getResourceAsStream(resourceDir + resources[i]);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String path = null;

                if (output != null)
                {
                    File outputDir = new File(output);
                    if (outputDir.exists() && outputDir.isDirectory())
                    {
                        path = output + File.separatorChar + resources[i];
                    }
                    else
                    {
                        String base = getProject().getBaseDir().getAbsolutePath();
                        outputDir = new File(base + File.separatorChar + output);
                        if (outputDir.exists() && outputDir.isDirectory())
                        {
                            path = base + File.separatorChar + output + File.separatorChar + resources[i];
                        }
                        else
                        {
                            throw new BuildException("output directory does not exist: " + output);
                        }
                    }
                }
                else
                {
                    path = resources[i];
                }

                File file = new File(path);
                file.getParentFile().mkdirs();

                printWriter = new PrintWriter(new FileWriter(file));
                
                String line;

                while ((line = bufferedReader.readLine()) != null)
                {
                    printWriter.println(line);
                }
            }
            catch (IOException ioException)
            {
                System.err.println("Error outputting resource: " + resources[i]);
                ioException.printStackTrace();
            }
            finally
            {
                try
                {
                    bufferedReader.close();
                    printWriter.close();
                }
                catch (Exception exception)
                {
                }
            }
        }
    }

    public void setApplication(String application)
    {
        this.application = application;
    }

    public void setBgcolor(String bgcolor)
    {
        this.bgcolor = bgcolor;
    }

    public void setDynamicAttribute(String name, String value)
    {
        if (name.equals("version-major"))
        {
            versionMajor = value;
        }
        else if (name.equals("version-minor"))
        {
            versionMinor = value;
        }
        else if (name.equals("version-revision"))
        {
            versionRevision = value;
        }
        else
        {
            throw new BuildException("The <html-wrapper> task doesn't support the \""
                                     + name + "\" attribute.", getLocation());
        }
    }

    public void setFile(String fileName)
    {
        this.fileName = fileName;
    }

    public void setHeight(String height)
    {
        this.height = height;
    }

    public void setHistory(boolean history)
    {
        this.history = history;
    }

    public void setOutput(String output)
    {
        this.output = output;
    }

    public void setSwf(String swf)
    {
        // Doctor up backslashes to fix bug 193739.
        this.swf = swf.replace('\\', '/');
        if (application == null)
        {
            application = this.swf;
        }
    }

    public void setTemplate(String template)
    {
        if (template.equals(templates[0]))
        {
            this.template = CLIENT_SIDE_DETECTION;
        }
        else if (template.equals(templates[1]))
        {
            this.template = EXPRESS_INSTALLATION;
        }
        else if (template.equals(templates[2]))
        {
            this.template = NO_PLAYER_DETECTION;
        }
        else
        {
            throw new BuildException("The 'template' attribute must be one of '" +
                                     templates[0] + "', '" +
                                     templates[1] + "', '" +
                                     templates[2] + "'.", getLocation());
        }
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setWidth(String width)
    {
        this.width = width;
    }

    private String substitute(String input)
    {
        String result = input.replaceAll("\\$\\{application\\}", application);
        result = result.replaceAll("\\$\\{bgcolor\\}", bgcolor);
        result = result.replaceAll("\\$\\{height\\}", height);
        result = result.replaceAll("\\$\\{swf\\}", swf);
        result = result.replaceAll("\\$\\{title\\}", title);
        result = result.replaceAll("\\$\\{version_major\\}", versionMajor);
        result = result.replaceAll("\\$\\{version_minor\\}", versionMinor);
        result = result.replaceAll("\\$\\{version_revision\\}", versionRevision);
        result = result.replaceAll("\\$\\{width\\}", width);
        return result;
    }
}
