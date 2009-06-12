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

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.FlexInteger;

/**
 *
 */
public class ConfigInt extends ConfigVariable
{
    private int value;
    private boolean isSet;

    public ConfigInt(OptionSpec option)
    {
        super(option);
        this.isSet = false;
    }

    public ConfigInt(OptionSpec option, int value)
    {
        super(option);
        set(value);
    }

    public void set(int value)
    {
        this.value = value;
        this.isSet = true;
    }

    public void set(String value)
    {
        int intVal;

        try {
            intVal = new FlexInteger(value).intValue();
        } catch (NumberFormatException e) {
            throw new BuildException("Not an integer: " + value);
        }

        this.value = intVal;
        this.isSet = true;
    }

    public boolean isSet() { return isSet; }

    public void addToCommandline(Commandline cmdl)
    {
        if (this.isSet) {
            cmdl.createArgument().setValue("-" + spec.getFullName());
            cmdl.createArgument().setValue(String.valueOf(this.value));
        }
    }

} //End of ConfigInt
