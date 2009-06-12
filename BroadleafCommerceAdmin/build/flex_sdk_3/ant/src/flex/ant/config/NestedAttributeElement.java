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
import org.apache.tools.ant.DynamicAttribute;
import org.apache.tools.ant.types.Commandline;

/**
 *
 */
public class NestedAttributeElement implements DynamicAttribute, OptionSource
{
    private final static String COMMA = ",";

    private String[] attribs;
    private String[] values;
    private OptionSpec spec;
    private boolean valueHasComma;

    public NestedAttributeElement(String attrib, OptionSpec spec)
    {
        this(new String[] { attrib }, spec);
    }

    public NestedAttributeElement(String[] attribs, OptionSpec spec)
    {
        /*
         * Note: Do not try and be clever and sort attribs in order to increase
         * lookup time using binary search. The order of the attributes is
         * meaningful!
         */
        this.attribs = attribs;
        this.values = new String[attribs.length];
        this.spec = spec;
    }

    public void addText(String value)
    {
        values[0] = value;

        if (value.indexOf(COMMA) != -1)
        {
            valueHasComma = true;
        }
    }

    public void setDynamicAttribute(String name, String value)
    {
        boolean isSet = false;

        for (int i = 0; i < attribs.length && !isSet; i++) {
            if (attribs[i].equals(name)) {
                values[i] = value;
                isSet = true;
            }
        }

        if (value.indexOf(COMMA) != -1)
        {
            valueHasComma = true;
        }

        if (!isSet)
            throw new BuildException("The <" + spec.getFullName()
                                     + "> type doesn't support the \""
                                     + name + "\" attribute.");
    }

    public void addToCommandline(Commandline cmdl)
    {
        if (valueHasComma)
        {
            cmdl.createArgument().setValue("-" + spec.getFullName());

            for (int i = 0; i < attribs.length; i++)
            {
                if (values[i] != null)
                {
                    cmdl.createArgument().setValue(values[i].replaceAll("\\s*,\\s*", COMMA));
                }
            }
        }
        else
        {
            StringBuffer stringBuffer = new StringBuffer();

            for (int i = 0; i < attribs.length; i++)
            {
                if (values[i] != null)
                {
                    stringBuffer.append(values[i]);

                    if ((i + 1) < attribs.length)
                    {
                        stringBuffer.append(COMMA);
                    }
                }
            }
            
            cmdl.createArgument().setValue("-" + spec.getFullName() + "=" + stringBuffer);
        }
    }
} //End of NestedAttributeElement
