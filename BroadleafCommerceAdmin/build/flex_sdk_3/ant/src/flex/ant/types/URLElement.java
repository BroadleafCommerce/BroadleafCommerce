////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package flex.ant.types;

import flex.ant.config.OptionSource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.types.Commandline;

public class URLElement implements DynamicAttribute, OptionSource
{
    private static final String RSL_URL = "rsl-url";
    private static final String POLICY_FILE_URL = "policy-file-url";

    private String rslURL;
    private String policyFileURL;

    public void setDynamicAttribute(String name, String value)
    {
        if (name.equals(RSL_URL))
        {
            rslURL = value;
        }
        else if (name.equals(POLICY_FILE_URL))
        {
            policyFileURL = value;
        }
        else
        {
            throw new BuildException("The <url> type doesn't support the \"" +
                                     name + "\" attribute.");            
        }
    }

    public void addToCommandline(Commandline commandLine)
    {
        if (rslURL != null)
        {
            commandLine.createArgument().setValue(rslURL);
        }
        
        if (policyFileURL != null)
        {
            commandLine.createArgument().setValue(policyFileURL);
        }
    }
}