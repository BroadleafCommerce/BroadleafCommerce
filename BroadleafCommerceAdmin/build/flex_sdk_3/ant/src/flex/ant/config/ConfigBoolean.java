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
public final class ConfigBoolean extends ConfigVariable
{
    private boolean enabled;
    private boolean isSet;

    public ConfigBoolean(OptionSpec spec)
    {
        super(spec);

        this.enabled = false;
        this.isSet = false;
    }

    public ConfigBoolean(OptionSpec spec, boolean enabled)
    {
        super(spec);
        this.set(enabled);
    }

    public void set(boolean value)
    {
        this.enabled = value;
        this.isSet = true;
    }

    public void set(String value)
    {
        this.enabled = parseValue(value);
        this.isSet = true;
    }

    public boolean isSet() { return isSet; }

    public void addToCommandline(Commandline cmdl)
    {
        if (isSet)
            cmdl.createArgument(true).setValue("-" + spec.getFullName() + "=" + enabled);
    }

    private boolean parseValue(String value)
    {
        return value.toLowerCase().matches("\\s*(true|yes|on)\\s*");
    }
    
} //End of ConfigBoolean
