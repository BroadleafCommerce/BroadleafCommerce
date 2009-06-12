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
 * Provides a base class for Configuration Variables that can take on multiple values.
 *
 * Consumers of this class must implement the <code>add</code> method.
 */
public abstract class RepeatableConfigVariable extends BaseConfigVariable
{
    /**
     * Creates a <code>RepeatableConfigVariable</code> instance with the specified <code>OpitonSpec</code>.
     */
    protected RepeatableConfigVariable(OptionSpec spec)
    {
        super(spec);
    }

    /**
     * Adds <code>value</code> as a value to this <code>RepeatableConfigVariable</code>.
     *
     * @param value the value to this <code>RepeatableConfigVariable</code>
     */
    public abstract void add(String value);

    /**
     * Adds every String in <code>values</code> as a value of this <code>RepeatableConfigVariable</code> by calling the <code>add</code> method with each String as an argument.
     * @param values an array of Strings
     */
    public void addAll(String[] values)
    {
        for (int i = 0; i < values.length; i++)
            this.add(values[i]);
    }

} //End of RepeatableConfigVariable
