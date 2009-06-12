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

package flex.ant.types;

import flex.ant.config.OptionSource;
import flex.ant.config.OptionSpec;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

/**
 *
 */
public class DefaultSize implements OptionSource
{
    public static OptionSpec spec = new OptionSpec("default-size");

    private int width = -1;
    private int height = -1;

    public void setWidth(int val)
    {
        if (val <= 0)
            throw new BuildException("width attribute must be a positive integer!");

        width = val;
    }

    public void setHeight(int val)
    {
        if (val <= 0)
            throw new BuildException("height attribute must be a positive integer!");

        height = val;
    }

    public void addToCommandline(Commandline cmdl)
    {
        if (width == -1)
            throw new BuildException("width attribute must be set!");
        else if (height == -1)
            throw new BuildException("height attribute must be set!");
        else {
            cmdl.createArgument().setValue("-" + spec.getFullName());
            cmdl.createArgument().setValue(String.valueOf(width));
            cmdl.createArgument().setValue(String.valueOf(height));
        }
    }

} //End of DefaultSiz
