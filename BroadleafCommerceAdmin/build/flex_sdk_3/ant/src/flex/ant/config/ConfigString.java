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

package flex.ant.config;

import org.apache.tools.ant.types.Commandline;

/**
 *
 */
public class ConfigString extends ConfigVariable
{
    private String value;

    public ConfigString(OptionSpec option)
    {
        this(option, null);
    }

    public ConfigString(OptionSpec option, String value)
    {
        super(option);
        this.set(value);
    }
    
    public void set(String value)
    {
        this.value = value;
    }

    public boolean isSet() { return value != null; }

    public String value() { return value; }

    public void addToCommandline(Commandline cmdl)
    {
        if (value != null)
        {
            if (value.length() > 0)
            {
                cmdl.createArgument().setValue("-" + spec.getFullName() + "=" + value);
            }
            else
            {
                cmdl.createArgument().setValue("-" + spec.getFullName() + "=");
            }
        }
    }

} //End of ConfigurationString
