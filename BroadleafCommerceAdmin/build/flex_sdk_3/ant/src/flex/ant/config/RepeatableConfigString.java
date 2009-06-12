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

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class RepeatableConfigString extends RepeatableConfigVariable
{
    private final ArrayList values;
    
    public RepeatableConfigString(OptionSpec spec)
    {
        super(spec);

        values = new ArrayList();
    }

    public void add(String value)
    {
        values.add(value);
    }

    public void addToCommandline(Commandline cmdl)
    {
       if (values.size() != 0)
            cmdl.createArgument().setValue("-" + spec.getFullName() + "=" + makeArgString());
    }

    private String makeArgString()
    {
        String arg = "";
        Iterator it = values.iterator();

        while (it.hasNext()) {
            arg += (String) it.next();
            arg += it.hasNext() ? "," : "";
        }

        return arg;
    }

} //End of RepeatableConfigString
