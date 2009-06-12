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
 * Provides a base class for Configuration Variables that can be set with a
 * String value.
 *
 * Consumers of this class must implement the <code>set</code> <code>isSet</code> methods.
 */
public abstract class ConfigVariable extends BaseConfigVariable
{
    /**
     * Create a <code>ConfigVariable</code> instance with the specified <code>OptionSpec</code>.
     */
    protected ConfigVariable(OptionSpec spec)
    {
        super(spec);
    }

    /**
     * Set the value of this <code>ConfigVariable</code>
     *
     * @param value the value (as a String) that this <code>ConfigVariable</code> should be set to.
     */
    public abstract void set(String value);

    /**
     * Predicate specifying whether this ConfigVariable has been set. Implementation depends on the implementation of <code>set</code>.
     *
     * @return true if this <code>ConfigVariable</code> has been set, false otherwise.
     */
    public abstract boolean isSet();

} //End of ConfigVariable

