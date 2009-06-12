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

/**
 *
 */
public class OptionSpec
{
    private String prefix;
    private String name;
    private String alias;

    /**
     *
     */
    public OptionSpec(String name)
    {
        this.name = name;
    }

    /**
     *
     */
    public OptionSpec(String prefix, String name)
    {
        this.prefix = prefix;
        this.name = name;
    }

    /**
     *
     */
    public OptionSpec(String prefix, String name, String alias)
    {
        this.prefix = prefix;
        this.name = name;
        this.alias = alias;
    }

    /**
     *
     */
    public String getFullName()
    {
        String result;

        if (prefix != null)
        {
            result = prefix + "." + name;
        }
        else
        {
            result = name;
        }

        return result;
    }

    /**
     *
     */
    public String getName()
    {
        return name;
    }

    /**
     *
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     *
     */
    public String getAlias()
    {
        return alias;
    }

    /**
     *
     */
    public boolean matches(String option)
    {
        boolean result = false;

        if ((prefix != null) && option.equals(prefix + "." + name))
        {
            result = true;
        }
        else if (option.equals(name))
        {
            result = true;
        }
        else if ((alias != null) && option.equals(alias))
        {
            result = true;
        }

        return result;
    }

} //End of OptionSpec
